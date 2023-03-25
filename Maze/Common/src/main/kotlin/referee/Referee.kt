package referee

import components.boards.Position
import components.boards.IBoard
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import observer.IObserver
import player.IPlayer
import player_referee_protocal.PlayerAction
import player_referee_protocal.PlayerPass
import player_referee_protocal.PlayerSIM
import state.IState
import state.PlayerGroundTruthData
import java.util.*
import kotlin.Comparator
import kotlin.math.sqrt

/**
 * An implementation of the game referee. Responsible for running the game,
 * identifying winners and cheaters.
 *
 * Illegal Player Actions:
 *      1. rotate the spare tile by a degree that is not a multiple of 90
 *      2. slide an invalid row/column index (odd or out of bound)
 *      3. attempt to move to an invalid position
 *      4. Player throws an exception
 */
open class Referee : IReferee {

    // We intend to properly set up this _gameState_ after we receive more details about
    // _proposeBoard0_ as that method provides a board, thereby allowing us to initialize
    // the game state.
    // private lateinit var gameState: IState
    private val MAX_ROUND_ALLOWED = 1000
    private var roundCount = 0
    private val MAX_TIME_FOR_MOVE_MS = 4000L

    // let each player take turn until game is complete.
    // returns the winners and cheaters
    override fun playGame(
        // INVARIANT: When iterating over _players_, must update the gameState.activePlayer using the
        // _nextPlayer_ method.
        playerAPIs: MutableList<IPlayer>,
        gameState: IState,
        observers: MutableList<IObserver>
    ): Pair<MutableList<IPlayer>, MutableList<IPlayer>> {

        determineIfGameCanBePlayed(playerAPIs)

        // Setup each IPlayer
        val cheaters = mutableListOf<IPlayer>()
        setupPlayer(playerAPIs, gameState, cheaters)
        removeCheatedPlayerFromPlayerAPI(cheaters, playerAPIs)

        val (roundData, players) = runRounds(playerAPIs, gameState, observers, cheaters)

        notifyObserversGameEnded(observers)
        val winners = roundData.winners.ifEmpty {
            determineWinners(players, gameState)
        }
        val (actualWinners, allCheaters) = informEachPlayerIfWon(winners, cheaters, players)
        return Pair(actualWinners.toMutableList(), allCheaters.toMutableList())
    }

    /*
    A game is completed if
        1. a player reaches its home after visiting its designated goal tile;
        2. all players that survive a round opt to pass; or
        3. the referee has run 1000 rounds.
     */
    private fun isGameOver(roundData: RoundData): Boolean {
        return roundData.allPlayersPass || roundData.winners.size > 0 || (roundCount >= MAX_ROUND_ALLOWED)
    }

    // Play a round of the game
    private fun playOneRound(
        players: MutableList<IPlayer>,
        gameState: IState,
        observers: MutableList<IObserver>
    ): RoundData {
        val roundData = RoundData()
        for (currentPlayer in players) {
            informObserversOfNewState(observers, gameState)
            // Protect the referee from malicious users
            val requestedAction = safelyRequestTurnFromPlayer(currentPlayer, gameState, roundData) ?: continue
            when (requestedAction) {
                is PlayerSIM -> {
                    // Anti-cheat check
                    if (playerCheated(requestedAction, gameState)) {
                        kickCheaterFromGame(currentPlayer, roundData.cheaters, gameState)
                        continue
                    }

                    // Perform action: rotate -> Slide&Insert -> Move
                    takeAction(requestedAction, gameState)
                    roundData.allPlayersPass = false

                    // Check if first visited treasure
                    val currentPlayerGroundTruth = gameState.getActivePlayerGroundTruth()
                    val reachTreasureForFirstTime =
                        gameState.activePlayerOnTreasure() && !currentPlayerGroundTruth.reachedTreasure
                    val reachedHomeWithTreasure =
                        gameState.activePlayerReturnedHome() && currentPlayerGroundTruth.reachedTreasure

                    if (reachTreasureForFirstTime) {
                        if (currentPlayerGroundTruth.homeTilePosition == currentPlayerGroundTruth.treasurePosition) {
                            roundData.winners.add(currentPlayer)
                            return roundData
                        } else {
                            handleReachedTreasure(
                                gameState,
                                currentPlayerGroundTruth,
                                currentPlayer,
                                roundData.cheaters
                            )
                        }
                    } else if (reachedHomeWithTreasure) {
                        roundData.winners.add(currentPlayer)
                        return roundData
                    }

                }

                is PlayerPass -> {
                    roundData.allPlayersPass = false || roundData.allPlayersPass
                }

                else -> {
                    kickCheaterFromGame(currentPlayer, roundData.cheaters, gameState)
                    continue
                }
            }

            // next player
            roundData.activePlayers.add(currentPlayer)
            gameState.nextPlayer()
        }
        return roundData
    }

