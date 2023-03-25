package strategy.player

import components.boards.Position
import player_referee_protocal.PlayerAction
import state.IState

/**
 * If the player can successfully reach the desired goal, obviously they should choose that.
 *
 * If it is impossible to reach the gaol tile, the player should also explore some alternative
 * goals. For this strategy, the player enumerates all tiles in row-column order, starting
 * from the top-most, left-most one and chooses those as alternative goals;
 *
 * Then, for each enumerated tile, in order, the strategy first starts from the top of the board
 * and tries sliding each successive possible row (trying to slide it left first, then right),
 * before next starting from the left of the board and trying each possible column (again trying
 * to slide each one up first, then down).
 *
 * For each sliding action, it experiments with all possible rotations of the spare tile—in the
 * order of Degrees listed—and tries inserting the tile into the freed-up spot.
 * Finally, for each of these actions (sliding plus rotating and inserting), it checks whether
 * the chosen goal (original or alternative) tile becomes reachable.
 *
 * If so, it stops the search and chooses the first such successful action.
 *
 * Otherwise, if none of these possibilities enables the player to reach any goal tile
 * (original or alternative), the player must pass on this opportunity to take a turn.
 *
 * @param board: represents the board algorithm will execute on
 * @param targetPos: the target position of the player
 */
class Riemann @Throws(IllegalStateException::class) constructor(
    boardState: IState,
    private var targetPos: Position
) : AbstractStrategy(boardState) {
    // execute the given strategy
    override fun execute(): PlayerAction {
        return tryPrimaryAndSecondaryGoal(targetPos)
    }

    override fun getPositionList(): MutableList<Position> {
        val returnList = mutableListOf<Position>()

        for (rowIdx in 0 until rowSize) {

            for (columnIdx in 0 until columnSize) {

                if (
                    Position(rowIdx, columnIdx) !=
                    boardState.getActivePlayerGroundTruth().currentTarget
                ) {
                    returnList.add(Position(rowIdx, columnIdx))
                }
            }
        }

        return returnList
    }
}