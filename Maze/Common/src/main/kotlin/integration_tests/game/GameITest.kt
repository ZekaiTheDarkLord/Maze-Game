package integration_tests.game

import com.google.gson.Gson
import com.google.gson.JsonStreamParser
import components.boards.Board
import components.boards.IBoard
import components.tiles.Gem
import components.tiles.ITileModel
import components.tiles.SimpleTile
import components.tiles.TileShape
import referee.Referee
import java.io.InputStreamReader


fun main() {
    // get basic elements
    val parser = JsonStreamParser(InputStreamReader(System.`in`))
    val players = createPlayerList(parser)
    val refereeState = scanRefereeState(parser, players)

    // add player to the list
    val referee = Referee()
    val winnersAndCheaters =
        referee.playGame(players, refereeState, mutableListOf())
    val winners = winnersAndCheaters.first
    val names: MutableList<String> = getNames(winners) as MutableList<String>
    for (i in names.indices) {
        names[i] = names[i].substring(1, names[i].length - 1)
    }
    names.sort()
    
    val result = Gson().toJson(names)
    print(result)


}
