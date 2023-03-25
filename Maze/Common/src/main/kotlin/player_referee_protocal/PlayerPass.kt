package player_referee_protocal

/**
 * Represents the pass action of players.
 */
object PlayerPass: PlayerAction {
    override fun toJson(): String {
        return "PASS"
    }
}