    // Check if the player proposed action is valid. Returns false if isn't.
    /*
        Illegal Player Actions:
           1. rotate the spare tile by a degree that is not a multiple of 90
           2. slide an invalid row/column index (odd or out of bound)
           3. attempt to move to an invalid position
     */
    private fun playerCheated(action: PlayerSIM, gameState: IState): Boolean {
        val stateCopy = gameState.copyStateForStrategy()

        while (stateCopy.getActivePlayerGroundTruth() != gameState.getActivePlayerGroundTruth()) {
            stateCopy.nextPlayer()
        }

        return try {
            val positionBeforeMove = takeAction(action, stateCopy)
            val canNotReach =
                !stateCopy.getActivePlayerReachableTilesPositions().contains(positionBeforeMove)
            val isStay =
                positionBeforeMove == stateCopy.getActivePlayerGroundTruth().currentPosition
            (canNotReach || isStay)
        } catch (e: IllegalArgumentException) {
            true
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun takeAction(action: PlayerSIM, state: IState): Position {
        state.rotateSpare(action.rotateDegree)
        state.slideAndInsertSpare(
            rowOrColumnIndex = action.rowOrColumnIdx,
            direction = action.slideDir
        )
        val positionBeforeMove = state.getActivePlayerGroundTruth().currentPosition
        val positionBeforeMoveCopy =
            Position(positionBeforeMove.rowIndex, positionBeforeMove.columnIndex)
        state.moveActivePlayerTo(action.targetPos)
        return positionBeforeMoveCopy
    }

    // Determine the winners if the game is not ended by player reaching its home after visiting
    // its designated target tile.
    // Winners are determined with priorities as such:
    //   1. winners are all those players that are on their way back home and share the
    //   same minimal Euclidean distance to their home; or
    //   2. if no players are on their way back from their assigned goals, winners are all those
    //   players that share the same minimal Euclidean distance to their respective goal tile.
    private fun determineWinners(
        players: MutableList<IPlayer>,
        gameState: IState
    ): MutableList<IPlayer> {
        val winners = mutableListOf<IPlayer>()
        val potentialWinners: MutableList<Pair<IPlayer, Double>> = mutableListOf()
        val compareByEuclid: Comparator<Pair<IPlayer, Double>> = compareBy { it.second }
        // If any player is on their way back home, use rule 1
        players.forEach {
            val currentPlayerTruth = gameState.getActivePlayerGroundTruth()
            if (currentPlayerTruth.reachedTreasure) potentialWinners.add(
                Pair(
                    it,
                    getEuclidDistance(
                        currentPlayerTruth.currentPosition,
                        currentPlayerTruth.homeTilePosition
                    )
                )
            )
            gameState.nextPlayer()
        }
        if (potentialWinners.isEmpty()) {
            // If no player has reached their target tile, use rule 2
            players.forEach {
                val currentPlayerTruth = gameState.getActivePlayerGroundTruth()
                potentialWinners.add(
                    Pair(
                        it,
                        getEuclidDistance(
                            currentPlayerTruth.currentPosition,
                            currentPlayerTruth.treasurePosition
                        )
                    )
                )
                gameState.nextPlayer()
            }
        }

        // What if all players cheated and got kicked out?
        Collections.sort(potentialWinners, compareByEuclid)

        if (potentialWinners.isEmpty()) return mutableListOf()

        val minEuclidValue = potentialWinners[0].second
        for (pair in potentialWinners) {
            if (pair.second == minEuclidValue) {
                winners.add(pair.first)
            } else {
                break
            }
        }

        return winners
    }

    private fun getEuclidDistance(playerPosition: Position, tilePosition: Position): Double {
        val (tileRow, tileCol) = tilePosition
        val (playerRow, playerCol) = playerPosition
        return sqrt(
            StrictMath.pow((tileCol - playerCol).toDouble(), 2.0) +
                    StrictMath.pow((tileRow - playerRow).toDouble(), 2.0)
        )
    }

    private fun setupPlayer(players: MutableList<IPlayer>, gameState: IState, cheaters: MutableList<IPlayer>) {
        players.forEach {
            try {
                it.setup(
                    gameState.copyStateForActivePlayer(),
                    gameState.getActivePlayerGroundTruth().treasurePosition
                )
            } catch (e: Exception) {
                kickCheaterFromGame(it, cheaters, gameState)
            }
        }
    }

    private fun informObserversOfNewState(observers: MutableList<IObserver>, gameState: IState) {
        observers.forEach {
            val stateCopy = gameState.copyStateForStrategy()
            while (stateCopy.getActivePlayerGroundTruth() != gameState.getActivePlayerGroundTruth()) {
                stateCopy.nextPlayer()
            }
            it.receiveState(stateCopy)
        }
    }


    // Set up the initial game state
    private fun setupInitialGameState(): IState {
        // CALL _requestBoard_ here
        TODO("Will Implement in the next milestone")
    }

    // Request each player to propose the game board
    private fun requestBoard(): IBoard {
        TODO("Will Implement in the next milestone")
    }

    // returns list of actual winners and new cheaters, ones who did not misbehave in informWon
    private fun informEachPlayerIfWon(winners: List<IPlayer>, cheaters: List<IPlayer>, players: MutableList<IPlayer>):
            Pair<List<IPlayer>, List<IPlayer>> {
        val listOfActualWinners = mutableListOf<IPlayer>()
        val newCheaters = cheaters.toMutableList()

        winners.forEach {
            try {
                it.informWon(true)
                listOfActualWinners.add(it)
            } catch (e: Exception) {
                newCheaters.add(it)
                players.remove(it)
            }
        }

        val losers = players - winners.toSet()
        losers.forEach {
            try {
                it.informWon(false)
            } catch (e: Exception) {
                newCheaters.add(it)
                players.remove(it)
            }
        }
        return Pair(listOfActualWinners, newCheaters)
    }

    private fun removeCheatedPlayerFromPlayerAPI(
        cheaters: MutableList<IPlayer>,
        players: MutableList<IPlayer>
    ): MutableList<IPlayer> {
        cheaters.forEach {
            players.remove(it)
        }

        return players
    }

    private fun runRounds(
        playerAPIs: MutableList<IPlayer>, gameState: IState, observers: MutableList<IObserver>,
        cheaters: MutableList<IPlayer>
    ): Pair<RoundData, MutableList<IPlayer>> {
        var roundData = RoundData()
//        var players = playerAPIs

        // Take turn until game is complete
        // Each iteration is a round of game
        while (!isGameOver(roundData)) {
            roundData = playOneRound(playerAPIs, gameState, observers)
            playerAPIs.clear()
            playerAPIs.addAll(roundData.activePlayers)
            cheaters.addAll(roundData.cheaters)
            removeCheatedPlayerFromPlayerAPI(roundData.cheaters, playerAPIs)
            roundCount++
        }
        return Pair(roundData, playerAPIs)
    }

    private fun notifyObserversGameEnded(observers: MutableList<IObserver>) {
        observers.forEach {
            it.gameEnds()
        }
    }

    private fun determineIfGameCanBePlayed(playerAPIs: MutableList<IPlayer>) {
        if (playerAPIs.size == 0) throw IllegalArgumentException("players cannot be empty.")
    }

    private fun safelyRequestTurnFromPlayer(
        currentPlayer: IPlayer,
        gameState: IState,
        roundData: RoundData
    ): PlayerAction? {
        return try {
            runBlocking {
                withTimeout(MAX_TIME_FOR_MOVE_MS) {
                    currentPlayer.takeTurn(gameState.copyStateForActivePlayer())
                }
            }
        } catch (e: Exception) {
            kickCheaterFromGame(currentPlayer, roundData.cheaters, gameState)
            null
        }
    }

    private fun kickCheaterFromGame(playerAPI: IPlayer, cheaters: MutableList<IPlayer>, gameState: IState) {
        cheaters.add(playerAPI)
        gameState.activePlayerKick()
    }

    private fun handleReachedTreasure(
        gameState: IState, currentPlayerGroundTruth: PlayerGroundTruthData,
        currentPlayer: IPlayer, cheaters: MutableList<IPlayer>
    ) {
        gameState.updateActivePlayerReachedTreasure()
        currentPlayerGroundTruth.currentTarget =
            currentPlayerGroundTruth.homeTilePosition.deepCopy()
        try {
            currentPlayer.setup(null, currentPlayerGroundTruth.homeTilePosition.deepCopy())
        } catch (e: Exception) {
            kickCheaterFromGame(currentPlayer, cheaters, gameState)
        }
    }
}