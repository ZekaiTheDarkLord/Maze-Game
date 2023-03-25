package observer

import components.tiles.ITileState
import components.tiles.TilePathDirection
import state.PlayerGroundTruthData
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JLabel

const val TILE_SIDE_LENGTH: Int = 80
const val PATH_LENGTH: Int = 10
const val GEM_SIDE_LENGTH = 15

/**
 * Draws an image of a tile based on its shape, rotation degree, and the gems.
 */
class TileLabel(
    private val tile: ITileState,
    private val gemImageMap: Map<String, BufferedImage>,
    private val playersOnTile: List<PlayerGroundTruthData>,
    private val homeOnTile: List<PlayerGroundTruthData>,
    private val treasuresOnTile: List<PlayerGroundTruthData>,
): JLabel() {

    private var image = BufferedImage(TILE_SIDE_LENGTH, TILE_SIDE_LENGTH, BufferedImage.TYPE_INT_RGB)
    private val graphics = image.graphics as Graphics2D

    init {
        image = draw()
        icon = ImageIcon(image)
    }

    override fun paintComponents(g: Graphics) {
        super.paintComponents(g)
        g.drawImage(image, 0, 0, TILE_SIDE_LENGTH, TILE_SIDE_LENGTH, this)
    }

    /**
     * Draws an image of the board
     */
    private fun draw(): BufferedImage {
        graphics.color = Color.gray

        // Draw Tile background
        graphics.fillRect(0, 0, TILE_SIDE_LENGTH, TILE_SIDE_LENGTH)

        // Draw connectors
        drawConnectors()

        // Draw Gems
        drawGems()

        // Draw player
        drawPlayer()

        // Draw player home, if any
        drawPlayerHome()

        // Draw player treasure
        drawPlayerTreasure()

        return image
    }

    private fun drawConnectors() {
        graphics.color = Color.cyan
        val pathDirections = tile.getPathDirections()
        pathDirections.forEach {
            when(it) {
                TilePathDirection.UP -> {
                    graphics.fillRect(
                        ((TILE_SIDE_LENGTH / 2) - (PATH_LENGTH / 2)),
                        0,
                        PATH_LENGTH,
                        (TILE_SIDE_LENGTH / 2),
                    )
                }
                TilePathDirection.DOWN -> {
                    graphics.fillRect(
                        ((TILE_SIDE_LENGTH / 2) - (PATH_LENGTH / 2)),
                        TILE_SIDE_LENGTH / 2,
                        PATH_LENGTH,
                        (TILE_SIDE_LENGTH / 2),
                    )
                }
                TilePathDirection.LEFT -> {
                    graphics.fillRect(
                        0,
                        ((TILE_SIDE_LENGTH / 2) - (PATH_LENGTH / 2)),
                        (TILE_SIDE_LENGTH / 2),
                        PATH_LENGTH,
                    )
                }
                TilePathDirection.RIGHT -> {
                    graphics.fillRect(
                        ((TILE_SIDE_LENGTH / 2) + (PATH_LENGTH / 2)),
                        ((TILE_SIDE_LENGTH / 2) - (PATH_LENGTH / 2)),
                        (TILE_SIDE_LENGTH / 2),
                        PATH_LENGTH,
                    )
                }
            }
        }
    }

    private fun drawGems() {
        val gems = tile.getGems()
        // resize gem1 and gem2
        val gem1OriginalImage = gemImageMap[gems[0].name]!!
        val gem2OriginalImage = gemImageMap[gems[1].name]!!
        val gem1Resized = BufferedImage(GEM_SIDE_LENGTH, GEM_SIDE_LENGTH, BufferedImage.TYPE_INT_RGB)
        val gem2Resized = BufferedImage(GEM_SIDE_LENGTH, GEM_SIDE_LENGTH, BufferedImage.TYPE_INT_RGB)
        val graph2D1 = gem1Resized.createGraphics()
        val graph2D2 = gem2Resized.createGraphics()
        graph2D1.drawImage(gem1OriginalImage, 0, 0, GEM_SIDE_LENGTH, GEM_SIDE_LENGTH, null)
        graph2D2.drawImage(gem2OriginalImage, 0, 0, GEM_SIDE_LENGTH, GEM_SIDE_LENGTH, null)
        graph2D1.dispose()
        graph2D2.dispose()

        // overlay
        graphics.drawImage(gem1Resized, 0, 0, null)
        graphics.drawImage(gem2Resized, GEM_SIDE_LENGTH, 0, null)
    }

    // Players are represented by a round rectangle with their name on it on the right-up quadrant
    private fun drawPlayer() {
        val arcSize = 5
        var offset = 0
        val offsetUnit = 10
            playersOnTile.forEach {
                graphics.color = it.avatar.color
                graphics.fillRoundRect(
                    (TILE_SIDE_LENGTH / 2) + (PATH_LENGTH / 2) + offset,
                    0,
                    PATH_LENGTH,
                    PATH_LENGTH,
                    arcSize,
                    arcSize
            )
            offset += offsetUnit
        }
    }

    // Represents a player's home. Displayed on the bottom-left quadrant.
    private fun drawPlayerHome() {
        var offset = 0
        val offsetUnit = 10
        homeOnTile.forEach {
            graphics.color = it.avatar.color
            graphics.fillOval(
                offset,
                TILE_SIDE_LENGTH - PATH_LENGTH,
                PATH_LENGTH,
                PATH_LENGTH,
            )
            offset += offsetUnit
        }
    }

    // Treasure are located at the bottom-right quadrant of the tile image
    private fun drawPlayerTreasure() {
        var offset = 0
        val offsetUnit = 10
        treasuresOnTile.forEach {
            graphics.color = it.avatar.color
            graphics.fillOval(
                TILE_SIDE_LENGTH / 2 + PATH_LENGTH / 2 + offset,
                TILE_SIDE_LENGTH - PATH_LENGTH,
                PATH_LENGTH,
                PATH_LENGTH,
            )
            offset += offsetUnit
        }
    }

}