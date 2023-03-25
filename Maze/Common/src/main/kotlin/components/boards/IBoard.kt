package components.boards

import components.tiles.ITileState
import kotlin.jvm.Throws

/**
 * Represents a game board of the Labyrinth game.
 */
interface IBoard {

    /**
     * Sliding a designated row or column .
     * @param slideIndex the index of the row to be slided
     * @param slideDirection the direction of the slide
     * @throws IllegalArgumentException if slideIndex < 0, slideIndex >= corresponding board size,
     * or slideIndex is odd.
     */
    @Throws(IllegalArgumentException::class)
    fun slideAndInsert(slideIndex: Int, slideDirection: SlideDirection)

    /**
     * Provides the tiles that are reachable to a given tile. Insert a given tile to the slot left open by the slide.
     * @param position the position of the tile we would like to find all its reachable tiles
     * @return a list of tile position that share at least a path to the fromTile.
     * @throws IllegalArgumentException if the tile is not on the board or is empty
     */
    @Throws( IllegalArgumentException::class)
    fun reachableFrom(position: Position): List<Position>

    /**
     * Get a tile on a specific position of the board
     * @param position the position of the target tile
     * @return the tile on the corresponding position
     * @throws IllegalArgumentException if either rowIndex or colIndex is out of bound
     */
    @Throws(IllegalArgumentException::class)
    fun getTileAt(position: Position): ITileState

    /**
     * Provides the dimension of the board.
     * @return a pair representing the number of rows and number of columns in the board
     */
    fun getDimension(): Pair<Int, Int>

    /**
     * Provides the deep copied spare tile
     */
    fun getSpare(): ITileState

    /**
     * Rotate the old spare tile by the input degree.
     * @param degree the degree to rotate the spare tile
     * @throws IllegalArgumentException if the input degree is not a multiple of 90
     */
    fun rotateSpare(degree: Int)

    fun deepCopy(): IBoard

    fun toJson(): String
    fun isSlideable(slideIndex: Int, upperBound: Int)
}