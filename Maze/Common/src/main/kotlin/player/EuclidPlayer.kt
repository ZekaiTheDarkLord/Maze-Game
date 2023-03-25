package player

import components.boards.Position
import player_referee_protocal.PlayerAction
import state.IState
import strategy.player.Euclid
import strategy.player.Riemann

/**
 * An player which uses euclid algorithm to decide his moves
 */
open class EuclidPlayer(override var name: String) : AbstractPlayer() {
    @Throws(IllegalArgumentException::class)
    override fun takeTurn(state: IState): PlayerAction {
        return takeTurnAbstract(state) { state: IState, targetPos: Position -> Euclid(state, targetPos) }
    }
}