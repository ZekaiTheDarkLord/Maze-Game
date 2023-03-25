package client

import components.boards.Position
import player.AbstractPlayer
import player_referee_protocal.PlayerAction
import state.IState
import strategy.player.Euclid

class RemotePlayer(override var name: String) :  AbstractPlayer() {
    @Throws(IllegalArgumentException::class)
    override fun takeTurn(state: IState): PlayerAction {
        return takeTurnAbstract(state) { state: IState, targetPos: Position -> Euclid(state, targetPos) }
    }
}