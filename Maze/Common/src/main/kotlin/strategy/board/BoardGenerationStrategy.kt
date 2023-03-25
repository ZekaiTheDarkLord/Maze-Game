package strategy.board

import components.boards.IBoard
import java.util.*

/**
 * Represents strategies for generating the game board.
 */
interface BoardGenerationStrategy {

    /**
     * Execute this strategy
     */
    fun execute(rows: Int, columns: Int, random: Random): IBoard
}