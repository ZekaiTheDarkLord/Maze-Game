package referee


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
import org.mockito.Mockito
import org.mockito.kotlin.any
import player.EuclidPlayer
import player.IPlayer
import player.RiemannPlayer
import player_referee_protocal.PlayerPass
import player_referee_protocal.PlayerSIM
import state.Avatar
import state.IState
import state.PlayerGroundTruthData
import state.State
import java.awt.Color
import kotlin.test.assertEquals

internal class RefereeTest {

    private lateinit var referee1: IReferee
    private lateinit var gameState1: IState
    private lateinit var player1: IPlayer
    private lateinit var player2: IPlayer

    private lateinit var players1: MutableList<IPlayer>
    private lateinit var threeByThreeBoard1: IBoard

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

    private lateinit var playerData1: PlayerGroundTruthData
    private lateinit var playerData2: PlayerGroundTruthData

    private lateinit var referee2: IReferee
    private lateinit var riemannPlayer: IPlayer
    private lateinit var euclidPlayer: IPlayer
    private lateinit var playerInfo1: PlayerGroundTruthData
    private lateinit var playerInfo2: PlayerGroundTruthData
    private lateinit var threeByThreeBoard2: IBoard
    private lateinit var players2: MutableList<IPlayer>
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
    private lateinit var gameState2: IState

    @BeforeEach
    fun setup() {
        player1 = Mockito.mock(IPlayer::class.java)
        player2 = Mockito.mock(IPlayer::class.java)
        players1 = mutableListOf(player1, player2)

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

        val tilesInThreeByThreeBoard1: MutableList<MutableList<ITileModel>> = mutableListOf(
            mutableListOf(tile1, tile2, tile3),
            mutableListOf(tile4, tile5, tile6),
            mutableListOf(tile7, tile8, tile9)
        )

        threeByThreeBoard1 = Board(tilesInThreeByThreeBoard1, tile10)

        playerData1 = PlayerGroundTruthData("josh", Position(0, 0), Position(3, 3), Position(0, 0), false, Position(2, 0), Avatar(
            Color(244, 4, 244)
        ))
        playerData2 = PlayerGroundTruthData("zoe", Position(0, 1), Position(2, 1), Position(0, 0), false, Position(2, 1), Avatar(
            Color(244, 4, 244)))

        gameState1 = Mockito.spy(State(
            board = threeByThreeBoard1,
            players = mutableListOf(playerData1, playerData2),
            activePlayer = playerData1
        ))

        referee1 = Mockito.spy(Referee())

        riemannPlayer = RiemannPlayer("Tom")
        euclidPlayer = EuclidPlayer("Isa")
        players2 = mutableListOf(riemannPlayer, euclidPlayer)
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

        val tilesInThreeByThreeBoard2: MutableList<MutableList<ITileModel>> = mutableListOf(
            mutableListOf(tile1InBoard1, tile2InBoard1, tile3InBoard1),
            mutableListOf(tile4InBoard1, tile5InBoard1, tile6InBoard1),
            mutableListOf(tile7InBoard1, tile8InBoard1, tile9InBoard1)
        )
        threeByThreeBoard2 = Board(tilesInThreeByThreeBoard2, spareTileInBoard1)

        playerInfo1 = PlayerGroundTruthData(
            "zekai",
            Position(1, 0),
            Position(1, 1),
            Position(0, 0),
            false,
            Position(1, 1),
            Avatar(Color(244, 4, 244))
        )

        playerInfo2 = PlayerGroundTruthData(
            "chengyi",
            Position(0, 1),
            Position(2, 2),
            Position(0, 0),
            false,
            Position(1, 1),
            Avatar(Color(244, 4, 244))
        )

        gameState2 = State(
            board = threeByThreeBoard2,
            players = mutableListOf(playerData1, playerInfo1, playerInfo2),
            activePlayer = playerData1
        )
        referee2 = Referee()
    }

    @Test
    fun

            `playGame ends immediately if all player passes`() {
        Mockito.`when`(player1.takeTurn(any())).thenReturn(PlayerPass)
        Mockito.`when`(player2.takeTurn(any())).thenReturn(PlayerPass)
        val result = referee1.playGame(
            players = players1,
            gameState = gameState1,
            observers = mutableListOf()
        )
        assertEquals(1, result.first.size)
        assertEquals(0, result.second.size)
    }

    @Test
    fun `playGame returns two winners if their distance to treasure is the same and they have not visited the treasure yet`() {
        playerData1 = PlayerGroundTruthData("josh", Position(0, 0), Position(2, 0), Position(0, 0), false, Position(2, 0), Avatar(Color(244, 4, 244)))
        playerData2 = PlayerGroundTruthData("zoe", Position(0, 1), Position(2, 1), Position(0, 0), false, Position(2, 0), Avatar(Color(244, 4, 244)))
        gameState1 = Mockito.spy(State(
            board = threeByThreeBoard1,
            players = mutableListOf(playerData1, playerData2),
            activePlayer = playerData1
        ))
        referee1 = Mockito.spy(Referee())
        Mockito.`when`(player1.takeTurn(any())).thenReturn(PlayerPass)
        Mockito.`when`(player2.takeTurn(any())).thenReturn(PlayerPass)
        val result = referee1.playGame(
            players = players1,
            gameState = gameState1,
            observers = mutableListOf()
        )
        assertEquals(1, result.first.size)
        assertEquals(0, result.second.size)
    }

