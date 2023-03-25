package integration_tests.board

import com.google.gson.*
import components.boards.Position
import components.boards.Board
import components.boards.IBoard
import components.tiles.Gem
import components.tiles.*
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

// Integration tests for board
fun main() {
    // Read from STDIN and convert to desired form
    val initializationData = scanJsonBoard(System.`in`)
    val tiles = initializationData.boardTiles
    // The spare tile is not used for this integration test
    val board: IBoard = Board(tiles, SimpleTile(arrayOf(
        Gem("black-obsidian"),
        Gem("black-obsidian1")
    ), TileShape.T_JUNCTION, 0))
    val sourceTilePosition = initializationData.startingPosition

    // Run function to be tested
    val reachedTiles = board.reachableFrom(sourceTilePosition)

    // Convert result to desired form
    val result = getResultArray(reachedTiles)
    println(result)

}

// Get board initialization data
fun scanJsonBoard(inputStream: InputStream): BoardITestInitializationData {

    var boardTiles = mutableListOf<MutableList<ITileModel>>()
    var startPosition = Position(-1, -1)
    val parser = JsonStreamParser(InputStreamReader(inputStream))

    while(parser.hasNext()) {

        val element: JsonElement = parser.next()
        if (element.isJsonObject) {
            if (element.asJsonObject.has("connectors") &&
                element.asJsonObject.has("treasures")) {
                // Get tile and treasures
                boardTiles = getBoardTiles(
                    element.asJsonObject.get("connectors").asJsonArray,
                    element.asJsonObject.get("treasures").asJsonArray
                )
            } else if (element.asJsonObject.has("row#") &&
                element.asJsonObject.has("column#")) {
                // Set staring position
                startPosition = Position(
                    element.asJsonObject.get("row#").asInt,
                    element.asJsonObject.get("column#").asInt
                )
            }

        }
    }

    return BoardITestInitializationData(boardTiles, startPosition)
}


fun getBoardTiles(connectors: JsonArray, treasures: JsonArray): MutableList<MutableList<ITileModel>> {

    val boardTiles = mutableListOf<MutableList<ITileModel>>()

    for (i in 0 until connectors.size()) {
        val currentRowConnectors = connectors.get(i).asJsonArray
        val currentRowGems = treasures.get(i).asJsonArray
        val rowTiles = mutableListOf<ITileModel>()
        for (j in 0 until currentRowConnectors.size()) {
            val connector = currentRowConnectors.get(j).asString
            val gems = arrayOf(
                Gem(currentRowGems.get(j).asJsonArray.get(0).asString),
                Gem(currentRowGems.get(j).asJsonArray.get(1).asString)
            )
            val tile = SimpleTile(
                gems = gems,
                tileShape = getTileShapeMap()[connector]!!,
                tileShapeDirection = getTileDirectionMap()[connector]!!,
            )
            rowTiles.add(tile)
        }
        boardTiles.add(rowTiles)
    }
    return boardTiles
}

// Convert the result to the desired form
fun getResultArray(reachedTiles: List<Position>): JsonArray {
    val rowColumnOrder: Comparator<Position> = compareBy<Position> {
        it.rowIndex
    }.thenComparator { a, b -> compareValues(a.columnIndex, b.columnIndex) }



    Collections.sort(reachedTiles, rowColumnOrder)
    val gson = Gson()
    val resulArray = JsonArray()
    reachedTiles.forEach {
        val data = "{ \"row#\": ${it.rowIndex}, \"column#\": ${it.columnIndex} }"
        resulArray.add(gson.fromJson(data, JsonObject::class.java))
    }
    return resulArray
}

fun getTileShapeMap(): Map<String, TileShape> {
    val tileShapeMap: HashMap<String, TileShape> = HashMap()
    tileShapeMap["│"] = TileShape.STRAIGHT
    tileShapeMap["─"] = TileShape.STRAIGHT
    tileShapeMap["┐"] = TileShape.RIGHT_ANGLE
    tileShapeMap["└"] = TileShape.RIGHT_ANGLE
    tileShapeMap["┌"] = TileShape.RIGHT_ANGLE
    tileShapeMap["┘"] = TileShape.RIGHT_ANGLE
    tileShapeMap["┬"] = TileShape.T_JUNCTION
    tileShapeMap["├"] = TileShape.T_JUNCTION
    tileShapeMap["┴"] = TileShape.T_JUNCTION
    tileShapeMap["┤"] = TileShape.T_JUNCTION
    tileShapeMap["┼"] = TileShape.CROSS

    return tileShapeMap
}

fun getTileDirectionMap(): Map<String, Int> {
    val tileDirectionMap: HashMap<String, Int> = HashMap()
    tileDirectionMap["│"] = 0
    tileDirectionMap["─"] = 1
    tileDirectionMap["┐"] = 0
    tileDirectionMap["┘"] = 1
    tileDirectionMap["└"] = 2
    tileDirectionMap["┌"] = 3
    tileDirectionMap["┬"] = 0
    tileDirectionMap["┤"] = 1
    tileDirectionMap["┴"] = 2
    tileDirectionMap["├"] = 3
    tileDirectionMap["┼"] = 0

    return tileDirectionMap
}


