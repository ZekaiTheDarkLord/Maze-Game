package integration_tests.bad

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonStreamParser
import integration_tests.game.getNames
import integration_tests.game.scanRefereeState
import player.EuclidPlayer
import player.IPlayer
import player.RiemannPlayer
import referee.Referee
import java.io.InputStreamReader

fun main() {
    val parser = JsonStreamParser(InputStreamReader(System.`in`))
    val players = createPlayerAndBadPlayerList(parser)
    val refereeState = scanRefereeState(parser, players)

    // add player to the list
    val referee = Referee()
    val (winners, cheaters) = referee.playGame(players, refereeState, mutableListOf())
    val namesOfWinners: MutableList<String> = getNames(winners) as MutableList<String>
    val namesOfCheaters: MutableList<String> = getNames(cheaters) as MutableList<String>
    namesOfWinners.sort()
    namesOfCheaters.sort()
    val result = Gson().toJson(listOf(namesOfWinners, namesOfCheaters))
    print(result)
}


fun createPlayerAndBadPlayerList(parser: JsonStreamParser): MutableList<IPlayer> {
    val players = mutableListOf<IPlayer>()
    val playerSpec = scanPlayerAndBadPlayerSpec(parser)

    playerSpec.forEach {
        val first = it.first.substring(1, it.first.length - 1)
        val second = it.second.substring(1, it.second.length - 1)
        val third = if (it.third.isEmpty()) {
            ""
        } else {
            it.third.substring(1, it.third.length - 1)
        }

        if (third == "setUp" || third == "win" || third == "takeTurn" ) {
            players.add(BadPlayer(first, second, third, 1))
        } else if (second == "Euclid") {
            players.add(EuclidPlayer(first))
        } else {
            players.add(RiemannPlayer(first))
        }
    }

    return players
}


fun scanPlayerAndBadPlayerSpec(parser: JsonStreamParser): MutableList<Triple<String, String, String>> {
    val scannedData: MutableList<Triple<String, String, String>> = mutableListOf()

    while (parser.hasNext()) {
        val element: JsonElement = parser.next()
        if (element.isJsonArray) {
            val temp = element.asJsonArray.toMutableList()
            temp.forEach {
                val ps = it.asJsonArray
                scannedData.add(
                    Triple(
                        ps.get(0).toString(), ps.get(1).toString(), try {
                            ps.get(2).toString()
                        } catch (e: Exception) {
                            ""
                        }
                    )
                )
            }
            break
        }
    }
    return scannedData
}