package observer

import components.boards.Position
import convertGameStateToJSON
import getGemImage
import state.IState
import state.PlayerGroundTruthData
import java.awt.Component
import java.awt.Graphics2D
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.swing.*

/**
 * A GUI based observer. Offers functionalities to view the next available state and save the current state into a file.
 * @param initialState the state to start with.
 */
class Observer(
    initialState: IState
): IObserver, ActionListener {

    private val gameStates = mutableListOf(initialState)
    private var stateIndex = 0
    private val bufferedImages = mutableMapOf<String, BufferedImage>()
    private val actionMap = mutableMapOf<String, Runnable>()
    private val gemDirectoryPath = "../Resource/gems"
    private var isGameOver = false

    // GUI fields
    private val window = JFrame("Labyrinth GUI Observer")
    private lateinit var boardPanel: JPanel
    private lateinit var sparePanel: JPanel
    private lateinit var dataPanel: JPanel
    private lateinit var buttonPanel: JPanel

    init {
        // Get all required images
        loadBufferImages()

        // Handle Actions
        actionMap["next state"] = Runnable { handleNextBtn() }
        actionMap["save state"] = Runnable { handleSaveBtn() }

        // Init Frame
        window.isResizable = true
        window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        // Init GUI
        updateGUI()
    }

    override fun receiveState(newState: IState) {
        if (!isGameOver) {
            gameStates.add(newState.copyStateForStrategy())
        }
    }

    override fun gameEnds() {
        isGameOver = true
    }

    override fun actionPerformed(e: ActionEvent) {
        val command = e.actionCommand
        actionMap[command]!!.run()
    }


    // Initialize the Map of name and BufferedImage
    @Throws(IllegalStateException::class)
    private fun loadBufferImages() {
        val directory = File(gemDirectoryPath)
        val usedGemNameList = mutableSetOf<String>()
        val currentState = gameStates[stateIndex]
        for (i in 0 until currentState.getBoardRowSize()) {
            for (j in 0 until currentState.getBoardColumnSize()) {
                val gemPair = currentState.getTileAt(Position(i, j)).getGems()
                for (gem in gemPair) {
                    usedGemNameList.add(gem.name)
                }
            }
        }
        currentState.getSpareTile().getGems().forEach {
            usedGemNameList.add(it.name)
        }
        directory.walkTopDown().forEach {
            if (it.nameWithoutExtension in usedGemNameList) {
                try {
                    bufferedImages[it.nameWithoutExtension] = getGemImage(it)
                } catch (e: IOException) {
                    throw IllegalStateException(e.message)
                }
            }
        }
    }

    private fun updateGUI() {
        // Init Board
        boardPanel = updateBoard()
        boardPanel.layout = BoxLayout(boardPanel, BoxLayout.Y_AXIS)
        boardPanel.alignmentX = Component.LEFT_ALIGNMENT
        boardPanel.isVisible = true

        // Draw Spare Tile
        sparePanel = updateSpareTilePanel()
        sparePanel.layout = BoxLayout(sparePanel, BoxLayout.Y_AXIS)
        sparePanel.isVisible = true

        // Render State Data
        dataPanel = updatePlayerData()
        dataPanel.layout = BoxLayout(dataPanel, BoxLayout.Y_AXIS)
        dataPanel.isVisible

        // Setup Buttons
        buttonPanel = setupButtons()
        buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.Y_AXIS)
        buttonPanel.isVisible

        val contentPanel = window.contentPane
        contentPanel.removeAll()
        contentPanel.layout = BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        contentPanel.add(boardPanel)
        contentPanel.add(sparePanel)
        contentPanel.add(buttonPanel)
        contentPanel.add(dataPanel)
//        contentPanel.add(buttonPanel)
        window.pack()
        window.isVisible = true
    }

    private fun updateBoard(): JPanel {
        val newJPanel = JPanel()
        val boardBoarder = BorderFactory.createTitledBorder("Game board")
        newJPanel.border = boardBoarder
        val currentState = gameStates[stateIndex]
        val playersData = currentState.getAllPlayerGroundTruthData()
        for (i in 0 until currentState.getBoardRowSize()) {
            val rowPanel = JPanel()
            for (j in 0 until currentState.getBoardColumnSize()) {
                // Draw a tile
                val playersOnTile = mutableListOf<PlayerGroundTruthData>()
                val homeOnTile = mutableListOf<PlayerGroundTruthData>()
                val treasureOnTile = mutableListOf<PlayerGroundTruthData>()
                val currentPosition = Position(i, j)
                playersData.forEach {
                    if (it.currentPosition == currentPosition) playersOnTile.add(it)
                    if (it.homeTilePosition == currentPosition) homeOnTile.add(it)
                    if (it.treasurePosition == currentPosition) treasureOnTile.add(it)
                }
                val tileLabel = TileLabel(
                    tile = currentState.getTileAt(Position(i, j)),
                    gemImageMap = bufferedImages,
                    playersOnTile = playersOnTile,
                    homeOnTile = homeOnTile,
                    treasuresOnTile = treasureOnTile
                )
                tileLabel.isVisible = true
                rowPanel.add(tileLabel)
            }
            rowPanel.isVisible = true
            newJPanel.add(rowPanel)
        }
        newJPanel.isVisible = true
        return newJPanel
    }

    private fun updateSpareTilePanel(): JPanel {
        val currentState = gameStates[stateIndex]
        val playersData = currentState.getAllPlayerGroundTruthData()
        val sparePanel = JPanel()
        val boardBoarder = BorderFactory.createTitledBorder("Spare Tile")
        sparePanel.border = boardBoarder
        val playersOnTile = mutableListOf<PlayerGroundTruthData>()
        val homeOnTile = mutableListOf<PlayerGroundTruthData>()
        val treasureOnTile = mutableListOf<PlayerGroundTruthData>()
        val outOfBoardPosition = Position(-1, -1)
        playersData.forEach {
            if (it.currentPosition == outOfBoardPosition) playersOnTile.add(it)
            if (it.homeTilePosition == outOfBoardPosition) homeOnTile.add(it)
            if (it.treasurePosition == outOfBoardPosition) treasureOnTile.add(it)
        }
        val spareLabel = TileLabel(
            tile = currentState.getSpareTile(),
            gemImageMap = bufferedImages,
            playersOnTile = playersOnTile,
            homeOnTile = homeOnTile,
            treasuresOnTile = treasureOnTile
        )
        spareLabel.isVisible = true
        sparePanel.add(spareLabel)
        sparePanel.isVisible = true
        return sparePanel
    }

    // Under development
    private fun updatePlayerData(): JPanel {
        val currentState = gameStates[stateIndex]
        val playersData = currentState.getAllPlayerGroundTruthData()
        val dataPanel = JPanel()
        val boardBoarder = BorderFactory.createTitledBorder("State data")
        val lineSeparator = "----------------------------------------------"
        dataPanel.border = boardBoarder
        // Last move
        val lastSlideLabel = JLabel()
        val lastSlide = currentState.getLastSlide()
        val lastSlideInfoText = "<html>$lineSeparator---<br/>" +
                "Last move:<br/>" +
                "rowOrColumn: ${lastSlide.first} <br/>" +
                "direction: ${lastSlide.second}<br/></html>"
        lastSlideLabel.text = lastSlideInfoText
        lastSlideLabel.isVisible = true
        dataPanel.add(lastSlideLabel)
        // Active player
        // Current slightly bugged. Will fix in the next milestone
        /*
        val activePlayerLabel = JLabel()
        val activePlayerAvatar = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        val individualGraph2D = (activePlayerAvatar.graphics as Graphics2D)
        individualGraph2D.color = currentState.getActivePlayerGroundTruth().avatar.color
        individualGraph2D.fillRoundRect(0, 0, 10, 10, 5, 5)
        activePlayerLabel.icon = ImageIcon(activePlayerAvatar)
        activePlayerLabel.text = "<html>$lineSeparator<br/>" +
                "Active player: ${currentState.getActivePlayerGroundTruth().name}<br/></html>"
        activePlayerLabel.isVisible = true
        dataPanel.add(activePlayerLabel)*/
        // Each player
        playersData.forEach {
            val individualDataLabel = JLabel()
            val avatarImage = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
            val graph2D = (avatarImage.graphics as Graphics2D)
            graph2D.color = it.avatar.color
            graph2D.fillRoundRect(0, 0, 10, 10, 5, 5)
            individualDataLabel.icon = ImageIcon(avatarImage)
            val playerDataTextBuilder = StringBuilder()
            playerDataTextBuilder.append("<html>$lineSeparator<br/>")
            playerDataTextBuilder.append("Name: ${it.name}<br/>")
            playerDataTextBuilder.append("Current Position: (${it.currentPosition})<br/>")
            playerDataTextBuilder.append("Home Position: (${it.homeTilePosition})<br/>")
            playerDataTextBuilder.append("Treasure Position: (${it.treasurePosition})<br/>")
            playerDataTextBuilder.append("<html>Been to Treasure: ${it.reachedTreasure}<br/></html>")
            individualDataLabel.text = playerDataTextBuilder.toString()
            individualDataLabel.isVisible = true
            dataPanel.add(individualDataLabel)
        }

        dataPanel.isVisible = true
        return dataPanel
    }

    private fun setupButtons(): JPanel {
        val btnPanel = JPanel()

        val nextBtm = JButton("next")
        val saveBtn = JButton("save")

        nextBtm.addActionListener(this)
        nextBtm.actionCommand = "next state"
        saveBtn.addActionListener(this)
        saveBtn.actionCommand = "save state"

        nextBtm.isVisible = true
        saveBtn.isVisible = true
        btnPanel.add(nextBtm)
        btnPanel.add(saveBtn)
        return btnPanel
    }

    private fun handleNextBtn() {
        if (stateIndex == gameStates.size - 1) {
            return
        }
        stateIndex++
        updateGUI()
    }

    private fun handleSaveBtn() {
        try {
            val fileName = "gameState"
            val fileExtension = "json"
            val fileChooser = JFileChooser()
            val stateJSON = convertGameStateToJSON(gameStates[stateIndex])
            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            fileChooser.showOpenDialog(this.window)
            val selectedFile = fileChooser.selectedFile
            val fullPath = "${selectedFile.absolutePath}${File.separator}$fileName.$fileExtension"
            val file = File(fullPath)
            file.writeText(stateJSON)
            JOptionPane.showMessageDialog(this.window, "Successfully save the gameState")
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(this.window, "Failed to save gameState")
        }
    }

}