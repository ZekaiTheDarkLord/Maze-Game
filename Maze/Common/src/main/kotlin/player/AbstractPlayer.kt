package player

import components.boards.Position
import components.boards.IBoard
import player_referee_protocal.PlayerAction
import state.IState
import strategy.board.SimpleGenerationStrategy
import strategy.player.Euclid
import strategy.player.Riemann
import strategy.player.Strategy
import java.util.*

/**
 * An abstract class of player which has several methods that all kinds of players can use
 */
abstract class AbstractPlayer : IPlayer {
    abstract var name: String
    private var isWon = false
    private lateinit var playerTarget: Position
    private lateinit var playerHome: Position
    private lateinit var currentTarget: Position
    private var hasSetUp = false
    protected var currentTargetType = PlayerTargetType.GO_TARGET

    override fun proposeBoard0(rows: Int, columns: Int): IBoard {
        return SimpleGenerationStrategy().execute(rows, columns, Random(1))
    }

    // is private goal same as the final target
    @Throws
    override fun setup(state0: IState?, goal: Position) {
        if (state0 == null) {
            if (!hasSetUp) throw IllegalStateException("The player hasn't set up yet!")

            currentTargetType = PlayerTargetType.GO_HOME
            currentTarget = goal
        } else {
            playerTarget = state0.getActivePlayerGroundTruth().treasurePosition
            playerHome = state0.getActivePlayerGroundTruth().homeTilePosition
            currentTarget = goal
            hasSetUp = true
        }
    }

    abstract override fun takeTurn(s: IState): PlayerAction

    override fun informWon(w: Boolean) {
        isWon = w
    }

    override fun getPlayerName(): String {
        return name
    }

    @Throws(IllegalArgumentException::class)
    protected fun takeTurnAbstract(state: IState, strategyFactory: (state:IState, targetPosition: Position) ->
    Strategy): PlayerAction {
        val playerTargetPos: Position
        val strategy: Strategy

        if (currentTargetType == PlayerTargetType.GO_TARGET) {
            playerTargetPos = state.getActivePlayerGroundTruth().treasurePosition
        } else if (currentTargetType == PlayerTargetType.GO_HOME) {
            playerTargetPos = state.getActivePlayerGroundTruth().homeTilePosition
        } else {
            throw IllegalArgumentException("Wrong type!")
        }

        strategy = strategyFactory(state, playerTargetPos)
        return strategy.execute()
    }

}