package components.boards

/**
 * Represents a position on the board.
 * @param rowIndex: the row index of the board.
 * @param columnIndex: the column index of the board.
 */
data class Position(
    val rowIndex: Int,
    val columnIndex: Int
) {
    fun deepCopy(): Position {
        return Position(
            rowIndex = rowIndex,
            columnIndex = columnIndex
        )
    }

    fun toJson(): String {
        return "{\"row#\": ${this.rowIndex}, \"column#\": ${this.columnIndex}}"
    }
}