    @Test
    fun `playGame returns one winners if only one player has visited the treasure`() {
        playerData1 = PlayerGroundTruthData("josh", Position(0, 0), Position(2, 0), Position(0, 0), false, Position(2, 0), Avatar(Color(244, 4, 244)))
        playerData2 = PlayerGroundTruthData("zoe", Position(0, 1), Position(2, 1), Position(0, 0), true, Position(2, 0), Avatar(Color(244, 4, 244)))
        gameState1 = Mockito.spy(State(
            board = threeByThreeBoard1,
            players = mutableListOf(playerData1, playerData2),
            activePlayer = playerData1
        ))
        referee1 = Mockito.spy(Referee())
        Mockito.`when`(player1.takeTurn(any())).thenReturn(PlayerPass)
        Mockito.`when`(player2.takeTurn(any())).thenReturn(PlayerPass)
        val result = referee1.playGame(
            players = players1,
            gameState = gameState1,
            observers = mutableListOf()
        )
        assertEquals(1, result.first.size)
        assertEquals(0, result.second.size)
        assertEquals(player2, result.first[0])
    }

    @Test
    fun `playGame returns two winners if their distance to treasure is the same and they have visited the treasure yet`() {
        playerData1 = PlayerGroundTruthData("josh", Position(0, 0), Position(3, 1), Position(4, 4), true, Position(0, 0), Avatar(Color(244, 4, 244)))
        playerData2 = PlayerGroundTruthData("zoe", Position(0, 1), Position(2, 0), Position(4, 4), true, Position(0, 0), Avatar(Color(244, 4, 244)))
        gameState1 = Mockito.spy(State(
            board = threeByThreeBoard1,
            players = mutableListOf(playerData1, playerData2),
            activePlayer = playerData1
        ))
        referee1 = Mockito.spy(Referee())
        Mockito.`when`(player1.takeTurn(any())).thenReturn(PlayerPass)
        Mockito.`when`(player2.takeTurn(any())).thenReturn(PlayerPass)
        val result = referee1.playGame(
            players = players1,
            gameState = gameState1,
            observers = mutableListOf()
        )
        assertEquals(1, result.first.size)
        assertEquals(0, result.second.size)
    }

    @Test
    fun `playGame plays a normal game`() {
        val result = referee2.playGame(
            players = players2,
            gameState = gameState2,
            observers = mutableListOf()
        )
        assertEquals(1, result.first.size)
        assertEquals(0, result.second.size)
    }

    @Test
    fun `playGame plays a normal game with one winner 1`() {
        riemannPlayer = Mockito.mock(IPlayer::class.java)
        Mockito.`when`(riemannPlayer.takeTurn(any())).thenReturn(PlayerPass)
        players2 = mutableListOf(riemannPlayer, euclidPlayer)
        referee2 = Referee()
        val result = referee2.playGame(
            players = players2,
            gameState = gameState2,
            observers = mutableListOf()
        )
        assertEquals(1, result.first.size)
        assertEquals(0, result.second.size)
        assertEquals(euclidPlayer, result.first[0])
    }

    @Test
    fun `playGame plays a normal game with one winner 2`() {
        euclidPlayer = Mockito.mock(IPlayer::class.java)
        Mockito.`when`(euclidPlayer.takeTurn(any())).thenReturn(PlayerPass)
        players2 = mutableListOf(riemannPlayer, euclidPlayer)
        referee2 = Referee()
        val result = referee2.playGame(
            players = players2,
            gameState = gameState2,
            observers = mutableListOf()
        )
        assertEquals(1, result.first.size)
        assertEquals(0, result.second.size)
        assertEquals(riemannPlayer, result.first[0])
    }

    @Test
    fun `playGame kicks cheater with invalid rotation degree`() {
        euclidPlayer = Mockito.mock(IPlayer::class.java)
        Mockito.`when`(euclidPlayer.takeTurn(any())).thenReturn(
            PlayerSIM(
                rowOrColumnIdx = 0,
                slideDir = SlideDirection.LEFT,
                rotateDegree = 1,
                targetPos = Position(1, 1),
            )
        )
        players2 = mutableListOf(riemannPlayer, euclidPlayer)
        referee2 = Referee()
        val result = referee2.playGame(
            players = players2,
            gameState = gameState2,
            observers = mutableListOf()
        )
        assertEquals(1, result.first.size)
        assertEquals(1, result.second.size)
        assertEquals(riemannPlayer, result.first[0])
        assertEquals(euclidPlayer, result.second[0])
    }

    // Need to validate
}