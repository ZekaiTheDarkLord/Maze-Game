package strategy.board

import components.boards.Board
import components.boards.IBoard
import components.tiles.Gem
import components.tiles.ITileModel
import components.tiles.SimpleTile
import components.tiles.TileShape
import java.io.File
import java.util.*

/**
 * A strategy to create board where all tiles are selected at random.
 */
class SimpleGenerationStrategy: BoardGenerationStrategy {

    private val gemDirectoryPath = "../Resource/gems"
    private val gemPairList = getGemPairList()
    override fun execute(rows: Int, columns: Int, random: Random): IBoard {
        val tilesInBoard = mutableListOf<MutableList<ITileModel>>()

        for (i in 0 until rows) {
            val row = mutableListOf<ITileModel>()
            for (j in 0 until columns) {
                val newTile = generateRandomTile(random)
                row.add(newTile)
            }
            tilesInBoard.add(row)
        }

        return Board(
            board = tilesInBoard,
            spare = generateRandomTile(random)
        )
    }

    // Create a random tile.
    private fun generateRandomTile(random: Random): ITileModel {

        val tile: ITileModel
        val tileShapeDirectionCount = 4
        val shape = TileShape.values()[random.nextInt(TileShape.values().size)]
        val tileShapeDirection = random.nextInt(tileShapeDirectionCount)
        val gemIndex = random.nextInt(gemPairList.size)
        val gems = gemPairList[gemIndex]
        gemPairList.removeAt(gemIndex)
        tile = SimpleTile(
            tileShapeDirection = tileShapeDirection,
            tileShape = shape,
            gems = gems
        )
        return tile
    }

    // Create a list of unique Gem pairs
    private fun getGemPairList(): MutableList<Array<Gem>> {
        val gemPackage = File(this.gemDirectoryPath)
        val gemsList = mutableListOf<Gem>()

        // Put all gems into a list
        gemPackage.walkTopDown().forEach {
            var fileName = it.name
            // Mac specific. Cannot remove .DS_Store so ignore it
            if (!(fileName == ".DS_Store" || fileName.contains("gem"))) {
                val fileExtensionIndex = fileName.indexOf(".")
                fileName = fileName.substring(0, fileExtensionIndex)
                gemsList.add(Gem(fileName))
            }
        }
        // Create pairs
        val gemPairList = mutableListOf<Array<Gem>>()
        for (i in gemsList.indices) {
            for (j in i until gemsList.size) {
                gemPairList.add(arrayOf(gemsList[i], gemsList[j]))
            }
        }
        return gemPairList
    }
}