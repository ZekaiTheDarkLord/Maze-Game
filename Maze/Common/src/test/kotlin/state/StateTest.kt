package state

import components.boards.Position
import components.boards.Board
import components.boards.SlideDirection
import components.boards.IBoard
import components.tiles.Gem
import components.tiles.ITileModel
import components.tiles.SimpleTile
import components.tiles.TilePathDirection
import components.tiles.TileShape
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.awt.Color
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Unit tests for the state class.
 */
class StateTest {

    private lateinit var threeByThreeBoard: IBoard

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

    private lateinit var player1: PlayerGroundTruthData
    private lateinit var player2: PlayerGroundTruthData
    private lateinit var player3: PlayerGroundTruthData

    private lateinit var state: IState


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
            ), TileShape.STRAIGHT, 1
        )

        val tilesInThreeByThreeBoard: MutableList<MutableList<ITileModel>> = mutableListOf(
            mutableListOf(tile1, tile2, tile3),
            mutableListOf(tile4, tile5, tile6),
            mutableListOf(tile7, tile8, tile9)
        )

        threeByThreeBoard = Board(tilesInThreeByThreeBoard, tile10)
        player1 = PlayerGroundTruthData(
            "josh", Position(0, 0), Position(2, 0), Position(0, 0), false, Position(2, 0), Avatar(
                Color(244, 4, 244)
            )
        )
        player2 = PlayerGroundTruthData(
            "zoe", Position(0, 1), Position(2, 1), Position(0, 0), false, Position(2, 1), Avatar(
                Color(244, 4, 244)
            )
        )
        player3 = PlayerGroundTruthData(
            "ben", Position(0, 2), Position(2, 2), Position(0, 0), false, Position(2, 2), Avatar(
                Color(244, 4, 244)
            )
        )
        state = State(
            board = threeByThreeBoard,
            players = mutableListOf(player1, player2, player3),
            activePlayer = player1
        )
    }

    @Test
    fun `rotateSpare happy paths`() {
        assertEquals(setOf(TilePathDirection.RIGHT, TilePathDirection.LEFT), tile10.getPathDirections())
        state.rotateSpare(90)
        assertEquals(setOf(TilePathDirection.UP, TilePathDirection.DOWN), tile10.getPathDirections())
        state.rotateSpare(-270)
        assertEquals(setOf(TilePathDirection.UP, TilePathDirection.DOWN), tile10.getPathDirections())
    }

    @Test
    fun `rotateSpare throws exception`() {
        assertThrows<IllegalArgumentException> {
            state.rotateSpare(92)
        }
        assertThrows<IllegalArgumentException> {
            state.rotateSpare(-210)
        }
    }

    @Test
    fun `getActivePlayerGroundTruth and nextPlayer happy paths`() {
        assertEquals(Position(0, 0), state.getActivePlayerGroundTruth().homeTilePosition)
        state.nextPlayer()
        assertEquals(Position(0, 1), state.getActivePlayerGroundTruth().homeTilePosition)
        state.nextPlayer()
        assertEquals(Position(0, 2), state.getActivePlayerGroundTruth().homeTilePosition)
        state.nextPlayer()
        assertEquals(Position(0, 0), state.getActivePlayerGroundTruth().homeTilePosition)
    }

    @Test
    fun `horizontalSlideAndInsertSpare without player on edge happy paths`() {
        player1.homeTilePosition = Position(0, 0)
        player2.homeTilePosition = Position(0, 1)
        player3.homeTilePosition = Position(-1, -1)
        player1.treasurePosition = Position(1, 0)
        player2.treasurePosition = Position(1, 1)
        player3.treasurePosition = Position(1, 2)

        state.slideAndInsertSpare(0, SlideDirection.LEFT)

        assertEquals(tile2, threeByThreeBoard.getTileAt(Position(0, 0)))
        assertEquals(tile10, threeByThreeBoard.getTileAt(Position(0, 2)))

        assertEquals(Position(-1, -1), player1.homeTilePosition)
        assertEquals(Position(0, 0), player2.homeTilePosition)
        assertEquals(Position(0, 2), player3.homeTilePosition)
        assertEquals(Position(1, 0), player1.treasurePosition)
        assertEquals(Position(1, 1), player2.treasurePosition)
        assertEquals(Position(1, 2), player3.treasurePosition)


        player1.homeTilePosition = Position(2, 0)
        player2.homeTilePosition = Position(-1, -1)
        player3.homeTilePosition = Position(2, 1)
        player1.treasurePosition = Position(2, 2)
        player2.treasurePosition = Position(-1, -1)
        player3.treasurePosition = Position(2, 0)

        state.slideAndInsertSpare(2, SlideDirection.RIGHT)
        assertEquals(tile8, threeByThreeBoard.getTileAt(Position(2, 2)))
        assertEquals(tile1, threeByThreeBoard.getTileAt(Position(2, 0)))

        assertEquals(Position(2, 1), player1.homeTilePosition)
        assertEquals(Position(2, 0), player2.homeTilePosition)
        assertEquals(Position(2, 2), player3.homeTilePosition)
        assertEquals(Position(-1, -1), player1.treasurePosition)
        assertEquals(Position(2, 0), player2.treasurePosition)
        assertEquals(Position(2, 1), player3.treasurePosition)
    }

    @Test
    fun `horizontalSlideAndInsertSpare with players happy paths`() {
        player2.currentPosition = Position(0, 0)
        player3.currentPosition = Position(0, 1)
        state.slideAndInsertSpare(0, SlideDirection.LEFT)
        assertEquals(tile2, threeByThreeBoard.getTileAt(Position(0, 0)))
        assertEquals(tile10, threeByThreeBoard.getTileAt(Position(0, 2)))
        assertEquals(Position(0, 2), player2.currentPosition)
        assertEquals(Position(0, 0), player3.currentPosition)

        player2.currentPosition = Position(2, 2)
        player3.currentPosition = Position(2, 0)
        state.slideAndInsertSpare(2, SlideDirection.RIGHT)
        assertEquals(tile1, threeByThreeBoard.getTileAt(Position(2, 0)))
        assertEquals(tile8, threeByThreeBoard.getTileAt(Position(2, 2)))
        assertEquals(Position(2, 0), player2.currentPosition)
        assertEquals(Position(2, 1), player3.currentPosition)
    }

    @Test
    fun `horizontalSlideAndInsertSpare throws exceptions`() {
        assertThrows<IllegalArgumentException> {
            state.slideAndInsertSpare(-1, SlideDirection.RIGHT)
        }
        assertThrows<IllegalArgumentException> {
            state.slideAndInsertSpare(4, SlideDirection.LEFT)
        }
        assertThrows<IllegalArgumentException> {
            state.slideAndInsertSpare(1, SlideDirection.LEFT)
        }
    }

    @Test
    fun `verticalSlideAndInsertSpare without player on edge happy paths`() {

        player1.homeTilePosition = Position(2, 2)
        player2.homeTilePosition = Position(-1, -1)
        player3.homeTilePosition = Position(2, 0)
        player1.treasurePosition = Position(0, 0)
        player2.treasurePosition = Position(-1, -1)
        player3.treasurePosition = Position(2, 0)

        state.slideAndInsertSpare(0, SlideDirection.DOWN)
        assertEquals(tile4, threeByThreeBoard.getTileAt(Position(2, 0)))
        assertEquals(tile10, threeByThreeBoard.getTileAt(Position(0, 0)))

        assertEquals(Position(2, 2), player1.homeTilePosition)
        assertEquals(Position(0, 0), player2.homeTilePosition)
        assertEquals(Position(-1, -1), player3.homeTilePosition)
        assertEquals(Position(1, 0), player1.treasurePosition)
        assertEquals(Position(0, 0), player2.treasurePosition)
        assertEquals(Position(-1, -1), player3.treasurePosition)

        player1.homeTilePosition = Position(0, 2)
        player2.homeTilePosition = Position(-1, -1)
        player3.homeTilePosition = Position(2, 2)
        player1.treasurePosition = Position(0, 1)
        player2.treasurePosition = Position(1, 2)
        player3.treasurePosition = Position(-1, -1)

        state.slideAndInsertSpare(2, SlideDirection.UP)
        assertEquals(tile6, threeByThreeBoard.getTileAt(Position(0, 2)))
        assertEquals(tile7, threeByThreeBoard.getTileAt(Position(2, 2)))

        assertEquals(Position(-1, -1), player1.homeTilePosition)
        assertEquals(Position(2, 2), player2.homeTilePosition)
        assertEquals(Position(1, 2), player3.homeTilePosition)
        assertEquals(Position(0, 1), player1.treasurePosition)
        assertEquals(Position(0, 2), player2.treasurePosition)
        assertEquals(Position(2, 2), player3.treasurePosition)
    }

    @Test
    fun `verticalSlideAndInsertSpare with players on edge happy paths`() {
        player2.currentPosition = Position(2, 0)
        player3.currentPosition = Position(1, 0)
        state.slideAndInsertSpare(0, SlideDirection.DOWN)
        assertEquals(tile10, threeByThreeBoard.getTileAt(Position(0, 0)))
        assertEquals(tile4, threeByThreeBoard.getTileAt(Position(2, 0)))
        assertEquals(Position(0, 0), player2.currentPosition)
        assertEquals(Position(2, 0), player3.currentPosition)

        player2.currentPosition = Position(0, 2)
        player3.currentPosition = Position(2, 2)
        state.slideAndInsertSpare(2, SlideDirection.UP)
        assertEquals(tile6, threeByThreeBoard.getTileAt(Position(0, 2)))
        assertEquals(tile7, threeByThreeBoard.getTileAt(Position(2, 2)))
        assertEquals(Position(2, 2), player2.currentPosition)
        assertEquals(Position(1, 2), player3.currentPosition)
    }

    @Test
    fun `verticalSlideAndInsertSpare throws exceptions`() {
        assertThrows<IllegalArgumentException> {
            state.slideAndInsertSpare(-1, SlideDirection.DOWN)
        }
        assertThrows<IllegalArgumentException> {
            state.slideAndInsertSpare(4, SlideDirection.UP)
        }
        assertThrows<IllegalArgumentException> {
            state.slideAndInsertSpare(1, SlideDirection.UP)
        }
    }

    @Test
    fun `activePlayerOnGoalTile returns true`() {
        player1.currentPosition = Position(2, 0)
        assertTrue(state.activePlayerOnTreasure())

        state.nextPlayer()

        player2.currentPosition = Position(2, 1)
        assertTrue(state.activePlayerOnTreasure())
    }

    @Test
    fun `activePlayerOnGoalTile returns false`() {
        assertFalse(state.activePlayerOnTreasure())

        state.nextPlayer()

        assertFalse(state.activePlayerOnTreasure())
    }

    @Test
    fun `activePlayerKick happy path`() {
        assertEquals(Position(0, 0), state.getActivePlayerGroundTruth().currentPosition)
        state.activePlayerKick()
        player2.currentPosition = Position(2, 1)
        assertEquals(Position(2, 1), state.getActivePlayerGroundTruth().currentPosition)
        state.activePlayerKick()
        player3.currentPosition = Position(2, 2)
        assertEquals(Position(2, 2), state.getActivePlayerGroundTruth().currentPosition)
    }

    @Test
    fun `copyStateForActivePlayer happy paths`() {
        for (i in 0..4) {
            val copy1 = state.copyStateForActivePlayer()
            assertEquals(state.getActivePlayerGroundTruth(), copy1.getActivePlayerGroundTruth())
            assertNotEquals(state, copy1)
            state.nextPlayer()
        }
    }

    @Test
    fun `state creation with distinct player homes`() {
        State(
            board = threeByThreeBoard,
            players = mutableListOf(player1, player2, player3),
            activePlayer = player1
        )
    }

    @Test
    fun `state creation with non-distinct player homes`() {
        val player4 = PlayerGroundTruthData(
            "Matthias", Position(0, 1), Position(0, 0),
            Position(0, 2), false, Position(0, 2), Avatar(Color(255, 255, 255))
        )
        assertThrows<IllegalArgumentException> {
            State(
                board = threeByThreeBoard,
                players = mutableListOf(player1, player2, player3, player4),
                activePlayer = player1
            )
        }
    }

    @Test
    fun `state test to JSON`() {
        State(
            board = threeByThreeBoard,
            players = mutableListOf(player1, player2, player3),
            activePlayer = player1
        )
        assertEquals(
            "{\n" +
                    "\"board\": {\"connectors\": [[\"┬\",\"┼\",\"─\"],[\"┬\",\"┼\",\"│\"],[\"┴\",\"─\",\"┌\"]], \"treasures\": [[[\"black-obsidian\",\"black-obsidian1\"],[\"emerald\",\"emerald1\"],[\"grandidierite\",\"grandidierite1\"]],[[\"heliotrope\",\"heliotrope1\"],[\"jasper\",\"jasper1\"],[\"magnesite\",\"magnesite1\"]],[[\"prasiolite\",\"prasiolite1\"],[\"ruby\",\"ruby1\"],[\"zircon\",\"zircon1\"]]]}\", \n" +
                    "\"spare\": \"─\", \n" +
                    "\"plmt\" : [{\"current\": {\"row#\": 0, \"column#\": 0}, \"home\": {\"row#\": 0, \"column#\": 0}, \"color\": \"red\"}}, {\"current\": {\"row#\": 0, \"column#\": 0}, \"home\": {\"row#\": 0, \"column#\": 1}, \"color\": \"red\"}}, {\"current\": {\"row#\": 0, \"column#\": 0}, \"home\": {\"row#\": 0, \"column#\": 2}, \"color\": \"red\"}}],\n" +
                    "\"last\" : {\"first\":-1,\"second\":\"LEFT\"}}", state.toJson()
        )
    }
}