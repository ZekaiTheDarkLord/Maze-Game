package components.tiles

/**
 * Represents the shape of a tile.
 * Here are the visualized TileShapes.
 * STRAIGHT = │
 * RIGHT_ANGLE = ┐
 * T_JUNCTION = ┬
 * CROSS = ┼
 * INVALID = a placeholder shape.
 */
enum class TileShape {
    STRAIGHT, RIGHT_ANGLE, T_JUNCTION, CROSS
}