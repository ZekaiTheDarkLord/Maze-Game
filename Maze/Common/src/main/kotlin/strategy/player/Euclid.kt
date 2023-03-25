package strategy.player

import components.boards.Position
import player_referee_protocal.PlayerAction
import state.IState
import java.lang.StrictMath.pow
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.sqrt

/**
 * Similar with Rieamnn but orders the alternative goal candidates according to their
 * Euclidean distance to the original goal tile.
 * To break ties (among candidates with equal distances to the original goal), it uses the
 * lexicographical (row-column) ordering of the first strategy.
 *
 * @param board: represents the board algorithm will execute on
 * @param targetPos: the target position of the player
 */
class Euclid @Throws(IllegalStateException::class) constructor(
    boardState: IState,
    private var targetPos: Position
) : AbstractStrategy(boardState) {
    private val distanceQueue: PriorityQueue<Double> = PriorityQueue<Double>()
    private val distanceToPositions: HashMap<Double, MutableList<Position>> = HashMap()

    // execute the given strategy
    override fun execute(): PlayerAction {
        return tryPrimaryAndSecondaryGoal(targetPos)
    }

    // give out a hashmap which has distance to Position
    // save all the distance into a priority queue
    private fun getDistanceMap() {
        for (rowIdx in 0 until rowSize) {
            for (columnIdx in 0 until columnSize) {
                val distance = euclidDirectionToTarget(rowIdx, columnIdx)
                if (distanceToPositions.containsKey(distance)) {
                    distanceToPositions[distance]?.add(Position(rowIdx, columnIdx))
                } else {
                    distanceToPositions[distance] = mutableListOf(Position(rowIdx, columnIdx))
                    distanceQueue.add(distance)
                }
            }
        }

        // note the lists inside the hashmap is unsorted
    }

    override fun getPositionList(): MutableList<Position> {
        getDistanceMap()
        val returnList = mutableListOf<Position>()

        while (distanceQueue.isNotEmpty()) {
            val it = distanceQueue.poll()
            val positions = distanceToPositions[it]

            val rowColumnOrder: Comparator<Position> = compareBy<Position> {
                it.rowIndex
            }.thenComparator { a, b -> compareValues(a.columnIndex, b.columnIndex) }

            Collections.sort(positions as MutableList<Position>, rowColumnOrder)

            positions.forEach {
                returnList.add(it)
            }
        }

        return returnList
    }

    private fun euclidDirectionToTarget(rowIdx: Int, columnIdx: Int): Double {
        return sqrt(
            pow((targetPos.columnIndex - columnIdx).toDouble(), 2.0) +
                    pow((targetPos.rowIndex - rowIdx).toDouble(), 2.0)
        )
    }
}