package server

import RemotePlayerProxy
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonStreamParser
import com.google.gson.JsonSyntaxException
import components.boards.Board
import components.boards.IBoard
import components.boards.Position
import integration_tests.game.getNames
import player.IPlayer
import referee.Referee
import state.Avatar
import state.PlayerGroundTruthData
import state.State
import strategy.board.SimpleGenerationStrategy
import java.awt.Color
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.system.exitProcess

const val WAITING_PERIOD = 20L
const val MINIMUM_PLAYERS = 2
const val MAXIMUM_PLAYERS = 6
const val DEFAULT_RANDOM_SEED = 88L

// requires [random: int, row: int, col: int, port: int]
fun main(args: Array<String>) {
    Server().run(args)
}

class Server {

    // requires [random: int, row: int, col: int, port: int]
    fun run(args: Array<String>): String {
        val server: ServerSocket

        try {
            val portNumber = args[3].toInt()
            server = ServerSocket(portNumber)
        } catch (e : Exception) {
            println("Invalid Input!")
            return("[ [], [] ]")
        }

        val clientSockets = mutableListOf<Pair<Socket, String>>()
        val execService = Executors.newCachedThreadPool()
        val random = Random(if (args.isEmpty()) DEFAULT_RANDOM_SEED else convertToLong(args[0]))
        val (row, col) = parseForIndex(args[1], args[2])
        waitForOnePeriod(clientSockets, server, execService)

        return if (clientSockets.size >= MINIMUM_PLAYERS) {
            playGame(clientSockets, random, row, col)
        } else {
            waitForOnePeriod(clientSockets, server, execService)
            if (clientSockets.size >= MINIMUM_PLAYERS) {
                playGame(clientSockets, random, row, col)
            } else {
                "[ [], [] ]"
            }
        }
    }

    private fun waitForOnePeriod(
        clientSockets: MutableList<Pair<Socket, String>>, server: ServerSocket,
        execService: ExecutorService
    ) {
        val futureResult = execService.submit<Unit> { trackClientsWhenGivenName(server, clientSockets, execService) }
        try {
            futureResult.get(WAITING_PERIOD, TimeUnit.SECONDS)
        } catch (te: TimeoutException) {
            println(clientSockets)
            return
        }
    }

    private fun trackClientsWhenGivenName(
        server: ServerSocket,
        clientSockets: MutableList<Pair<Socket, String>>,
        execService: ExecutorService
    ) {
        while (serverStillInWaitingState(clientSockets)) {
            try {
                val client = receiveClientAndName(server, execService)
                client?.let {
                    println("here $it.second")
                    synchronized(clientSockets) {
                        clientSockets.add(client)
                    }
                }
            } catch (timeoutException: TimeoutException) {
                continue
            }
        }
    }

    //hit the max players,
    private fun serverStillInWaitingState(clientSockets: MutableList<Pair<Socket, String>>): Boolean {
        return clientSockets.size < MAXIMUM_PLAYERS
    }

    private fun receiveClientAndName(server: ServerSocket, execService: ExecutorService): Pair<Socket, String>? {
        val future = execService.submit<Pair<Socket, String>> {
            val clientSocket = server.accept()
            println("client connected $clientSocket")
            val name = listenForName(clientSocket.getInputStream())
            name?.let { Pair(clientSocket, name) }
        }
        return future.get(2L, TimeUnit.SECONDS)
    }

    private fun listenForName(inputStream: InputStream): String? {
        val clientInputStreamReader = InputStreamReader(inputStream, "utf-8")
        val jsonStreamReader = JsonStreamParser(clientInputStreamReader)
        println("listening")
        while (jsonStreamReader.hasNext()) {
            println("received")
            val input = jsonStreamReader.next()
            println("received $input")
            val gson = Gson()
            return try {
                gson.fromJson(input, String::class.java)
            } catch (exception: JsonSyntaxException) {
                null
            }
        }
        throw IOException("No valid input coming from stream")
    }

