package components.tiles

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class SimpleTileTest {

    private val straightHorizontalPath = setOf(TilePathDirection.LEFT, TilePathDirection.RIGHT)
    private val fourPaths = setOf(
        TilePathDirection.LEFT,
        TilePathDirection.RIGHT,
        TilePathDirection.DOWN,
        TilePathDirection.UP
    )
    private lateinit var twoPathTile: ITileModel
    private lateinit var fourPathTile: ITileModel

    @BeforeEach
    fun setup() {
        twoPathTile = SimpleTile(
            arrayOf(Gem("clinohumite"), Gem("clinohumite1")),
            TileShape.STRAIGHT, 1)
        fourPathTile = SimpleTile(
            arrayOf(Gem("grandidierite"), Gem("grandidierite1")),
            TileShape.CROSS, 3)
    }

    @Test
    fun `getPathDirections happy paths`() {
        assertEquals(straightHorizontalPath, twoPathTile.getPathDirections())
        assertEquals(fourPaths, fourPathTile.getPathDirections())
    }

    @Test
    fun `rotate with positive degree`() {
        twoPathTile.rotate(0)
        assertEquals(setOf(TilePathDirection.LEFT, TilePathDirection.RIGHT), twoPathTile.getPathDirections())
        twoPathTile.rotate(90)
        assertEquals(setOf(TilePathDirection.UP, TilePathDirection.DOWN), twoPathTile.getPathDirections())
        twoPathTile.rotate(180)
        assertEquals(setOf(TilePathDirection.DOWN, TilePathDirection.UP), twoPathTile.getPathDirections())
        fourPathTile.rotate(90)
        assertEquals(setOf(
            TilePathDirection.UP,
            TilePathDirection.DOWN,
            TilePathDirection.LEFT,
            TilePathDirection.RIGHT,
            ),
            fourPathTile.getPathDirections()
        )
    }

    @Test
    fun `rotate with negative degree`() {
        twoPathTile.rotate(-90)
        assertEquals(setOf(TilePathDirection.RIGHT, TilePathDirection.LEFT), twoPathTile.getPathDirections())
        twoPathTile.rotate(-180)
        assertEquals(setOf(TilePathDirection.RIGHT, TilePathDirection.LEFT), twoPathTile.getPathDirections())
        fourPathTile.rotate(-90)
        assertEquals(setOf(
            TilePathDirection.DOWN,
            TilePathDirection.UP,
            TilePathDirection.RIGHT,
            TilePathDirection.LEFT,
            ),
            fourPathTile.getPathDirections()
        )
    }

    @Test
    fun `rotate with a degree that is not a multiple of 90`() {
        assertThrows<IllegalArgumentException> {
            twoPathTile.rotate(-1)
        }
        assertThrows<IllegalArgumentException> {
            twoPathTile.rotate(89)
        }
        assertThrows<IllegalArgumentException> {
            fourPathTile.rotate(1000)
        }
        assertThrows<IllegalArgumentException> {
            fourPathTile.rotate(179)
        }
    }

    @Test
    fun `getGems happy path`() {
        val gems1 = twoPathTile.getGems()

        assertTrue(gems1.contains(Gem("clinohumite")))
        assertTrue(gems1.contains(Gem("clinohumite1")))

        val gems2 = fourPathTile.getGems()
        assertTrue(gems2.contains(Gem("grandidierite")))
        assertTrue(gems2.contains(Gem("grandidierite1")))
    }

    @Test
    fun `deep copy happy path`() {
        val copy = twoPathTile.deepCopy()
        assertEquals(copy, twoPathTile)
        twoPathTile.rotate(90)
        assertNotEquals(copy, twoPathTile)
    }

    @Test
    fun `test toJson for straight`() {
        assertEquals("─", twoPathTile.toJson())
        twoPathTile.rotate(90)
        assertEquals("│", twoPathTile.toJson())
        twoPathTile.rotate(90)
        assertEquals("─", twoPathTile.toJson())
        twoPathTile.rotate(90)
        assertEquals("│", twoPathTile.toJson())
        twoPathTile.rotate(90)
        assertEquals("─", twoPathTile.toJson())
    }

    @Test
    fun `test toJson for cross`() {
        assertEquals("┼", fourPathTile.toJson())
        fourPathTile.rotate(90)
        assertEquals("┼", fourPathTile.toJson())
        fourPathTile.rotate(90)
        assertEquals("┼", fourPathTile.toJson())
        fourPathTile.rotate(90)
        assertEquals("┼", fourPathTile.toJson())
        fourPathTile.rotate(90)
        assertEquals("┼", fourPathTile.toJson())
    }

    @Test
    fun `test toJson for right angle`() {
        val rightPathTile = SimpleTile(
            arrayOf(Gem("grandidierite"), Gem("grandidierite1")),
            TileShape.RIGHT_ANGLE, 0)
        assertEquals("┐", rightPathTile.toJson())
        rightPathTile.rotate(90)
        assertEquals("┌", rightPathTile.toJson())
        rightPathTile.rotate(90)
        assertEquals("└", rightPathTile.toJson())
        rightPathTile.rotate(90)
        assertEquals("┘", rightPathTile.toJson())
        rightPathTile.rotate(90)
        assertEquals("┐", rightPathTile.toJson())
    }

    @Test
    fun `test toJson for t-junction`() {
        val threePathTile = SimpleTile(
            arrayOf(Gem("grandidierite"), Gem("grandidierite1")),
            TileShape.T_JUNCTION, 0)
        assertEquals("┬", threePathTile.toJson())
        threePathTile.rotate(90)
        assertEquals("├", threePathTile.toJson())
        threePathTile.rotate(90)
        assertEquals("┴", threePathTile.toJson())
        threePathTile.rotate(90)
        assertEquals("┤", threePathTile.toJson())
        threePathTile.rotate(90)
        assertEquals("┬", threePathTile.toJson())
    }
}