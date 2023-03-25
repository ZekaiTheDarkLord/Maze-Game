package player_referee_protocal

import components.boards.Position
import components.boards.SlideDirection

/**
 * Represents the slide-insert-move action of players.
 * @param rowOrColumnIdx: the index of the row or column players want to slide
 * @param slideDir: the slide direction players choose
 * @param rotateDegree: the degree player want to rotate for the inserting tile
 * @param targetPos: the target position player wants to move
 */
data class PlayerSIM(
    val rowOrColumnIdx: Int,
    val slideDir: SlideDirection,
    val rotateDegree: Int,
    val targetPos: Position
) : PlayerAction {
    override fun toJson(): String {
        return "[$rowOrColumnIdx, ${slideDir.slideDirectionString}, $rotateDegree, ${targetPos.toJson()}]"
    }
}
