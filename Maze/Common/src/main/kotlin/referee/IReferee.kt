package referee

import observer.IObserver
import player.IPlayer
import state.IState

/**
 * Represents a all-knowing referee of the game.
 */
interface IReferee {

    /**
     * Play a Labyrinth game.
     * @param players player APIs
     * @param gameState the initial game state
     * @param observers observers of this game
     * @return the winners and the misbehaved players.
     * @throws IllegalArgumentException if players is empty
     */
    @Throws(IllegalArgumentException::class)
    fun playGame(
        players: MutableList<IPlayer>,
        gameState: IState,
        observers: MutableList<IObserver>
    ): Pair<MutableList<IPlayer>, MutableList<IPlayer>>


}