package integration_tests.strategy

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
import player_referee_protocal.PlayerAction
import player_referee_protocal.PlayerPass
import player_referee_protocal.PlayerSIM
import state.*
import strategy.player.Euclid
import strategy.player.Riemann
import strategy.player.Strategy
import java.awt.Color
import java.io.InputStreamReader

fun main() {
    val strategy: Strategy
    val parser = JsonStreamParser(InputStreamReader(System.`in`))
    val strategyName = scanStrategy(parser)
    val state = scanJsonState(parser)
    val targetPosition = scanPosition(parser)

    // Convert counter-clockwise to clockwise

    strategy = if (strategyName == "Euclid") {
        Euclid(state, targetPosition)
    } else {
        Riemann(state, targetPosition)
    }

    val unformattedResult = strategy.execute()
    formatResult(unformattedResult)
}

fun formatResult(playerAction: PlayerAction) {

    val gson = Gson()

    if (playerAction is PlayerPass) {
        print("PASS")
    } else {
        val resultArray = JsonArray()
        val playerActionCopy = playerAction as PlayerSIM

        // index
        resultArray.add(gson.toJson(playerActionCopy.rowOrColumnIdx))
        // direction
        resultArray.add(playerActionCopy.slideDir.toString())
        // rotation degree
        resultArray.add(gson.toJson(playerActionCopy.rotateDegree))

        // coordinate
        val coordinate =
            "{ \"row#\": ${playerActionCopy.targetPos.rowIndex}, \"column#\": ${playerActionCopy.targetPos.columnIndex} }"
        resultArray.add(gson.fromJson(coordinate, JsonObject::class.java))

        print(resultArray)
    }
}

//fun clockToCounterClock(degree: Int): Int {
//    return if (degree == 0) 0
//    else 360 - degree
//}

fun scanStrategy(parser: JsonStreamParser): String {

    while (parser.hasNext()) {
        val element: JsonElement = parser.next()
        if (element.asString == "Euclid" || element.asString == "Riemann") {
            return element.asString
        }
    }

    return ""
}

fun scanJsonState(parser: JsonStreamParser): IState {
    var boardTiles: MutableList<MutableList<ITileModel>> = mutableListOf()
    var spareTile: ITileModel = EmptyTile()
    var playerTruthData: MutableList<PlayerGroundTruthData> = mutableListOf()
    lateinit var lastSlide: Pair<Int, SlideDirection>

    while (parser.hasNext()) {
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

            if (jsonObject.has("last")) {
                if (jsonObject["last"].isJsonNull) {
                    lastSlide = Pair(-1, SlideDirection.DOWN)
                } else {
                    val lastArray = jsonObject["last"].asJsonArray
                    // Need to refactor player
                    lastSlide = Pair(
                        lastArray[0].asInt,
                        SlideDirection.stringToDirection(lastArray[1].asString)
                    )
                }
            }
            // Ignore last for now
            break
        }
    }

    return State(
        board = Board(
            board = boardTiles,
            spare = spareTile
        ),
        players = playerTruthData,
        lastSlide = lastSlide,
        activePlayer = playerTruthData[0]
    )
}

fun scanPosition(parser: JsonStreamParser): Position {
    while (parser.hasNext()) {
        val element: JsonElement = parser.next()
        if (element.asJsonObject.has("row#") &&
            element.asJsonObject.has("column#")
        ) {
            // Set staring position
            return Position(
                element.asJsonObject.get("row#").asInt,
                element.asJsonObject.get("column#").asInt
            )
        }
    }

    return Position(-1, -1)
}