package strategy.board

import components.boards.Position
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertNotEquals

class SimpleGenerationStrategyTest {

    @Test
    fun `valid tile uniqueness`() {

        for (testCount in 0 .. 100) {

            val strategy = SimpleGenerationStrategy()
            val board = strategy.execute(10, 10, Random(0L))
            for (i in 0 until board.getDimension().first) {
                for (j in 0 until board.getDimension().second - 1) {

                    val tile1 = board.getTileAt(Position(i ,j))
                    val tile2 = board.getTileAt(Position(i,j + 1))
                    if (tile1 == tile2) {
                        println("${tile1.getGems()[0]} ${tile1.getGems()[1]} | ${tile2.getGems()[0]} ${tile2.getGems()[1]}")
                        println("${tile1.getPathDirections()} ${tile2.getPathDirections()}")
                    }


                    assertNotEquals(
                        board.getTileAt(Position(i ,j)),
                        board.getTileAt(Position(i,j + 1))
                    )
                }
            }

        }
    }
}