    private fun playGame(clientSockets: List<Pair<Socket, String>>, random: Random, row: Int, col: Int): String {
        val board = SimpleGenerationStrategy().execute(row, col, Random(1))
        val listOfPlayerData = generatePlayerGroundTruthData(board, random, clientSockets)
        val state = State(board, listOfPlayerData.toMutableList(), activePlayer = listOfPlayerData[0])
        val playerAPIs: MutableList<IPlayer> =
            clientSockets.map { RemotePlayerProxy(it.first, it.second) }.toMutableList()
        val referee = Referee()
        val (winners, cheaters) = referee.playGame(playerAPIs, state, mutableListOf())
        return formatWinnersAndCheatersToJson(winners, cheaters)
    }

    private fun generatePlayerGroundTruthData(
        board: IBoard,
        random: Random,
        clientSockets: List<Pair<Socket, String>>
    ): List<PlayerGroundTruthData> {
        val randomHomeTiles =
            generateRandomPositionsOfSizeN(board, clientSockets.size, random, unique = true, fixed = true)
        val randomTreasureTiles =
            generateRandomPositionsOfSizeN(board, clientSockets.size, random, unique = false, fixed = true)
        val randomAvatars = generateUniqueRandomColorsOfSizeN(clientSockets.size, random)
        val listOfPlayerGroundTruthData = clientSockets.mapIndexed { index, (_, name) ->
            PlayerGroundTruthData(
                name,
                randomHomeTiles[index], randomTreasureTiles[index], randomHomeTiles[index], false,
                randomTreasureTiles[index], randomAvatars[index]
            )
        }
        return listOfPlayerGroundTruthData
    }

    private fun generateRandomPositionsOfSizeN(
        board: IBoard, size: Int, random: Random, unique: Boolean,
        fixed: Boolean
    ): List<Position> {
        val seenBeforePositions = mutableSetOf<Position>()
        val positions = mutableListOf<Position>()
        val rowMaxValue = board.getDimension().first
        val colMaxValue = board.getDimension().second

        var count = 0
        while (count < size) {
            val row = random.nextInt(rowMaxValue)
            val col = random.nextInt(colMaxValue)
            val position = Position(row, col)
            if (!seenBeforePositions.contains(position) && isValidPosition(fixed, board, position)) {
                if (unique) seenBeforePositions.add(position)
                positions.add(position)
                count++
            }
        }
        return positions
    }

    private fun generateUniqueRandomColorsOfSizeN(size: Int, random: Random): List<Avatar> {
        val createdColors = mutableSetOf<Color>()
        var count = 0
        while (count < size) {
            val color = Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))
            if (!createdColors.contains(color)) {
                createdColors.add(color)
                count++
            }
        }
        return createdColors.map { Avatar(it) }
    }

    private fun convertToLong(arg: String): Long {
        try {
            return arg.toLong()
        } catch (e: Exception) {
            println("Needs to be an number")
            exitProcess(1)
        }
    }

    private fun isValidPosition(fixed: Boolean, board: IBoard, position: Position): Boolean {
        return if (fixed) {
            try {
                board.isSlideable(position.columnIndex, board.getDimension().second)
                board.isSlideable(position.rowIndex, board.getDimension().first)
                true
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }

    private fun parseForIndex(arg1: String, arg2: String): Pair<Int, Int> {
        try {
            return Pair(arg1.toInt(), arg2.toInt())
        } catch (exception: Exception) {
            println("Row and column needs to be 2 numbers")
            exitProcess(1)
        }
    }

    private fun formatWinnersAndCheatersToJson(winners: List<IPlayer>, cheaters: List<IPlayer>): String {
        val namesOfWinners: MutableList<String> = getNames(winners) as MutableList<String>
        val namesOfCheaters: MutableList<String> = getNames(cheaters) as MutableList<String>
        namesOfWinners.sort()
        namesOfCheaters.sort()
        return Gson().toJson(listOf(namesOfWinners, namesOfCheaters))
    }
}



