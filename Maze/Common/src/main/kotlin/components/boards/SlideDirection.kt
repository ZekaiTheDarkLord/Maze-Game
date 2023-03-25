package components.boards

/**
 * Represents the direction of a slide to the column on the game board.
 */
enum class SlideDirection(val slideDirectionString: String) {
    UP("UP"),
    DOWN("DOWN"),
    LEFT("LEFT"),
    RIGHT("RIGHT");

    companion object {
        fun getReverseDirection(direction: SlideDirection): SlideDirection {
            return when (direction) {
                UP -> DOWN
                DOWN -> UP
                LEFT -> RIGHT
                RIGHT -> LEFT
            }
        }

        fun stringToDirection(str: String): SlideDirection {
            return when (str) {
                "UP" -> UP
                "DOWN" -> DOWN
                "LEFT" -> LEFT
                "RIGHT" -> RIGHT
                else -> throw IllegalArgumentException("Direction string invalid!")
            }
        }
    }
}