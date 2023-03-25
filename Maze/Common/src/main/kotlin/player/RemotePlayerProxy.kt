import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonStreamParser
import components.boards.IBoard
import components.boards.Position
import components.boards.SlideDirection
import components.tiles.TilePathDirection
import player.IPlayer
import player_referee_protocal.PlayerAction
import player_referee_protocal.PlayerPass
import player_referee_protocal.PlayerSIM
import state.IState
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.lang.IllegalArgumentException
import java.net.Socket

class RemotePlayerProxy(socket: Socket, private val name: String): IPlayer {
    private val inputStreamReader = InputStreamReader(socket.getInputStream())
    private val outputStreamWriter = OutputStreamWriter(socket.getOutputStream())
    private val jsonStreamReader = JsonStreamParser(inputStreamReader)
    private val gson = Gson()

    @Throws(IllegalArgumentException::class)
    override fun proposeBoard0(rows: Int, columns: Int): IBoard {
        TODO("Not yet implemented")
    }

    @Throws(IllegalArgumentException::class)
    override fun setup(state0: IState?, goal: Position) {
        val stateOrFalse = state0?.let { state0.toJson()} ?: "false"
        writeToOutput("setup", listOf(stateOrFalse, goal.toJson()))
        val response = gson.fromJson(jsonStreamReader.next(), String::class.java).toString() //TODO FACTOR OUT
        if (response == "void") {
            return
        }
        throw IllegalArgumentException("Did not return void")
    }

    @Throws(IllegalArgumentException::class)
    override fun takeTurn(s: IState): PlayerAction {
        writeToOutput("take-turn", listOf(s.toJson()))
        val nextEl = jsonStreamReader.next()
        return try {
            parseForPass(nextEl)
        } catch (exception: Exception) {
            try {
                parseForChoice(nextEl)
            } catch (e: Exception) {
                throw IllegalArgumentException("Neither pass or choice. Misbehaving player.")
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    override fun informWon(w: Boolean) {
        writeToOutput("win", listOf(w.toString()))
        val response = gson.fromJson(jsonStreamReader.next(), String::class.java).toString()
        if (response == "void") {
            return
        }
        throw IllegalArgumentException("Did not return void")
    }

    override fun getPlayerName(): String {
        return name
    }

    private fun writeToOutput(mname: String, arguments: List<String>) {
        outputStreamWriter.write("[\"$mname\", ${arguments.joinToString(prefix="[", postfix = "]", separator = ", ")} ]")
        outputStreamWriter.flush()
    }

    @Throws(IllegalArgumentException::class, UnsupportedOperationException::class, NumberFormatException::class)
    private fun parseForChoice(nextEl: JsonElement):PlayerSIM {
        val listOfJsonEl = gson.fromJson<List<JsonElement>>(nextEl, List::class.java)
        val index = listOfJsonEl[0].asInt
        val direction = SlideDirection.valueOf(listOfJsonEl[1].asString)
        listOfJsonEl[2].asInt //0, 90, 180, 270 -> 0, 1, 2, 3
        val rotateDegree = TilePathDirection.parseDegreeToDirection(listOfJsonEl[2].asInt)
        val coordinate = Position(
            listOfJsonEl[3].asJsonObject.get("row#").asInt,
            listOfJsonEl[3].asJsonObject.get("column#").asInt
        )
        return PlayerSIM(index, direction, rotateDegree, coordinate)
    }

    private fun parseForPass(nextEl: JsonElement): PlayerPass {
        if (gson.fromJson(nextEl, String::class.java) == "PASS") {
            return PlayerPass
        }
        throw IllegalArgumentException("PASS was not supplied")
    }
}