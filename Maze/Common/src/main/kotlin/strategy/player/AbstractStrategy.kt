package strategy.player

import components.boards.Position
import components.boards.SlideDirection
import player_referee_protocal.PlayerAction
import player_referee_protocal.PlayerPass
import player_referee_protocal.PlayerSIM
import state.IState

abstract class AbstractStrategy @Throws(IllegalStateException::class) constructor(
    internal open var boardState: IState,
) : Strategy {
    protected val rowSize = boardState.getBoardRowSize()
    protected val columnSize = boardState.getBoardColumnSize()
    private val rotationDegree = listOf(0, 90, 180, 270)
    private val playerPos = boardState.getActivePlayerGroundTruth().currentPosition
    var stateBackUp = boardState.copyStateForStrategy()

    protected fun tryPrimaryAndSecondaryGoal(targetPos: Position): PlayerAction {
        val firstResult = firstTry(targetPos)

        return if (firstResult is PlayerPass) {
            secondTry(getPositionList())
        } else {
            firstResult
        }
    }

    abstract fun getPositionList(): MutableList<Position>

    protected fun firstTry(targetPos: Position): PlayerAction {
        boardState.setActivePlayerCurrentTarget(targetPos)
        stateBackUp = boardState.copyStateForStrategy()
        return tryAllSlides()
    }

    private fun secondTry(positionList: MutableList<Position>): PlayerAction {
        positionList.forEach {
            val rowIdx = it.rowIndex
            val columnIdx = it.columnIndex

            if (
                Position(rowIdx, columnIdx) !=
                boardState.getActivePlayerGroundTruth().currentTarget
            ) {
                boardState.setActivePlayerCurrentTarget(Position(rowIdx, columnIdx))
                stateBackUp = boardState.copyStateForStrategy()

                val result = tryAllSlides()

                if (result is PlayerSIM) {
                    return PlayerSIM(
                        result.rowOrColumnIdx,
                        result.slideDir,
                        result.rotateDegree,
                        Position(rowIdx, columnIdx)
                    )
                }
            }
        }

        return PlayerPass
    }

    // try all the slides horizontally and vertically
    protected fun tryAllSlides(): PlayerAction {
        for (i in 0..1) {

            var size = rowSize
            var directionPair = Pair(SlideDirection.LEFT, SlideDirection.RIGHT)

            if (i == 1) {
                size = columnSize
                directionPair = Pair(SlideDirection.UP, SlideDirection.DOWN)
            }

            for (idx in 0..size) {
                if (idx % 2 != 0) continue

                val firstTry = tryAllDegrees(
                    { boardState.slideAndInsertSpare(idx, directionPair.first) },
                    idx,
                    directionPair.first
                )

                if (firstTry is PlayerSIM) {
                    return firstTry
                } else {

                    val secondTry = tryAllDegrees(
                        { boardState.slideAndInsertSpare(idx, directionPair.second) },
                        idx,
                        directionPair.second
                    )

                    if (secondTry is PlayerSIM) {
                        return secondTry
                    }
                }
            }
        }

        return PlayerPass
    }

    // try all the degrees when inserting the spare tile for the given slide index and slide
    // direction
    private fun tryAllDegrees(
        slideAction: Runnable,
        slideIdx: Int,
        slideDir: SlideDirection,
    ): PlayerAction {
        val playerInfo = boardState.getActivePlayerGroundTruth()
        var savedPos =
            Position(playerInfo.currentTarget.rowIndex, playerInfo.currentTarget.columnIndex)

        rotationDegree.forEach {
            if (slideIdx == boardState.getLastSlide().first &&
                slideDir == SlideDirection.getReverseDirection(boardState.getLastSlide().second)
            ) {

            } else {
                try {
                    boardState.rotateSpare(it)

                    slideAction.run()

                    val reachablePositions = boardState.getActivePlayerReachableTilesPositions()

                    val containsTarget = reachablePositions.contains(savedPos)

                    if (containsTarget && boardState.getActivePlayerGroundTruth().currentPosition
                        != savedPos
                    ) {
                        return PlayerSIM(
                            slideIdx,
                            slideDir,
                            it,
                            savedPos
                        )
                    }
                } catch (e: IllegalStateException) {
                    // do nothing
                }

                restore()
                restorePlayerPos()
            }
        }


        return PlayerPass
    }

    // restore the changes made on the board
    @Throws(IllegalArgumentException::class)
    protected fun restore() {
        boardState = stateBackUp.copyStateForStrategy()
    }

    // restore the current player position to original position
    private fun restorePlayerPos() {
        // playerCurrentPos = Position(playerPos.rowIndex, playerPos.columnIndex)
        boardState.moveActivePlayerTo(playerPos)
    }
}