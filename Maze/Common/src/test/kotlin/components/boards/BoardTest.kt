package components.boards

import components.tiles.Gem
import components.tiles.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.IllegalArgumentException
import kotlin.test.assertEquals

/**
 * Unit tests of the board.
 */
class BoardTest {

    private lateinit var threeByThreeBoard: IBoard
    private lateinit var tilesInTwoByTwoBoard: MutableList<MutableList<ITileModel>>
    private lateinit var twoByTwoBoard: IBoard

    private lateinit var tile1: ITileModel
    private lateinit var tile2: ITileModel
    private lateinit var tile3: ITileModel
    private lateinit var tile4: ITileModel
    private lateinit var tile5: ITileModel
    private lateinit var tile6: ITileModel
    private lateinit var tile7: ITileModel
    private lateinit var tile8: ITileModel
    private lateinit var tile9: ITileModel
    private lateinit var tile10: ITileModel
    private lateinit var straightHorizontal1: ITileModel
    private lateinit var straightHorizontal2: ITileModel
    private lateinit var straightVertical1: ITileModel
    private lateinit var straightVertical2: ITileModel

    @BeforeEach
    fun setup() {
        tile1 = SimpleTile(
            arrayOf(
                Gem("black-obsidian"),
                Gem("black-obsidian1")
            ), TileShape.T_JUNCTION, 0
        )
        tile2 = SimpleTile(
            arrayOf(
                Gem("emerald"),
                Gem("emerald1")
            ), TileShape.CROSS, 3
        )
        tile3 = SimpleTile(
            arrayOf(
                Gem("grandidierite"),
                Gem("grandidierite1")
            ), TileShape.STRAIGHT, 1
        )
        tile4 = SimpleTile(
            arrayOf(
                Gem("heliotrope"),
                Gem("heliotrope1")
            ), TileShape.T_JUNCTION, 0
        )
        tile5 = SimpleTile(
            arrayOf(
                Gem("jasper"),
                Gem("jasper1")
            ), TileShape.CROSS, 2
        )
        tile6 = SimpleTile(
            arrayOf(
                Gem("magnesite"),
                Gem("magnesite1")
            ), TileShape.STRAIGHT, 0
        )
        tile7 = SimpleTile(
            arrayOf(
                Gem("prasiolite"),
                Gem("prasiolite1")
            ), TileShape.T_JUNCTION, 2
        )
        tile8 = SimpleTile(
            arrayOf(
                Gem("ruby"),
                Gem("ruby1")
            ), TileShape.STRAIGHT, 1
        )
        tile9 = SimpleTile(
            arrayOf(
                Gem("zircon"),
                Gem("zircon1")
            ), TileShape.RIGHT_ANGLE, 3
        )
        tile10 = SimpleTile(
            arrayOf(
                Gem("zircon"),
                Gem("zircon1")
            ), TileShape.CROSS, 1
        )

        val tilesInThreeByThreeBoard: MutableList<MutableList<ITileModel>> = mutableListOf(
            mutableListOf(tile1, tile2, tile3),
            mutableListOf(tile4, tile5, tile6),
            mutableListOf(tile7, tile8, tile9)
        )
        threeByThreeBoard = Board(tilesInThreeByThreeBoard, tile10)

        // Small 2x2 board
        straightHorizontal1 = SimpleTile(
            arrayOf(
                Gem("bulls-eye"),
                Gem("bulls-eye1")
            ), TileShape.STRAIGHT, 1
        )
        straightHorizontal2 = SimpleTile(
            arrayOf(
                Gem("carnelian"),
                Gem("carnelian1")
            ), TileShape.STRAIGHT, 3
        )
        straightVertical1 = SimpleTile(
            arrayOf(
                Gem("clinohumite"),
                Gem("clinohumite1")
            ), TileShape.STRAIGHT, 0
        )
        straightVertical2 = SimpleTile(
            arrayOf(
                Gem("diamond"),
                Gem("diamond1")
            ), TileShape.STRAIGHT, 2
        )
        tilesInTwoByTwoBoard = mutableListOf(
            mutableListOf(straightHorizontal1, straightHorizontal2),
            mutableListOf(straightVertical1, straightVertical2)
        )
        twoByTwoBoard = Board(tilesInTwoByTwoBoard, tile9)
    }

    @Test
    fun `constructor exceptions`() {
        assertThrows<IllegalArgumentException> {
            Board(mutableListOf(), tile10)
        }

        assertThrows<IllegalArgumentException> {
            Board(
                mutableListOf(
                    mutableListOf(tile1, tile2, tile3),
                    mutableListOf(tile4, tile5, tile6),
                    mutableListOf(tile7, tile8, EmptyTile()),
                ),
                tile10
            )
        }
        assertThrows<IllegalArgumentException> {
            Board(
                mutableListOf(
                    mutableListOf(tile1, tile2, tile3),
                    mutableListOf(tile4, tile5, tile6),
                    mutableListOf(tile7, tile8, tile9)
                ),
                EmptyTile()
            )
        }
    }


    @Test
    fun `slideRowAndInsert throws exceptions`() {

        assertThrows<IllegalArgumentException> {
            threeByThreeBoard.slideAndInsert(1, SlideDirection.LEFT)
        }

        assertThrows<IllegalArgumentException> {
            threeByThreeBoard.slideAndInsert(-2, SlideDirection.RIGHT)
        }

        assertThrows<IllegalArgumentException> {
            threeByThreeBoard.slideAndInsert(4, SlideDirection.LEFT)
        }
    }

