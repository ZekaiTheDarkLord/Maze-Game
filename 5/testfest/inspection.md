Pair: johnkuang-ruaruarua \
Commit: [ccc6aba526a28f840ddecf5803e475277710dd6d](https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/tree/ccc6aba526a28f840ddecf5803e475277710dd6d) \
Self-eval: https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/ccc6aba526a28f840ddecf5803e475277710dd6d/5/self-5.md \
Score: 108/160 \
Grader: Alexis Hooks

<hr>

SELF EVAL **[20/20]**:

- [20/20] - helpful and accurate self-eval

<hr>

PROGRAMMING TASK **[88/110]**:

[46/50] - The `Player` should have the following functionality with good names, signatures/types, and purpose statements.

- [6/10] - `name`
  - admitting that the functionality was not present
- [10/10] - `propose board`
- [10/10] - `setting up`
- [10/10] - `take a turn`
- [10/10] - `did I win`

<br>

[31/40] - The `Referee` should have the following functionality with good names, signatures/types, and purpose statements.

- [6/10] setting up the player with initial information
   - admitting that the functionality was not present 
- [5/10] running rounds until the game is over
  - this should be factored out into its own method
- [10/10] running a round, which must have functionality for
  - checking for "all passes"
  - checking for a player that returned to its home (winner)
- [10/10] score the game

NOTE: 
* Your referee should be able to initialize/run a game without needing a gamestate to be passed in as an argument to RunGame.
* Every referee call on the player should be protected against illegal behavior and infinite loops/timeouts/exceptions. This should be factored out into a single point of control.

<br>

[11/20] - Unit Tests

- [5/10] a unit test for the referee function that returns a unique winner
  - this test only verifies the number of winners not the actual player object
- [6/10] a unit test for the scoring function that returns several winners
   - admitting that the functionality was not present 


<hr>

DESIGN TASK **[0/30]**:

- [0/10] rotates the tile before insertion
- [0/10] selects a row or column to be shifted and in which direction
- [0/10] selects the next place for the player's avatar
