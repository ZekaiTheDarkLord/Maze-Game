package integration_tests.board

import components.boards.Position
import components.tiles.ITileModel

/**
 * Represents the data used by the integration tests for board.
 */
data class BoardITestInitializationData(
    val boardTiles: MutableList<MutableList<ITileModel>>,
    val startingPosition: Position
)
