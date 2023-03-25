**If you use GitHub permalinks, make sure your link points to the most recent commit before the milestone deadline.**

## Self-Evaluation Form for Milestone 5

Indicate below each bullet which file/unit takes care of each task:

The player should support five pieces of functionality: 

- `name`
- Note: We had *name* when we designed the Players. It was accidentally removed later. 
- `propose board` (okay to be `void`)
- https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/main/kotlin/player/IPlayer.kt#L21
- `setting up`
- https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/main/kotlin/player/IPlayer.kt#L34
- `take a turn`
- https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/main/kotlin/player/IPlayer.kt#L44
- `did I win`
- https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/main/kotlin/player/IPlayer.kt#L49

Provide links. 


The referee functionality should compose at least four functions:

- setting up the player with initial information
- Note: We believe this is closely related to setting up the game state. Since the referee is not responsible for calling proposeBoard0 in this milestone, we decided to implement this feature when implementing proposedBoard0 becomes a requiremnet. We are aware of this functionality and have created a placeholder function for now.  
- https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/main/kotlin/referee/Referee.kt#L222
- running rounds until termination
- https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/main/kotlin/referee/Referee.kt#L58
- running a single round (part of the preceding bullet)
- https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/main/kotlin/referee/Referee.kt#L85
- scoring the game
- Note: the function *determineWinner* is responsible for figuring out a winner if no player returned home after reaching the treasure tile.
- https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/main/kotlin/referee/Referee.kt#L66
- https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/main/kotlin/referee/Referee.kt#L174

Point to two unit tests for the testing referee:

1. a unit test for the referee function that returns a unique winner
- Note: the first assertion asserts the number of winners while the second assertion asserts the number of cheaters.  
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/51a8e663f54fe9daf107ea8e4985915ff1386a24/Maze/Common/src/test/kotlin/referee/RefereeTest.kt#L137
2. a unit test for the scoring function that returns several winners
- N/A

The ideal feedback for each of these points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files -- in the last git-commit from Thursday evening. 

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.