    @Test
    fun `slideRowAndInsert happy paths`() {

        threeByThreeBoard.slideAndInsert(0, SlideDirection.LEFT)
        assertEquals(tile1, threeByThreeBoard.getSpare())
        assertEquals(tile10, threeByThreeBoard.getTileAt(Position(0, 2)))

        threeByThreeBoard.slideAndInsert(2, SlideDirection.RIGHT)
        assertEquals(tile9, threeByThreeBoard.getSpare())
        assertEquals(tile1, threeByThreeBoard.getTileAt(Position(2, 0)))

    }


    @Test
    fun `slideColumnAndInsert throws exception`() {
        assertThrows<IllegalArgumentException> {
            threeByThreeBoard.slideAndInsert(1, SlideDirection.UP)
        }

        assertThrows<IllegalArgumentException> {
            threeByThreeBoard.slideAndInsert(-2, SlideDirection.UP)
        }

        assertThrows<IllegalArgumentException> {
            threeByThreeBoard.slideAndInsert(4, SlideDirection.DOWN)
        }
    }

    @Test
    fun `slideColumnAndInsert happy paths`() {
        threeByThreeBoard.slideAndInsert(0, SlideDirection.UP)
        assertEquals(tile1, threeByThreeBoard.getSpare())
        assertEquals(tile10, threeByThreeBoard.getTileAt(Position(2, 0)))

        threeByThreeBoard.slideAndInsert(2, SlideDirection.DOWN)
        assertEquals(tile9, threeByThreeBoard.getSpare())
        assertEquals(tile1, threeByThreeBoard.getTileAt(Position(0, 2)))
    }

    @Test
    fun `reachableFrom happy paths`() {

        // Small 2x2 board
        val reachAbleFromStraightHorizontal1 = twoByTwoBoard.reachableFrom(Position(0, 0))
        assertEquals(2, reachAbleFromStraightHorizontal1.size)
        assertTrue(
            reachAbleFromStraightHorizontal1.containsAll(
                listOf(
                    Position(0, 0),
                    Position(0, 1)
                )
            )
        )

        val reachAbleFromStraightVertical1 = twoByTwoBoard.reachableFrom(Position(1, 0))
        assertEquals(1, reachAbleFromStraightVertical1.size)
        assertTrue(reachAbleFromStraightVertical1.contains(Position(1, 0)))
    }

    @Test
    fun `reachableFrom start with invalid position`() {
        assertThrows<IllegalArgumentException> {
            twoByTwoBoard.reachableFrom(Position(-1, -1))
        }
        assertThrows<IllegalArgumentException> {
            twoByTwoBoard.reachableFrom(Position(4, 4))
        }
        assertThrows<IllegalArgumentException> {
            twoByTwoBoard.reachableFrom(Position(-4, 2))
        }
        assertThrows<IllegalArgumentException> {
            twoByTwoBoard.reachableFrom(Position(2, -4))
        }
    }

    @Test
    fun `getDimension happy paths`() {
        assertEquals(Pair(3, 3), threeByThreeBoard.getDimension())
    }

    @Test
    fun `getSpare happy path`() {
        assertEquals(tile10, threeByThreeBoard.getSpare())
        assertEquals(tile9, twoByTwoBoard.getSpare())
    }

    @Test
    fun `rotateSpare happy path`() {
        assertEquals(tile9, twoByTwoBoard.getSpare())
        assertEquals(
            setOf(TilePathDirection.RIGHT, TilePathDirection.DOWN),
            tile9.getPathDirections()
        )
        twoByTwoBoard.rotateSpare(0)
        assertEquals(
            setOf(TilePathDirection.RIGHT, TilePathDirection.DOWN),
            tile9.getPathDirections()
        )
        twoByTwoBoard.rotateSpare(90)
        assertEquals(
            setOf(TilePathDirection.RIGHT, TilePathDirection.UP),
            tile9.getPathDirections()
        )
        twoByTwoBoard.rotateSpare(-180)
        assertEquals(
            setOf(TilePathDirection.RIGHT, TilePathDirection.UP),
            tile9.getPathDirections()
        )
        twoByTwoBoard.rotateSpare(720)
        assertEquals(
            setOf(TilePathDirection.RIGHT, TilePathDirection.UP),
            tile9.getPathDirections()
        )
    }

    @Test
    fun `rotateSpare throws exception`() {
        assertThrows<IllegalArgumentException> {
            twoByTwoBoard.rotateSpare(1)
        }

        assertThrows<IllegalArgumentException> {
            twoByTwoBoard.rotateSpare(-91)
        }
        assertThrows<IllegalArgumentException> {
            twoByTwoBoard.rotateSpare(271)
        }
    }

    @Test
    fun `test two by two board`() {
        assertEquals("{\"connectors\": [[\"─\",\"─\"],[\"│\",\"│\"]], \"treasures\": [[[\"bulls-eye\",\"bulls-eye1\"],[\"carnelian\",\"carnelian1\"]],[[\"clinohumite\",\"clinohumite1\"],[\"diamond\",\"diamond1\"]]]}\"", twoByTwoBoard.toJson())
    }


}