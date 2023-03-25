package observer

import state.IState

/**
 * An observer that displays the state of the game.
 */
interface IObserver {

    /**
     * Provides a new state available for the observer to display.
     */
    fun receiveState(newState: IState)

    /**
     * Alerts the observer that the game has ended. No more new states will be available.
     */
    fun gameEnds()

}