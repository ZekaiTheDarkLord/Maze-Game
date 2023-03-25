package integration_tests.state

import com.google.gson.*
import components.boards.Position
import components.boards.Board
import components.boards.SlideDirection
import components.tiles.Gem
import components.tiles.EmptyTile
import components.tiles.ITileModel
import components.tiles.SimpleTile
import integration_tests.board.getBoardTiles
import integration_tests.board.getTileDirectionMap
import integration_tests.board.getTileShapeMap
import state.Avatar
import state.PlayerGroundTruthData
import state.State
import java.awt.Color
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

fun main() {
    val stateInitData = scanJsonState(System.`in`)
    val state = stateInitData.state
    // Convert counter-clockwise to clockwise
    state.rotateSpare(stateInitData.rotationDegree * -1)
    state.slideAndInsertSpare(stateInitData.rowOrColumnIndex, stateInitData.slideDirection)
    val unformattedResult = state.getActivePlayerReachableTilesPositions()
    print(formatResult(unformattedResult))
}

fun formatResult(positions: List<Position>): JsonArray {
    val rowColumnOrder: Comparator<Position> = compareBy<Position> {
        it.rowIndex
    }.thenComparator { a, b -> compareValues(a.columnIndex, b.columnIndex) }
    val gson = Gson()
    val resultArray = JsonArray()
    Collections.sort(positions, rowColumnOrder)
    positions.forEach {
        val data = "{ \"row#\": ${it.rowIndex}, \"column#\": ${it.columnIndex} }"
        resultArray.add(gson.fromJson(data, JsonObject::class.java))
    }
    return resultArray
}

fun scanJsonState(inputStream: InputStream): StateInitializationData {

    val parser = JsonStreamParser(InputStreamReader(inputStream))
    var boardTiles: MutableList<MutableList<ITileModel>> = mutableListOf()
    var spareTile: ITileModel = EmptyTile()
    var playerTruthData: MutableList<PlayerGroundTruthData> = mutableListOf()

    while(parser.hasNext()) {

        var element: JsonElement = parser.next()
        if (element.isJsonObject) {
            val jsonObject = element.asJsonObject
            // Board
            if (jsonObject.has("board")) {
                val boardJson = jsonObject["board"].asJsonObject
                if (boardJson.has("connectors") && boardJson.has("treasures")) {
                    boardTiles = getBoardTiles(
                        boardJson["connectors"].asJsonArray,
                        boardJson["treasures"].asJsonArray
                    )
                }
            // Spare
            }
            if (jsonObject.has("spare")) {
                val spareObject = jsonObject["spare"].asJsonObject
                spareTile = SimpleTile(
                    gems = arrayOf(
                        Gem(spareObject["1-image"].asString),
                        Gem(spareObject["2-image"].asString)
                    ),
                    tileShape = getTileShapeMap()[spareObject["tilekey"].asString]!!,
                    tileShapeDirection = getTileDirectionMap()[spareObject["tilekey"].asString]!!,
                )
            // Players
            }
            if (jsonObject.has("plmt")) {
                val playersArray = jsonObject["plmt"].asJsonArray
                // Need to refactor player
                playersArray.forEach {
                    val playerObject = it.asJsonObject
                    val currentPositionJsonObject = playerObject["current"].asJsonObject
                    val currentPosition = Position(
                        currentPositionJsonObject["row#"].asInt,
                        currentPositionJsonObject["column#"].asInt
                    )
                    playerTruthData.add(
                        // some garbage data cuz not needed by this test
                        PlayerGroundTruthData(
                            name = "bruh",
                            homeTilePosition = Position(-1, -1),
                            treasurePosition = Position(-1, -1),
                            currentPosition = currentPosition,
                            reachedTreasure = false,
                            avatar = Avatar(Color(0, 0, 0)),
                            currentTarget = Position(-1, -1)
                        )
                    )
                }
            }
            // Ignore last for now
            break
        }
    }
    val rowOrColumnIndex = parser.next().asInt
    val slideDirection = SlideDirection.valueOf(parser.next().asString.uppercase())
    val rotationDegree = parser.next().asInt

    val state = State(
        board = Board(
            board = boardTiles,
            spare = spareTile
        ),
        players = playerTruthData,
        activePlayer = playerTruthData[0]
    )

    return StateInitializationData(
        state = state,
        rowOrColumnIndex = rowOrColumnIndex,
        slideDirection = slideDirection,
        rotationDegree = rotationDegree
    )
}