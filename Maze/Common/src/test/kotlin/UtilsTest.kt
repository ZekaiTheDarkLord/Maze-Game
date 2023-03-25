import com.google.gson.Gson
import com.google.gson.JsonObject
import components.boards.Position
import components.boards.Board
import components.tiles.Gem
import components.tiles.ITileModel
import components.tiles.SimpleTile
import components.tiles.TileShape
import org.junit.jupiter.api.Test
import state.Avatar
import state.PlayerGroundTruthData
import state.State
import java.awt.Color
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsTest {

    @Test
    fun `convertGameStateToJSON does the job`() {
        // Initialize a state
        val tile1 = SimpleTile(
            arrayOf(
                Gem("black-obsidian"),
                Gem("black-obsidian1")
            ), TileShape.T_JUNCTION, 0
        )
        val tile2 = SimpleTile(
            arrayOf(
                Gem("emerald"),
                Gem("emerald1")
            ), TileShape.CROSS, 3
        )
        val tile3 = SimpleTile(
            arrayOf(
                Gem("grandidierite"),
                Gem("grandidierite1")
            ), TileShape.STRAIGHT, 1
        )
        val tile4 = SimpleTile(
            arrayOf(
                Gem("heliotrope"),
                Gem("heliotrope1")
            ), TileShape.T_JUNCTION, 0
        )
        val tile5 = SimpleTile(
            arrayOf(
                Gem("jasper"),
                Gem("jasper1")
            ), TileShape.CROSS, 2
        )
        val tile6 = SimpleTile(
            arrayOf(
                Gem("magnesite"),
                Gem("magnesite1")
            ), TileShape.STRAIGHT, 0
        )
        val tile7 = SimpleTile(
            arrayOf(
                Gem("prasiolite"),
                Gem("prasiolite1")
            ), TileShape.T_JUNCTION, 2
        )
        val tile8 = SimpleTile(
            arrayOf(
                Gem("ruby"),
                Gem("ruby1")
            ), TileShape.STRAIGHT, 1
        )
        val tile9 = SimpleTile(
            arrayOf(
                Gem("zircon"),
                Gem("zircon1")
            ), TileShape.RIGHT_ANGLE, 3
        )
        val tile10 = SimpleTile(
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

        val threeByThreeBoard = Board(tilesInThreeByThreeBoard, tile10)
        val player1 = PlayerGroundTruthData(
            "josh", Position(0, 0), Position(2, 0), Position(0, 0), false, Position(2, 0), Avatar(
                Color(111, 112, 113)
            )
        )
        val state = State(
            board = threeByThreeBoard,
            players = mutableListOf(player1),
            activePlayer = player1
        )

        val serializedState = convertGameStateToJSON(state)
        println(serializedState)
        val deserializedState = Gson().fromJson(serializedState, JsonObject::class.java)
        assertTrue(deserializedState.has("board"))
        assertTrue(deserializedState.has("players"))
        assertTrue(deserializedState.has("activePlayer"))
        val boardJsonObject = deserializedState["board"].asJsonObject
        assertTrue(boardJsonObject.has("board"))
        assertTrue(boardJsonObject.has("spare"))
        val boardJsonArray = boardJsonObject["board"].asJsonArray
        assertEquals(3, boardJsonArray.size())
        assertEquals(3, boardJsonArray[0].asJsonArray.size())
        assertEquals(3, boardJsonArray[1].asJsonArray.size())
        assertEquals(3, boardJsonArray[2].asJsonArray.size())
        val playersJsonArray = deserializedState["players"].asJsonArray
        assertEquals(1, playersJsonArray.size())
        val gamePlayer = playersJsonArray[0].asJsonObject
        assertEquals("josh", gamePlayer["name"].asString)
        val homeTilePosition = gamePlayer["homeTilePosition"].asJsonObject
        assertEquals(0, homeTilePosition["rowIndex"].asInt)
        assertEquals(0, homeTilePosition["columnIndex"].asInt)
        val treasurePosition = gamePlayer["treasurePosition"].asJsonObject
        assertEquals(2, treasurePosition["rowIndex"].asInt)
        assertEquals(0, treasurePosition["columnIndex"].asInt)
        assertFalse(gamePlayer["reachedTreasure"].asBoolean)
        val currentTargetPosition = gamePlayer["currentTarget"].asJsonObject
        assertEquals(2, currentTargetPosition["rowIndex"].asInt)
        assertEquals(0, currentTargetPosition["columnIndex"].asInt)
        assertTrue(gamePlayer.has("avatar"))
    }

}