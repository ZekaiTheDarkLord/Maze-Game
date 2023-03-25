package player

import components.boards.Position
import player_referee_protocal.PlayerAction
import state.IState
import strategy.player.Riemann

/**
 * A kinds of player which uses riemann to decide its moves.
 */
open class RiemannPlayer(override var name: String) : AbstractPlayer() {
    @Throws(IllegalArgumentException::class)
    override fun takeTurn(state: IState): PlayerAction {
        return takeTurnAbstract(state) { state:IState, targetPos:Position -> Riemann(state, targetPos)}
    }
}