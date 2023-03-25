package strategy.player

import components.boards.Position
import components.boards.Board
import components.boards.IBoard
import components.boards.SlideDirection
import components.tiles.Gem
import components.tiles.ITileModel
import components.tiles.SimpleTile
import components.tiles.TileShape
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import player_referee_protocal.PlayerSIM
import state.Avatar
import state.PlayerGroundTruthData
import state.State
import java.awt.Color
import kotlin.test.assertEquals

class RiemannTest {
    private lateinit var threeByThreeBoard: IBoard

    private lateinit var tile1InBoard1: ITileModel
    private lateinit var tile2InBoard1: ITileModel
    private lateinit var tile3InBoard1: ITileModel
    private lateinit var tile4InBoard1: ITileModel
    private lateinit var tile5InBoard1: ITileModel
    private lateinit var tile6InBoard1: ITileModel
    private lateinit var tile7InBoard1: ITileModel
    private lateinit var tile8InBoard1: ITileModel
    private lateinit var tile9InBoard1: ITileModel
    private lateinit var spareTileInBoard1: ITileModel

    @BeforeEach
    fun setup() {
        tile1InBoard1 = SimpleTile(
            arrayOf(
                Gem("gem111"),
                Gem("gem112")
            ), TileShape.STRAIGHT, 1
        )
        tile2InBoard1 = SimpleTile(
            arrayOf(
                Gem("gem121"),
                Gem("gem122")
            ), TileShape.RIGHT_ANGLE, 0
        )
        tile3InBoard1 = SimpleTile(
            arrayOf(
                Gem("gem131"),
                Gem("gem132")
            ), TileShape.RIGHT_ANGLE, 0
        )
        tile4InBoard1 = SimpleTile(
            arrayOf(
                Gem("gem141"),
                Gem("gem142")
            ), TileShape.RIGHT_ANGLE, 3
        )
        tile5InBoard1 = SimpleTile(
            arrayOf(
                Gem("gem151"),
                Gem("gem152")
            ), TileShape.CROSS, 0
        )
        tile6InBoard1 = SimpleTile(
            arrayOf(
                Gem("gem161"),
                Gem("gem162")
            ), TileShape.RIGHT_ANGLE, 2
        )
        tile7InBoard1 = SimpleTile(
            arrayOf(
                Gem("gem171"),
                Gem("gem172")
            ), TileShape.RIGHT_ANGLE, 0
        )
        tile8InBoard1 = SimpleTile(
            arrayOf(
                Gem("gem181"),
                Gem("gem182")
            ), TileShape.T_JUNCTION, 2
        )
        tile9InBoard1 = SimpleTile(
            arrayOf(
                Gem("gem191"),
                Gem("gem192")
            ), TileShape.STRAIGHT, 0
        )

        spareTileInBoard1 = SimpleTile(
            arrayOf(
                Gem("spare_gem_11"),
                Gem("spare_gem_12")
            ), TileShape.STRAIGHT, 0
        )

        val tilesInThreeByThreeBoard: MutableList<MutableList<ITileModel>> = mutableListOf(
            mutableListOf(tile1InBoard1, tile2InBoard1, tile3InBoard1),
            mutableListOf(tile4InBoard1, tile5InBoard1, tile6InBoard1),
            mutableListOf(tile7InBoard1, tile8InBoard1, tile9InBoard1)
        )
        threeByThreeBoard = Board(tilesInThreeByThreeBoard, spareTileInBoard1)
    }


    //1. player go to another side during slide
    //2. can reach initial target
    //3. cannot reach initial target
    //4. target tile changed position during slide
    //5. slide row
    //6. slide column

    @Test
    fun `slide column to reach Target`() {
        val playerInfo = PlayerGroundTruthData(
            "zekai",
            Position(0, 0),
            Position(1, 1),
            Position(0, 0),
            false,
            Position(1, 1),
            Avatar(Color(244, 4, 244)),
        )

        val state = State(board = threeByThreeBoard, players = mutableListOf(playerInfo), activePlayer = playerInfo)

        val riemnn1 = Riemann(
            state,
            Position(1, 1)
        )

        assertEquals(
            PlayerSIM(2, SlideDirection.LEFT,
                0, Position(1, 1)
            ),
            riemnn1.execute()
        )
    }

    @Test
    fun `player go to another side during slide`() {
        val playerInfo = PlayerGroundTruthData(
            "zekai",
            Position(0, 0),
            Position(1, 2),
            Position(0, 0),
            false,
            Position(1, 2),
            Avatar(Color(244, 4, 244)),
        )

        val state = State(board = threeByThreeBoard, players = mutableListOf(playerInfo), activePlayer = playerInfo)

        val riemnn1 = Riemann(
            state,
            Position(1, 2)
        )

        assertEquals(
            PlayerSIM(0, SlideDirection.LEFT,
                0, Position(1, 2)
            ),
            riemnn1.execute()
        )
    }

    @Test
    fun `slide column to reach initial target`() {
        val playerInfo = PlayerGroundTruthData(
            "zekai",
            Position(0, 0),
            Position(1, 2),
            Position(1, 0),
            false,
            Position(1, 2),
            Avatar(Color(244, 4, 244))
        )

        val state = State(board = threeByThreeBoard, players = mutableListOf(playerInfo), activePlayer = playerInfo)

        val riemnn1 = Riemann(
            state,
            Position(1, 2)
        )

        assertEquals(
            PlayerSIM(2, SlideDirection.DOWN, 0, Position(1, 2)),
            riemnn1.execute()
        )
    }

    @Test
    fun `algorithm choose alternative goal`() {
        val playerInfo = PlayerGroundTruthData(
            "zekai",
            Position(0, 0),
            Position(0, 2),
            Position(2, 2),
            false,
            Position(0, 2),
            Avatar(Color(244, 4, 244))
        )

        val state = State(board = threeByThreeBoard, players = mutableListOf(playerInfo), activePlayer = playerInfo)

        val riemnn1 = Riemann(
            state,
            Position(0, 2)
        )

        assertEquals(
            PlayerSIM(2, SlideDirection.LEFT, 0, Position(0, 0)),
            riemnn1.execute()
        )
    }
}