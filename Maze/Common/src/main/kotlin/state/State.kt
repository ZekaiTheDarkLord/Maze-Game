package state

import com.google.gson.Gson
import com.google.gson.JsonObject
import components.boards.Board
import components.boards.Position
import components.boards.SlideDirection
import components.boards.IBoard
import components.tiles.ITileState
import java.awt.Color

/**
 * Represents the game state of the Labyrinth game.
 * @param board: the board of the current game
 * @param players: the player list of the game which saves the player according to the round order
 * @throws IllegalArgumentException if no players are provided.
 */
open class State @Throws(IllegalArgumentException::class) constructor(
    private val board: IBoard,
    private val players: MutableList<PlayerGroundTruthData>,
    internal open var lastSlide: Pair<Int, SlideDirection> = Pair(-1, SlideDirection.LEFT),
    private var activePlayer: PlayerGroundTruthData
) : IState {

    init {
        if (players.isEmpty()) throw IllegalArgumentException("Needs to have at least 1 player")
        if (activePlayer !in players) throw IllegalArgumentException("Active player should be in the players list.")
        if (!doPlayersHaveDistinctHomes(players)) throw java.lang.IllegalArgumentException("Players need distinct homes")
    }

    override fun nextPlayer() {
        val activePlayerIndex = players.indexOf(activePlayer)

        activePlayer = if (activePlayerIndex >= players.size - 1) {
            players[0]
        } else {
            players[activePlayerIndex + 1]
        }
    }

    override fun getActivePlayerGroundTruth(): PlayerGroundTruthData {
        return PlayerGroundTruthData(
            name = activePlayer.name,
            homeTilePosition = activePlayer.homeTilePosition,
            reachedTreasure = activePlayer.reachedTreasure,
            currentPosition = activePlayer.currentPosition,
            treasurePosition = activePlayer.treasurePosition,
            currentTarget = activePlayer.currentTarget,
            avatar = activePlayer.avatar
        )
    }

    override fun getBoardRowSize(): Int {
        return board.getDimension().first
    }

    override fun getBoardColumnSize(): Int {
        return board.getDimension().second
    }

    @Throws(IllegalArgumentException::class)
    override fun rotateSpare(degree: Int) {
        board.rotateSpare(degree)
    }

    @Throws(IllegalArgumentException::class)
    override fun slideAndInsertSpare(rowOrColumnIndex: Int, direction: SlideDirection) {
        if (rowOrColumnIndex == lastSlide.first &&
            direction == SlideDirection.getReverseDirection(lastSlide.second)
        ) {
            throw IllegalArgumentException("Cannot undo the previous sliding action")
        }
        if (direction == SlideDirection.LEFT || direction == SlideDirection.RIGHT) {
            horizontalSlideAndInsertSpare(rowOrColumnIndex, direction)
        } else {
            verticalSlideAndInsertSpare(rowOrColumnIndex, direction)
        }
        lastSlide = Pair(rowOrColumnIndex, direction)
    }

    @Throws(IllegalArgumentException::class)
    fun horizontalSlideAndInsertSpare(rowIndex: Int, direction: SlideDirection) {
        if (rowIndex < 0 || rowIndex >= board.getDimension().second) {
            throw IllegalArgumentException("rowIndex out of bound! Given $rowIndex")
        }
        val targetColIdx =
            if (direction == SlideDirection.RIGHT) board.getDimension().second - 1 else 0
        updatePlayerTruthData(
            oldEdgePosition = Position(rowIndex, targetColIdx),
            newEdgePosition = Position(rowIndex, (board.getDimension().second - 1) - targetColIdx),
            slideDirection = direction
        )
        board.slideAndInsert(rowIndex, direction)
    }

    @Throws(IllegalArgumentException::class)
    fun verticalSlideAndInsertSpare(columnIndex: Int, direction: SlideDirection) {
        if (columnIndex < 0 || columnIndex >= board.getDimension().first) {
            throw IllegalArgumentException("columnIndex out of bound! Given $columnIndex")
        }
        val targetRowIdx = if (direction == SlideDirection.UP) 0 else board.getDimension().first - 1
        updatePlayerTruthData(
            oldEdgePosition = Position(targetRowIdx, columnIndex),
            newEdgePosition = Position(
                (board.getDimension().first - 1) - targetRowIdx,
                columnIndex
            ),
            slideDirection = direction
        )
        board.slideAndInsert(columnIndex, direction)
    }

    override fun getActivePlayerReachableTilesPositions(): List<Position> {
        return board.reachableFrom(activePlayer.currentPosition)
    }

    override fun setActivePlayerCurrentTarget(pos: Position) {
        activePlayer.currentTarget = pos
    }

    //TODO: should be in player (IState) -> IState
    override fun copyStateForActivePlayer(): IState {
        val players = mutableListOf(
            PlayerGroundTruthData(
                name = activePlayer.name,
                homeTilePosition = activePlayer.homeTilePosition.deepCopy(),
                reachedTreasure = activePlayer.reachedTreasure,
                currentPosition = activePlayer.currentPosition.deepCopy(),
                treasurePosition = activePlayer.treasurePosition.deepCopy(),
                currentTarget = activePlayer.currentTarget.deepCopy(),
                avatar = activePlayer.avatar
            )
        )
        return State(
            board = board.deepCopy(),
            players = players,
            lastSlide = Pair(lastSlide.first, lastSlide.second),
            activePlayer = players[0]
        )
    }

    //TODO: should be in strategy  (IState) -> IState
    override fun copyStateForStrategy(): IState {

        val playersCopy = mutableListOf<PlayerGroundTruthData>()
        players.forEach {
            playersCopy.add(
                PlayerGroundTruthData(
                    name = it.name,
                    homeTilePosition = it.homeTilePosition,
                    reachedTreasure = it.reachedTreasure,
                    currentPosition = it.currentPosition,
                    treasurePosition = it.treasurePosition,
                    currentTarget = it.currentTarget,
                    avatar = it.avatar
                )
            )
        }
        return State(
            board = board.deepCopy(),
            players = playersCopy,
            lastSlide = Pair(lastSlide.first, lastSlide.second),
            activePlayer = playersCopy[0]
        )
    }

    override fun getTileAt(pos: Position): ITileState {
        return this.board.getTileAt(pos)
    }

    override fun getSpareTile(): ITileState {
        return this.board.getSpare()
    }

    override fun getAllPlayerGroundTruthData(): List<PlayerGroundTruthData> {
        val result = mutableListOf<PlayerGroundTruthData>()
        players.forEach {
            result.add(
                PlayerGroundTruthData(
                    name = it.name,
                    homeTilePosition = it.homeTilePosition,
                    reachedTreasure = it.reachedTreasure,
                    currentPosition = it.currentPosition,
                    treasurePosition = it.treasurePosition,
                    currentTarget = it.currentTarget,
                    avatar = it.avatar
                )
            )
        }
        return result
    }

    override fun areTilesConnected(pos1: Position, pos2: Position): Boolean {
        return board.reachableFrom(pos1).contains(pos2)
    }

    override fun getLastSlide(): Pair<Int, SlideDirection> {
        return Pair(lastSlide.first, lastSlide.second)
    }

    override fun updateActivePlayerReachedTreasure() {
        activePlayer.reachedTreasure = true
    }

    override fun activePlayerOnTreasure(): Boolean {
        return activePlayer.currentPosition == activePlayer.treasurePosition
    }

    override fun activePlayerReturnedHome(): Boolean {
        if (activePlayer.reachedTreasure) return activePlayer.currentPosition == activePlayer.homeTilePosition
        return false
    }

    override fun activePlayerKick() {
        val toBeKicked = activePlayer
        nextPlayer()
        players.remove(toBeKicked)
    }

    override fun moveActivePlayerTo(position: Position) {
        if (position.rowIndex >= board.getDimension().first ||
            position.columnIndex >= board.getDimension().second ||
            position.rowIndex < 0 ||
            position.columnIndex < 0
        ) {
            throw IllegalArgumentException("Cannot move the player out of the board.")
        }
        activePlayer.currentPosition = position
    }

    override fun toJson(): String {
        val lastSlideString = Pair(lastSlide.first, lastSlide.second.slideDirectionString)
        val playerJsonList = players.map { it.toJson() }.joinToString(prefix = "[", postfix = "]", separator = ", ")
        val lastSlideJson = Gson().toJson(lastSlideString)

        val returnJson = """{
            |"board": ${board.toJson()}, 
            |"spare": "${getSpareTile().toJson()}", 
            |"plmt" : $playerJsonList,
            |"last" : $lastSlideJson}""".trimMargin()

        return returnJson
    }


    // Move all players impacted by a slide to a new tile
    private fun updatePlayerTruthData(
        oldEdgePosition: Position,
        newEdgePosition: Position,
        slideDirection: SlideDirection
    ) {
        for (it in players) {

            updatePlayerCurrentPosition(
                oldEdgePosition = oldEdgePosition,
                newEdgePosition = newEdgePosition,
                slideDirection = slideDirection,
                player = it
            )

            updatePlayerHomeOrTargetPosition(
                oldEdgePosition = oldEdgePosition,
                newEdgePosition = newEdgePosition,
                slideDirection = slideDirection,
                player = it,
                currentTilePosition = it.homeTilePosition,
                setter = { pos: Position -> it.homeTilePosition = pos }
            )

            updatePlayerHomeOrTargetPosition(
                oldEdgePosition = oldEdgePosition,
                newEdgePosition = newEdgePosition,
                slideDirection = slideDirection,
                player = it,
                currentTilePosition = it.treasurePosition,
                setter = { pos: Position -> it.treasurePosition = pos }
            )

            updatePlayerHomeOrTargetPosition(
                oldEdgePosition = oldEdgePosition,
                newEdgePosition = newEdgePosition,
                slideDirection = slideDirection,
                player = it,
                currentTilePosition = it.currentTarget,
                setter = { pos: Position -> it.currentTarget = pos }
            )

        }
    }

    // Updates a player's current position.
    private fun updatePlayerCurrentPosition(
        oldEdgePosition: Position,
        newEdgePosition: Position,
        slideDirection: SlideDirection,
        player: PlayerGroundTruthData
    ) {
        val playerCurrentPosition = player.currentPosition
        if (player.currentPosition == oldEdgePosition) {
            player.currentPosition = newEdgePosition
        } else {

            if ((slideDirection == SlideDirection.LEFT || slideDirection == SlideDirection.RIGHT) &&
                player.currentPosition.rowIndex != oldEdgePosition.rowIndex
            ) return
            else if ((slideDirection == SlideDirection.UP || slideDirection == SlideDirection.DOWN) &&
                player.currentPosition.columnIndex != oldEdgePosition.columnIndex
            ) return

            when (slideDirection) {
                SlideDirection.LEFT -> {
                    player.currentPosition = Position(
                        playerCurrentPosition.rowIndex,
                        playerCurrentPosition.columnIndex - 1
                    )
                }

                SlideDirection.RIGHT -> {
                    player.currentPosition = Position(
                        playerCurrentPosition.rowIndex,
                        playerCurrentPosition.columnIndex + 1
                    )
                }

                SlideDirection.UP -> {
                    player.currentPosition = Position(
                        playerCurrentPosition.rowIndex - 1,
                        playerCurrentPosition.columnIndex
                    )
                }

                SlideDirection.DOWN -> {
                    player.currentPosition = Position(
                        playerCurrentPosition.rowIndex + 1,
                        playerCurrentPosition.columnIndex
                    )
                }
            }
        }
    }

    private fun updatePlayerHomeOrTargetPosition(
        oldEdgePosition: Position,
        newEdgePosition: Position,
        slideDirection: SlideDirection,
        player: PlayerGroundTruthData,
        currentTilePosition: Position,
        setter: (input: Position) -> Unit
    ) {

        // Position is spare tile
        when (currentTilePosition) {
            Position(-1, -1) -> {
                setter(newEdgePosition)
                return
            }

            // Position is about to become spare tile
            oldEdgePosition -> {
                setter(Position(-1, -1))
                return
            }
        }

        if ((slideDirection == SlideDirection.LEFT || slideDirection == SlideDirection.RIGHT) &&
            currentTilePosition.rowIndex != oldEdgePosition.rowIndex
        ) return
        else if ((slideDirection == SlideDirection.UP || slideDirection == SlideDirection.DOWN) &&
            currentTilePosition.columnIndex != oldEdgePosition.columnIndex
        ) return

        when (slideDirection) {
            SlideDirection.LEFT -> {
                setter(
                    Position(
                        currentTilePosition.rowIndex,
                        currentTilePosition.columnIndex - 1
                    )
                )
            }

            SlideDirection.RIGHT -> {
                setter(
                    Position(
                        currentTilePosition.rowIndex,
                        currentTilePosition.columnIndex + 1
                    )
                )
            }

            SlideDirection.UP -> {
                setter(
                    Position(
                        currentTilePosition.rowIndex - 1,
                        currentTilePosition.columnIndex
                    )
                )
            }

            SlideDirection.DOWN -> {
                setter(
                    Position(
                        currentTilePosition.rowIndex + 1,
                        currentTilePosition.columnIndex
                    )
                )
            }
        }
    }

    // returns whether players have distinct home positions from another
    private fun doPlayersHaveDistinctHomes(players: MutableList<PlayerGroundTruthData>): Boolean {
        val setOfPlayerHomes = players.map { player -> player.homeTilePosition }.toSet()
        return setOfPlayerHomes.size == players.size
    }

    companion object {
        fun fromJson(jsonObj: JsonObject): State {
            val board = Board.fromJson(jsonObj.get("board"), jsonObj.get("spare"))
            val listOfPlayerTruthData = jsonObj.get("plmt").asJsonArray.map {
                PlayerGroundTruthData.fromJson(it.asJsonObject)
            }
            return State(board, listOfPlayerTruthData.toMutableList(), activePlayer = listOfPlayerTruthData[0])
        }
    }

}


