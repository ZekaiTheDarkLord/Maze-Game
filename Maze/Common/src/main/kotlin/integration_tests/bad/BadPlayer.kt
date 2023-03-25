package integration_tests.bad

import components.boards.IBoard
import components.boards.Position
import player.AbstractPlayer
import player.IPlayer
import player_referee_protocal.PlayerAction
import state.IState
import strategy.player.Euclid
import strategy.player.Riemann
import java.lang.IllegalArgumentException

class BadPlayer(override var name: String, val strategy: String, val badFM: String, val badCount: Int): AbstractPlayer() {
    var termCount = 1

    init {
        if (badCount < 1 || badCount > 7) {
            throw IllegalArgumentException("The bad count is out of the bound! Given: $badCount")
        }
    }

    override fun setup(state0: IState?, goal: Position) {
        throwExceptionOrPass(PlayerCommand.SETUP)
        super.setup(state0, goal)
    }

    override fun takeTurn(s: IState): PlayerAction {
        throwExceptionOrPass(PlayerCommand.TAKETURN)
        val strategyFactory =
            when (strategy) {
                "Euclid" ->  { state:IState, targetPos:Position -> Euclid(state, targetPos) }
                "Riemann" ->  { state:IState, targetPos:Position -> Riemann(state, targetPos)}
                else -> throw IllegalArgumentException("Unsupported strategy: $strategy")
            }
        return super.takeTurnAbstract(s, strategyFactory)
    }

    override fun informWon(w: Boolean) {
        throwExceptionOrPass(PlayerCommand.WIN)
        return super.informWon(w)
    }

    private fun throwExceptionOrPass(givenAction: PlayerCommand) {
        if (indicatedRound(givenAction)) {
            errorFunc()
        }
    }
    
    private fun indicatedRound(givenAction: PlayerCommand): Boolean {
        return if (badFM == givenAction.commandString) {
            if  (termCount == badCount) {
                true
            } else {
                termCount++
                false
            }
        } else {
            false
        }
    }

    private fun errorFunc() {
        1 / 0
    }
}