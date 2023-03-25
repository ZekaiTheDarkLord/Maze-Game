package referee

import player.IPlayer

/**
 * Represents data recorded for playing a round of game.
 */
data class RoundData(
    var allPlayersPass: Boolean = false,
    var cheaters: MutableList<IPlayer> = mutableListOf(),
    var winners: MutableList<IPlayer> = mutableListOf(),
    val activePlayers: MutableList<IPlayer> = mutableListOf(),
)
