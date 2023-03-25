package client

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonStreamParser
import components.boards.Position
import player.IPlayer
import state.State
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class RefereeProxy(address: String, port: Int, private val name: String) {

    private val socket: Socket
    private val inputStreamReader: InputStreamReader
    private val outputStreamWriter: OutputStreamWriter
    private val jsonStreamReader: JsonStreamParser
    private val gson = Gson()
    private val remotePlayer: IPlayer = RemotePlayer(name)

    init {
        socket = Socket(address, port)
        inputStreamReader = InputStreamReader(socket.getInputStream())
        outputStreamWriter = OutputStreamWriter(socket.getOutputStream())
        jsonStreamReader = JsonStreamParser(inputStreamReader)
    }

    fun startListening() {
        outputStreamWriter.write(name + "\n")
        outputStreamWriter.flush()
        println("sending $name")

        while(jsonStreamReader.hasNext()) {
            val jsonElementList = gson.fromJson<List<JsonElement>>(jsonStreamReader.next(), List::class.java)
            val mName = jsonElementList[0].asString
            val args = jsonElementList[1].asJsonArray

            val result = dispatchMethodCall(mName, args)
            outputStreamWriter.write(result)
            outputStreamWriter.flush()
        }
    }

    private fun dispatchMethodCall(mName: String, args: JsonArray): String {
        return when (mName) {
            "setup" -> {
                val state = if (args[0].asString == "False") null else State.fromJson(args[0].asJsonObject)
                val position = Position(
                    args[1].asJsonObject.get("row#").asInt,
                    args[1].asJsonObject.get("column#").asInt
                )
                remotePlayer.setup(state, position)
                "void"
            }
            "take-turn" -> {
                val playerAction = remotePlayer.takeTurn(State.fromJson(args[1].asJsonObject))
                playerAction.toJson()
            }
            "win" -> {
                remotePlayer.informWon(args[0].asBoolean)
                "void"
            }
            else -> throw IllegalArgumentException("Invalid method name supplied from server.")
        }
    }
}