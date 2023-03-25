package components.tiles

/**
 * Represents the direction of an open path in a Tile.
 */
enum class TilePathDirection {
    UP, DOWN, LEFT, RIGHT;


    companion object {
        fun parseDegreeToDirection(degree: Int): Int {
            return when(degree) {
                0 -> 0
                90 -> 3
                180 -> 2
                270 -> 1
                else -> throw IllegalArgumentException("Invalid Degree given")
            }
        }
    }
}

