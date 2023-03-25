package components.tiles

import java.lang.IllegalArgumentException

/**
 * Represents an empty tile.
 */
class EmptyTile: ITileModel {
    override fun rotate(totalRotationDegree: Int) {
        throw IllegalArgumentException("Tile is empty.")
    }

    override fun getPathDirections(): Collection<TilePathDirection> {
        throw IllegalArgumentException("Tile is empty.")
    }

    override fun getGems(): Array<Gem> {
        throw IllegalArgumentException("Tile is empty.")
    }

    override fun deepCopy(): ITileState {
        return EmptyTile()
    }

    override fun toJson(): String {
        return ""
    }

    override fun equals(other: Any?): Boolean {
        return other is EmptyTile
    }

}