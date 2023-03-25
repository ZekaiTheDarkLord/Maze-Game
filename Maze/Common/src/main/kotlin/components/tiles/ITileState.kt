package components.tiles

import kotlin.jvm.Throws

/**
 * Represents a tile that is read-only.
 */
interface ITileState {
    /**
     * Provides the directions of all paths in this Tile.
     * @return the directions of all paths on this tile
     * @throws IllegalArgumentException if the tile is empty
     */
    @Throws(IllegalArgumentException::class)
    fun getPathDirections(): Collection<TilePathDirection>

    /**
     * Gets the gem in this tile.
     * @return the gem in this tile
     * @throws IllegalArgumentException if the tile is empty
     */
    @Throws(IllegalArgumentException::class)
    fun getGems(): Array<Gem>

    /**
     * Deep copy this ITileState.
     * @return the deep copied object.
     */
    fun deepCopy(): ITileState

    /**
     * return a json of the tile
     */
    fun toJson(): String
}