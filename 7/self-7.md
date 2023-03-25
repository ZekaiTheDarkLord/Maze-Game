**If you use GitHub permalinks, make sure your link points to the most recent commit before the milestone deadline.**

## Self-Evaluation Form for Milestone 7

Indicate below each bullet which file/unit takes care of each task:

The require revision calls for

    - the relaxation of the constraints on the board size
    - a suitability check for the board size vs player number 

1. Which unit tests validate the implementation of the relaxation?

https://github.khoury.northeastern.edu/CS4500-F22/tricky-armadillos/blob/bac10b435370be96062e88f2714d1173ae82a68a/Maze/Common/src/test/kotlin/referee/RefereeTest.kt#L276

A board of size 3 and slidable rows and columns according to the spec can be seen here.


2. Which unit tests validate the suitability of the board/player combination?   

https://github.khoury.northeastern.edu/CS4500-F22/tricky-armadillos/blob/bac10b435370be96062e88f2714d1173ae82a68a/Maze/Common/src/test/kotlin/state/StateTest.kt#L346-L363

We admit we do not fully adhere to the spec as mentioned by Prof. Felleisen in https://piazza.com/class/l7g9e46cc2l4d3/post/184. Currently, our check here https://github.khoury.northeastern.edu/CS4500-F22/tricky-armadillos/blob/bac10b435370be96062e88f2714d1173ae82a68a/Maze/Common/src/main/kotlin/state/State.kt#L390 determines if players have distinct homes meaning # of players <= # of tiles. However, the Piazza post mentions it should be roughly # of rows / 2 * # of colums /2 while our implementation currently supports # of row * # of col implicity.

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.
