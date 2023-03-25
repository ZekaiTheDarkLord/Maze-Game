package components.tiles

import com.google.gson.JsonObject
import components.tiles.TilePathDirection.Companion.parseDegreeToDirection
import integration_tests.board.getTileDirectionMap
import integration_tests.board.getTileShapeMap
import java.util.*

/**
 * Represents a very simple tile.
 * @param gem the gem in this tile
 * @param tileShape the shape of this tile
 * @param tileShapeDirection the starting direction of this tile. The tileShapeDirection is a natural
 * number of range [0, 3]. 0 represents the direction of the default tileShape. 1 represents a 90-degree
 * clockwise rotation on the default tileShape. The same idea applies to 2 and 3.
 * @throws IllegalArgumentException if tileShapeDirection is not in range of 0 to 3
 */
class SimpleTile @Throws(IllegalArgumentException::class) constructor(
    private val gems: Array<Gem>,
    private val tileShape: TileShape,
    private var tileShapeDirection: Int
) : ITileModel {

    init {
        if (tileShapeDirection > 3 || tileShapeDirection < 0) {
            throw IllegalArgumentException(
                "Invalid tile shape direction: Should given a number " +
                        "between 0 to 3"
            )
        }
    }

    override fun getPathDirections(): Collection<TilePathDirection> {
        val listDirection: Set<TilePathDirection>

        when (tileShape) {
            TileShape.STRAIGHT -> {
                listDirection = setOf(
                    getPathDirectionsHelper(TilePathDirection.UP, tileShapeDirection),
                    getPathDirectionsHelper(TilePathDirection.DOWN, tileShapeDirection)
                )
            }
            TileShape.RIGHT_ANGLE -> {
                listDirection = setOf(
                    getPathDirectionsHelper(TilePathDirection.LEFT, tileShapeDirection),
                    getPathDirectionsHelper(TilePathDirection.DOWN, tileShapeDirection)
                )
            }
            TileShape.T_JUNCTION -> {
                listDirection = setOf(
                    getPathDirectionsHelper(TilePathDirection.LEFT, tileShapeDirection),
                    getPathDirectionsHelper(TilePathDirection.RIGHT, tileShapeDirection),
                    getPathDirectionsHelper(TilePathDirection.DOWN, tileShapeDirection)
                )
            }
            TileShape.CROSS -> {
                listDirection = setOf(
                    getPathDirectionsHelper(TilePathDirection.UP, tileShapeDirection),
                    getPathDirectionsHelper(TilePathDirection.DOWN, tileShapeDirection),
                    getPathDirectionsHelper(TilePathDirection.LEFT, tileShapeDirection),
                    getPathDirectionsHelper(TilePathDirection.RIGHT, tileShapeDirection)
                )
            }
        }

        return listDirection
    }

    @Throws(IllegalArgumentException::class)
    override fun rotate(totalRotationDegree: Int) {
        if (totalRotationDegree % 90 != 0) {
            throw IllegalArgumentException("Degree $totalRotationDegree is not a multiple of 90.")
        }
        when (totalRotationDegree % 360) {
            0 -> return
            90 -> tileShapeDirection += parseDegreeToDirection(90)
            180 -> tileShapeDirection += parseDegreeToDirection(180)
            270 -> tileShapeDirection += parseDegreeToDirection(270)
        }
        tileShapeDirection %= 4
    }

    override fun getGems(): Array<Gem> {
        return gems
    }

    override fun deepCopy(): ITileState {
        return SimpleTile(arrayOf(gems[0], gems[1]), tileShape, tileShapeDirection)
    }

    /**
     * Represents the shape of a tile.
     * Here are the visualized TileShapes.
     * STRAIGHT = │
     * RIGHT_ANGLE = ┐
     * T_JUNCTION = ┬
     * CROSS = ┼
     * INVALID = a placeholder shape.
     */

    override fun toJson(): String {
        when (tileShape) {
            TileShape.STRAIGHT -> {
                return when(tileShapeDirection) {
                    0 -> "│"
                    1 -> "─"
                    2 -> "│"
                    3 -> "─"
                    else -> {throw java.lang.IllegalArgumentException("Wrong rotation degree")}
                }
            }

            TileShape.RIGHT_ANGLE -> {
                return when(tileShapeDirection) {
                    0 -> "┐"
                    1 -> "┘"
                    2 -> "└"
                    3 -> "┌"
                    else -> {throw java.lang.IllegalArgumentException("Wrong rotation degree")}
                }
            }

            TileShape.T_JUNCTION -> {
                return when(tileShapeDirection) {
                    0 -> "┬"
                    1 -> "┤"
                    2 -> "┴"
                    3 -> "├"
                    else -> {throw java.lang.IllegalArgumentException("Wrong rotation degree")}
                }
            }

            TileShape.CROSS -> {
                return "┼"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SimpleTile) return false

        val comparisonTile: SimpleTile = other

        return comparisonTile.tileShape == this.tileShape &&
                comparisonTile.tileShapeDirection == this.tileShapeDirection &&
                comparisonTile.getGems().contains(gems[0]) &&
                comparisonTile.getGems().contains(gems[1]) &&
                getGems().contains(comparisonTile.getGems()[0]) &&
                getGems().contains(comparisonTile.getGems()[1])
    }

    override fun hashCode(): Int {
        return Objects.hash(this.tileShape.hashCode(), this.tileShapeDirection.hashCode(), gems[0].hashCode(), gems[1].hashCode())
    }

    @Throws(IllegalArgumentException::class)
    private fun getPathDirectionsHelper(
        openPathDirections: TilePathDirection,
        totalRotationDegree: Int
    ): TilePathDirection {
        val possibleRotationCount = 4

        var rotationCount = totalRotationDegree % possibleRotationCount
        // convert counter-clockwise rotation to clockwise rotation
        if (rotationCount < 0) rotationCount = possibleRotationCount - (rotationCount * -1)

        return getRotationMap()[openPathDirections]?.get(rotationCount)!!
    }

    private fun getRotationMap(): Map<TilePathDirection, List<TilePathDirection>> {
        val upCycle = listOf(
            TilePathDirection.UP,
            TilePathDirection.RIGHT,
            TilePathDirection.DOWN,
            TilePathDirection.LEFT
        )

        val rightCycle = listOf(
            TilePathDirection.RIGHT,
            TilePathDirection.DOWN,
            TilePathDirection.LEFT,
            TilePathDirection.UP
        )

        val downCycle = listOf(
            TilePathDirection.DOWN,
            TilePathDirection.LEFT,
            TilePathDirection.UP,
            TilePathDirection.RIGHT
        )

        val leftCycle = listOf(
            TilePathDirection.LEFT,
            TilePathDirection.UP,
            TilePathDirection.RIGHT,
            TilePathDirection.DOWN
        )

        return mapOf(
            TilePathDirection.UP to upCycle,
            TilePathDirection.RIGHT to rightCycle,
            TilePathDirection.DOWN to downCycle,
            TilePathDirection.LEFT to leftCycle
        )
    }

    companion object {
        fun fromJson(jsonObject: JsonObject): ITileModel {
            val spareObject = jsonObject["spare"].asJsonObject
            return SimpleTile(
                gems = arrayOf(
                    Gem(spareObject["1-image"].asString),
                    Gem(spareObject["2-image"].asString)
                ),
                tileShape = getTileShapeMap()[spareObject["tilekey"].asString]!!,
                tileShapeDirection = getTileDirectionMap()[spareObject["tilekey"].asString]!!,
            )
        }
    }

}