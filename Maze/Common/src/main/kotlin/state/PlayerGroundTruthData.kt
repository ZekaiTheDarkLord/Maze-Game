package state

import com.google.gson.JsonObject
import components.boards.Position
import java.awt.Color

/**
 * the ground-truth data of a player.
 * @param name: the name of the player
 * @param homeTilePosition: the home tile of the player.
 *      Position(-1, -1) indicates this tile is currently the spare tile.
 * @param targetTilePosition: the target tile of the player.
 *      Position(-1, -1) indicates this tile is currently the spare tile.
 * @param currentPosition: the current position of the player
 * @param reachedTarget: whether the player reached the target tile or not
 */
data class PlayerGroundTruthData(
    val name: String,
    var homeTilePosition: Position,
    var treasurePosition: Position,
    var currentPosition: Position,
    var reachedTreasure: Boolean,
    var currentTarget: Position,
    var avatar: Avatar
) {

    override fun toString(): String {
        return "$name $homeTilePosition $treasurePosition $currentPosition $reachedTreasure $currentTarget $avatar"
    }


    // modify the field of color
    fun toJson(): String {
        return "{\"current\": ${currentPosition.toJson()}, \"home\": ${homeTilePosition.toJson()}, \"color\": \"red\"}}"
    }

    companion object {

        //with dummy values
        fun fromJson(playerObject: JsonObject): PlayerGroundTruthData {
            val currentPositionJsonObject = playerObject["current"].asJsonObject
            val currentPosition = Position(
                currentPositionJsonObject["row#"].asInt,
                currentPositionJsonObject["column#"].asInt
            )
            val homePositionJsonObject = playerObject["home"].asJsonObject
            val homePosition = Position(
                homePositionJsonObject["row#"].asInt,
                homePositionJsonObject["column#"].asInt
            )
            return PlayerGroundTruthData(
                name = "null",
                homeTilePosition = homePosition,
                treasurePosition = Position(-1, -1),
                currentPosition = currentPosition,
                reachedTreasure = false,
                avatar = Avatar(Color(0, 0, 0)),
                currentTarget = Position(-1, -1)
            )
        }
    }
}