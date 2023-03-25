package state

import components.boards.Position
import components.boards.SlideDirection
import components.tiles.ITileState
import kotlin.IllegalArgumentException

/**
 * Represents a referee that has knowledge on the state of the game.
 */
interface IState {

    /**
     * get the next player on the player list
     */
    fun nextPlayer()

    /**
     * Get the ground truth data of the current active player.
     * @return the ground truth data of the active player
     */
    fun getActivePlayerGroundTruth(): PlayerGroundTruthData

    /**
     * Get the row size of the board
     * @return the row size of the board
     */
    fun getBoardRowSize(): Int

    /**
     * get the column size of the board
     * @return the column size of the board
     */
    fun getBoardColumnSize(): Int

    /**
     * Rotate the old spare tile by the input degree.
     * @param degree the degree to rotate the spare tile
     * @throws IllegalArgumentException if the input degree is not a multiple of 90
     */
    @Throws(IllegalArgumentException::class)
    fun rotateSpare(degree: Int)

    /**
     * Slide a row horizontally or a column vertically and insert the spare tile based on the
     * direction of the slide. If a player is on the tile to be removed by the slide,
     * move it to the newly inserted tile. Shift all other players on the corresponding row/column
     * accordingly.
     * @param rowOrColumnIndex the index of the row/column to slide
     * @param direction the direction of the slide
     * @throws IllegalArgumentException if rowOrColumnIndex < 0, rowOrColumnIndex >= board length, or rowIndex is
     * odd.
     *
     */
    @Throws(IllegalArgumentException::class)
    fun slideAndInsertSpare(rowOrColumnIndex: Int, direction: SlideDirection)

    /**
     * Determine if the current active player's move placed it on the treasure tile.
     * @return if the current active player's move placed it on the active tile.
     */
    fun activePlayerOnTreasure(): Boolean

    /**
     * Determine if the current active player's move placed it on the home tile after reaching the treasure.
     * @return if the current active player's move placed it on the active tile.
     */
    fun activePlayerReturnedHome(): Boolean

    /**
     * Kick the current active player out of the game.
     */
    fun activePlayerKick()

    /**
     * Move the active player to a specific position.
     */
    @Throws(IllegalArgumentException::class)
    fun moveActivePlayerTo(position: Position)

    /**
     * Get all the tiles reachable by the active player.
     */
    fun getActivePlayerReachableTilesPositions(): List<Position>

    /**
     * Set the current active player's current target
     */
    fun setActivePlayerCurrentTarget(pos: Position)

    /**
     * Make a deep copy of the game state for the active player. The copy should not include
     * data of other players.
     */
    fun copyStateForActivePlayer(): IState

    /**
     * Provides a deep copy of this state.
     */
    fun copyStateForStrategy(): IState

    /**
     * Get a specific tile in a specified position.
     */
    fun getTileAt(pos: Position): ITileState

    /**
     * Get the spare tile.
     */
    fun getSpareTile(): ITileState

    /**
     * Get a copy of all players' true data.
     */
    fun getAllPlayerGroundTruthData(): List<PlayerGroundTruthData>

    /**
     * Check if two tiles are connected.
     * @param pos1 the position of the first tile
     * @param pos2 the position of the second tile
     * @return if two tiles are connected
     */
    fun areTilesConnected(pos1: Position, pos2: Position): Boolean

    /**
     * Get the last slide made by a player.
     */
    fun getLastSlide(): Pair<Int, SlideDirection>

    /**
     * Update the reachedTreasure data of the active player.
     */
    fun updateActivePlayerReachedTreasure();

    fun toJson(): String
}