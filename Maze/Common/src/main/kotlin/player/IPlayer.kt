package player

import components.boards.Position
import components.boards.IBoard
import player_referee_protocal.PlayerAction
import state.IState

/**
 * Representing a player and its corresponding functionalities
 */
interface IPlayer {
    /**
     * the player may be asked to propose a game board that comes wtih a min number of rows
     * and columns
     * @param rows: the number of rows the player wants to propose
     * @param columns: the number of columns the player wants to propose
     * @return a proposed board that may be chosen bt the referee
     */
    fun proposeBoard0(rows: Int, columns: Int): IBoard

    /**
     * the player is handed the initial state, which is visible to all plus a (private) goal
     * that it must visit next.
     *
     * If state is NONE, setup is used to tell the player go-home
     * @param state0: the initial state passed to player
     * @param goal: the private goal the player must visit next
     * @throws IllegalStateException: When the player is been set up for second time and not set up
     * for the initial target
     */
    @Throws(IllegalStateException::class)
    fun setup(state0: IState?, goal: Position)

    /**
     * after receiving the state, a player passes on taking action or picks
     * -- a row or column index and a direction,
     * -- a degree of rotation for the spare,
     * -- a new place to move to.
     * @param: the state given to player
     */
    @Throws(IllegalArgumentException::class)
    fun takeTurn(s: IState): PlayerAction

    /**
     * the player is informed whether it won or not
     */
    fun informWon(w: Boolean)

    /**
     * Get the name of the player
     */
    fun getPlayerName(): String
}