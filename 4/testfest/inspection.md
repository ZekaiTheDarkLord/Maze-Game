Pair: johnkuang-ruaruarua \
Commit: [1539f99d8488b9b76001a79312202fbbc67be9a0](https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/tree/1539f99d8488b9b76001a79312202fbbc67be9a0) \
Self-eval: https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/4213738641eef8288146d88a4b382b35b53e01cb/4/self-4.md \
Score: 94/110 \
Grader: Rajat Keshri


10/10: Self-eval

`strategy.PP`: 84/100
- [20/20] a data definition for the return value of a call to strategy
  - Your data definition interpretation could be stronger; interpretations for fields are kind of found in the purpose statements for their getters, but it would be better to have these also as part of data definition interpretation.
   
- [15/15] - for the top-level function that *composes* generating a sequence of spots to move to and searching.
- [15/15] - for generating the sequence of spots the player may wish to move to
  - You could possibly create a method for generating moves for Riemann, the same way you have done for Euclid (getDistanceMap).
- [15/15] - for searching rows then columns.
- [9/15] - for function/method that validates the location of the avatar after slide/insert is not the target and the current target is reachable.
  - the rubric asks for a specific functionality, since you acknowledged on your self-eval that you don't have it, giving 60% credit. 

- [10/10] - for unit test that produces an action to move the player
- [0/10] - for unit test that forces player to pass on turn 

`player-protocol.md` (Ungraded)
- No explicit explaination of the UML diagram
- No explicit mentioning of what Referee does if player cheats
- No mention of a player passing on its turn


\<free text\>
