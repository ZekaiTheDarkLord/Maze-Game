package components.boards

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import components.tiles.*
import integration_tests.board.getBoardTiles
import java.util.StringJoiner

// If the emptyTilePosition is INVALID_POSITION, the board is filled.
val INVALID_POSITION = Position(-1, -1)

/**
 * Represents a symmetric board of the Labyrinth game board.
 * @param board the board containing all the tiles
 * @throws IllegalArgumentException if board is empty, the board contains an empty tile, or the spare is empty
 */
class Board @Throws(IllegalArgumentException::class) constructor(
    private val board: MutableList<MutableList<ITileModel>>,
    private var spare: ITileModel
) : IBoard {

    // INVARIANT: if the board does not have an empty slot, _emptyTilePosition_ is _INVALID_POSITION_.
    //            else, the coordinate of the empty slot is _emptyTilePosition_
    private var emptyTilePosition: Position = INVALID_POSITION
    private val emptyTile: ITileModel = EmptyTile()

    init {
        if (board.isEmpty()) throw IllegalArgumentException("Board cannot be empty")
        board.forEach { it ->
            if (it.size == 0) {
                throw IllegalArgumentException("Board row cannot be empty")
            }
            it.forEach {
                if (it == EmptyTile())
                    throw IllegalArgumentException("Board cannot contain empty tile")
            }
        }
        if (spare == EmptyTile()) throw IllegalArgumentException("Spare tile cannot be empty")
    }

    override fun slideAndInsert(slideIndex: Int, slideDirection: SlideDirection) {
        when (slideDirection) {
            SlideDirection.UP, SlideDirection.DOWN -> {
                slideColumnAndInsert(slideIndex, slideDirection)
            }

            SlideDirection.LEFT, SlideDirection.RIGHT -> {
                slideRowAndInsert(slideIndex, slideDirection)
            }

            else -> {
                throw IllegalArgumentException("Illegal direction! Given: $slideDirection")
            }
        }
    }

    private fun slideRowAndInsert(rowIndex: Int, rowDirection: SlideDirection) {
        isSlideable(rowIndex, board.size)

        val newSpare: ITileModel

        if (rowDirection == SlideDirection.LEFT) {
            newSpare = board[rowIndex][0]
            shiftRowToLeft(rowIndex)
            setEmptyTilePosition(Position(rowIndex, board[rowIndex].size - 1))
        } else {
            newSpare = board[rowIndex][board[rowIndex].size - 1]
            shiftRowToRight(rowIndex)
            setEmptyTilePosition(Position(rowIndex, 0))
        }

        insertTile(spare)
        spare = newSpare
    }

    private fun slideColumnAndInsert(colIndex: Int, colDirection: SlideDirection) {
        isSlideable(colIndex, board[0].size)

        val newSpare: ITileModel

        if (colDirection == SlideDirection.UP) {
            newSpare = board[0][colIndex]
            shiftColumnToUp(colIndex)
            setEmptyTilePosition(Position(board.size - 1, colIndex))
        } else {
            newSpare = board[board.size - 1][colIndex]
            shiftColumnToDown(colIndex)
            setEmptyTilePosition(Position(0, colIndex))
        }

        insertTile(spare)
        spare = newSpare
    }

    @Throws(IllegalArgumentException::class)
    override fun getTileAt(position: Position): ITileState {
        val tile = board[position.rowIndex][position.columnIndex]
        return tile.deepCopy()
    }

    override fun getDimension(): Pair<Int, Int> {
        return Pair(board.size, board[0].size)
    }

    override fun getSpare(): ITileState {
        return spare.deepCopy()
    }

    override fun rotateSpare(degree: Int) {
        spare.rotate(degree)
    }

    override fun deepCopy(): IBoard {
        val boardCopy = mutableListOf<MutableList<ITileModel>>()

        board.forEach { it ->
            val rowCopy = mutableListOf<ITileModel>()
            it.forEach {
                rowCopy.add(it.deepCopy() as ITileModel)
            }
            boardCopy.add(rowCopy)
        }

        return Board(boardCopy, spare.deepCopy() as ITileModel)
    }

    override fun toJson(): String {
        val connectors = Gson().toJson((board.map { iTileModels -> iTileModels.map { it.toJson() } }))
        val treasures =
            Gson().toJson((board.map { ITileModels -> ITileModels.map { singlePairGemToList(it) } }))

        return """{"connectors": $connectors, "treasures": $treasures}""""
    }

    private fun singlePairGemToList(tile: ITileModel): List<String> {
        return tile.getGems().map { it.name }
    }

    override fun reachableFrom(position: Position): List<Position> {

        if (position.rowIndex >= board.size || position.rowIndex < 0 ||
            position.columnIndex >= board[0].size || position.columnIndex < 0
        ) {
            throw IllegalArgumentException("Starting position is out of bound")
        }

        val connectedTiles = mutableListOf<Position>()
        val visited = Array(board.size) {
            arrayOfNulls<Boolean>(
                board[0].size
            )
        }
        visited.forEach {
            it.fill(false)
        }

        board[position.rowIndex][position.columnIndex].getPathDirections().forEach {
            findReachableTiles(
                visited = visited,
                position = position,
                requiredDirection = it,
                connectedTiles = connectedTiles
            )
        }

        return ArrayList(connectedTiles)
    }

    @Throws(IllegalArgumentException::class)
    private fun findReachableTiles(
        visited: Array<Array<Boolean?>>,
        position: Position,
        requiredDirection: TilePathDirection,
        connectedTiles: MutableList<Position>
    ) {

        // Base case
        if (stopDFS(visited, position, requiredDirection)) {
            return
        }

        visited[position.rowIndex][position.columnIndex] = true
        val currentTile = getTileAt(position)
        connectedTiles.add(position)

        // Recursive case
        if (currentTile.getPathDirections().contains(TilePathDirection.UP)) {
            findReachableTiles(
                visited = visited,
                position = Position(position.rowIndex - 1, position.columnIndex),
                requiredDirection = TilePathDirection.DOWN,
                connectedTiles = connectedTiles
            )
        }

        if (currentTile.getPathDirections().contains(TilePathDirection.DOWN)) {
            findReachableTiles(
                visited = visited,
                position = Position(position.rowIndex + 1, position.columnIndex),
                requiredDirection = TilePathDirection.UP,
                connectedTiles = connectedTiles
            )
        }

        if (currentTile.getPathDirections().contains(TilePathDirection.LEFT)) {
            findReachableTiles(
                visited = visited,
                position = Position(position.rowIndex, position.columnIndex - 1),
                requiredDirection = TilePathDirection.RIGHT,
                connectedTiles = connectedTiles
            )
        }

        if (currentTile.getPathDirections().contains(TilePathDirection.RIGHT)) {
            findReachableTiles(
                visited = visited,
                position = Position(position.rowIndex, position.columnIndex + 1),
                requiredDirection = TilePathDirection.LEFT,
                connectedTiles = connectedTiles
            )
        }

    }

    private fun stopDFS(
        visited: Array<Array<Boolean?>>,
        position: Position,
        requiredDirection: TilePathDirection
    ): Boolean {
        return position.rowIndex < 0 || position.columnIndex < 0 || position.rowIndex >= board.size ||
                position.columnIndex >= board[position.rowIndex].size || visited[position.rowIndex][position.columnIndex]!! ||
                getTileAt(position) == EmptyTile() || !getTileAt(position).getPathDirections()
            .contains(requiredDirection)
    }


    private fun insertTile(newTile: ITileModel) {
        board[emptyTilePosition.rowIndex][emptyTilePosition.columnIndex] =
            newTile.deepCopy() as ITileModel
        setEmptyTilePosition(INVALID_POSITION)
    }

    // Validate if the slideIndex provided is valid
    @Throws(IllegalArgumentException::class)
    override fun isSlideable(slideIndex: Int, upperBound: Int) {
        if (slideIndex % 2 != 0) {
            throw IllegalArgumentException("slideIndex must be even. Given: $slideIndex")
        }

        if (slideIndex < 0 || slideIndex >= upperBound) {
            throw IllegalArgumentException(
                "SlideIndex provided out of bound. " +
                        "Given SlideIndex: $slideIndex, LowerBound: 0" +
                        ", UpperBound: $upperBound"
            )
        }
    }

    // Shift a given row to left
    private fun shiftRowToLeft(rowIndex: Int) {
        val row = board[rowIndex]

        for (i in row.indices) {
            if (i == 0) continue
            row[i - 1] = row[i]
        }

        row[row.size - 1] = emptyTile
    }

    // Shift a given row to right
    private fun shiftRowToRight(rowIndex: Int) {
        val row = board[rowIndex]

        for (i in row.indices.reversed()) {
            if (i == 0) continue
            row[i] = row[i - 1]
        }

        row[0] = emptyTile
    }

    // Shift a given column to up
    private fun shiftColumnToUp(colIndex: Int) {
        for (i in board.indices) {
            if (i == board.size - 1) continue
            board[i][colIndex] = board[i + 1][colIndex]
        }

        board[board.size - 1][colIndex] = emptyTile
    }

    // Shift a given column to down
    private fun shiftColumnToDown(colIndex: Int) {
        for (i in board.indices.reversed()) {
            if (i == 0) continue
            board[i][colIndex] = board[i - 1][colIndex]
        }

        board[0][colIndex] = emptyTile
    }

    private fun setEmptyTilePosition(position: Position) {
        emptyTilePosition = position
    }


    companion object {
        fun fromJson(boardObject: JsonElement, tileObject: JsonElement): Board {
            return Board(getBoardTiles(
                boardObject.asJsonObject.get("connectors").asJsonArray,
                boardObject.asJsonObject.get("treasures").asJsonArray
            ), SimpleTile.fromJson(tileObject.asJsonObject))
        }
    }

}