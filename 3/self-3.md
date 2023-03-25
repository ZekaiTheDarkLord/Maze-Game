## Self-Evaluation Form for Milestone 3

Indicate below each bullet which file/unit takes care of each task:

Note: 
We are aware that there is an ongoing conversation between the Professors regarding whether a single class deserves an interface. 
Since we don't have a clear final answer yet, we decided to create an interface as that's what we learned in OOD.

1. rotate the spare tile by some number of degrees
   - Interface: https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/IState.kt#L29
   - Class: https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/State.kt#L41
   - Unit tests: 
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/test/kotlin/state/StateTest.kt#L126
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/test/kotlin/state/StateTest.kt#L135
<br />
2. shift a row/column and insert the spare tile
   - Interface: 
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/IState.kt#L41
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/IState.kt#L51
   - Class: 
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/State.kt#L46
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/State.kt#L60
   - Unit tests: https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/test/kotlin/state/StateTest.kt#L162
   - Note: unit tests for slide/insert are between line 162 to 232.
<br />
3. move the avatar of the currently active player to a designated spot
   - Data class: 
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/PlayerGroundTruthData.kt#L13
   - Note: This data class holds the true information of a player. Given the information in milestone 3, we are unsure if we should make this functionality a public method in State.PP. If this becomes a necessity, we will make it a public method in State.PP and its interface in the next milestone. Kotlin data class does not require us to write setters and getters. To move a player a designated slot, we simply update the _currentPosition_ field of the data class.
<br />
4. check whether the active player's move has returned its avatar home
   - Note: This task is not in the milestone 3 specs. The spec only asked us to check whether the active player's move has placed it on its goal tile, not the home tile. If this question is intended to ask if we have implemented the functionality to check whether the active player's move has returned its goal tile, here is the link.
   - Interface: 
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/IState.kt#L66
    - Class: 
      - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/State.kt#L79
   - Unit tests:
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/test/kotlin/state/StateTest.kt#L235
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/test/kotlin/state/StateTest.kt#L246
<br />
5. kick out the currently active player
   - Note: Ideally, if we know more about a player, we could refuse to let the player communicate with the referee. With the information given in this milestone, we decided to remove all information about the active player from the game state. If we find it necessary to reject the player's request to the referee, we will do so as we expect more information about the player in later milestones.
   - Interface: 
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/IState.kt#L71
   - Class: 
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/main/kotlin/state/State.kt#L83
   - Unit tests: 
     - https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/master/Maze/Common/src/test/kotlin/state/StateTest.kt#L255


<br />
The ideal feedback for each of these points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

