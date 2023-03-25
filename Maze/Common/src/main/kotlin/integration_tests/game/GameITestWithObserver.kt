package integration_tests.game

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonStreamParser
import components.boards.Position
import components.boards.Board
import components.tiles.Gem
import components.tiles.EmptyTile
import components.tiles.ITileModel
import components.tiles.SimpleTile
import integration_tests.board.getBoardTiles
import integration_tests.board.getTileDirectionMap
import integration_tests.board.getTileShapeMap
import observer.Observer
import player.EuclidPlayer
import player.IPlayer
import player.RiemannPlayer
import referee.Referee
import state.Avatar
import state.IState
import state.PlayerGroundTruthData
import state.State
import java.awt.Color
import java.io.InputStreamReader


fun main() {
    // get basic elements
    val parser = JsonStreamParser(InputStreamReader(System.`in`))
    val players = createPlayerList(parser)
    val refereeState = scanRefereeState(parser, players)

    // add player to the list
    val referee = Referee()
    val winnersAndCheaters =
        referee.playGame(players, refereeState, mutableListOf(Observer(refereeState)))
    val winners = winnersAndCheaters.first
    val names: MutableList<String> = getNames(winners) as MutableList<String>
    for (i in names.indices) {
        names[i] = names[i].substring(1, names[i].length - 1)

    }
    
    val result = Gson().toJson(names)
    print(result)
}

fun createPlayerList(parser: JsonStreamParser): MutableList<IPlayer> {
    val players = mutableListOf<IPlayer>()
    val playerSpec = scanPlayerSpec(parser)

    playerSpec.forEach {
        if (it.second == "\"Euclid\"") {
            players.add(EuclidPlayer(it.first))
        } else {
            players.add(RiemannPlayer(it.first))
        }
    }

    return players
}

// Scan the PlayerSpec JSON input
// A PlayerSpec is a JSON array of PS arrays.
// A PS is a JSON array of two (2) elements: [Name, Strategy]
// Returns pairs of player name and strategy
fun scanPlayerSpec(parser: JsonStreamParser): MutableList<Pair<String, String>> {
    val scannedData: MutableList<Pair<String, String>> = mutableListOf()

    while (parser.hasNext()) {
        val element: JsonElement = parser.next()
        if (element.isJsonArray) {
            val temp = element.asJsonArray.toMutableList()
            temp.forEach {
                val ps = it.asJsonArray
                scannedData.add(
                    Pair(
                        ps.get(0).toString(), ps.get(1).toString()
                    )
                )
            }
            break
        }
    }

    return scannedData
}

fun scanRefereeState(parser: JsonStreamParser, players: List<IPlayer>): IState {
    var boardTiles: MutableList<MutableList<ITileModel>> = mutableListOf()
    var spareTile: ITileModel = EmptyTile()
    val playerTruthData: MutableList<PlayerGroundTruthData> = mutableListOf()

    while (parser.hasNext()) {
        val element: JsonElement = parser.next()
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
                var playerListIndex = 0
                playersArray.forEach {
                    val playerObject = it.asJsonObject
                    val homePositionJsonObject = playerObject["home"].asJsonObject
                    val homePosition = Position(
                        homePositionJsonObject["row#"].asInt,
                        homePositionJsonObject["column#"].asInt
                    )
                    val treasurePositionJsonObject = playerObject["goto"].asJsonObject
                    val treasurePosition = Position(
                        treasurePositionJsonObject["row#"].asInt,
                        treasurePositionJsonObject["column#"].asInt
                    )
                    val currentPositionJsonObject = playerObject["current"].asJsonObject
                    val currentPosition = Position(
                        currentPositionJsonObject["row#"].asInt,
                        currentPositionJsonObject["column#"].asInt
                    )
                    val currentColor = playerObject["color"].asString
                    playerTruthData.add(
                        // some garbage data cuz not needed by this test
                        PlayerGroundTruthData(
                            name = players[playerListIndex].getPlayerName(),
                            homeTilePosition = homePosition,
                            treasurePosition = treasurePosition,
                            currentPosition = currentPosition,
                            reachedTreasure = false,
                            // Change back to black later
                            avatar = Avatar(getColor(currentColor)),
                            currentTarget = Position(-1, -1)
                        )
                    )
                    playerListIndex++
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
        activePlayer = playerTruthData[0]
    )
}

fun getNames(players: List<IPlayer>): List<String> {
    val names = mutableListOf<String>()
    players.forEach {
        names.add(it.getPlayerName())
    }

    return names
}

fun getColor(color: String): Color {
    return when(color) {
        "purple" -> Color(102, 0, 153)
        "orange" -> Color.orange
        "pink" -> Color.pink
        "red" -> Color.red
        "blue" -> Color.blue
        "green" -> Color.green
        "yellow" -> Color.yellow
        "white" -> Color.white
        "black" -> Color.black
        else -> Color.decode("#$color")
    }
}

