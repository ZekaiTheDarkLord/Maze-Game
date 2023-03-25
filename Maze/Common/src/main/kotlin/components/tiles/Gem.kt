package components.tiles

import java.util.*

/**
 * Represents a gem in the Labyrinth game.
 */
data class Gem(
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Gem) return false

        return other.name == this.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }
}
