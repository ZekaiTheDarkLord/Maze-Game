package components.tiles

/**
 * Represents a Tile in the Labyrinth game.
 */
interface ITileModel : ITileState {

    /**
     * Rotate this tile by the input degree.
     * @param totalRotationDegree the degree of this rotation
     * @throws IllegalArgumentException if the input degree is not a multiple of 90 or the tile is
     *                                  an empty tile.
     */
    @Throws(IllegalArgumentException::class)
    fun rotate(totalRotationDegree: Int)

}