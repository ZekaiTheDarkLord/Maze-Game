package strategy.player
import player_referee_protocal.PlayerAction

/**
 * Represents the strategy player chose to play the game.
 */
interface Strategy {
    /**
     * Execute the strategy.
     */
    fun execute(): PlayerAction
}