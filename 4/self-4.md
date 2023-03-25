**If you use GitHub permalinks, make sure your links points to the most recent commit before the milestone deadline.**

## Self-Evaluation Form for Milestone 4

The milestone asks for a function that performs six identifiable
separate tasks. We are looking for four of them and will overlook that
some of you may have written deep loop nests (which are in all
likelihood difficult to understand for anyone who is to maintain this
code.)

Indicate below each bullet which file/unit takes care of each task:

1. the "top-level" function/method, which composes tasks 2 and 3 

The execute method for Riemannn  
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/strategy/Riemann.kt#L42  
The execute method for Euclid  
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/strategy/Euclid.kt#L33  
The tryAllDegrees Method
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/strategy/AbtractStrategy.kt#L96

The execute method composes tasks two and three. It determines the target position of the player. Once the player cannot reach its original goal by sliding, it will choose an alternative goal for the player according to its algorithm. 
For completing task three, it will delegate the task to the method "tryAllDegrees" in abstract class.   

2. a method that `generates` the sequence of spots the player may wish to move to  

The execute for Riemannn:  
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/strategy/Riemann.kt#L48  
The getDistanceMap for Euclid:  
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/strategy/Euclid.kt#L82

For Riemann, the execute will iterate all the spots in row-column order. 
For Euclid, the getDistanceMap method will generates a piority queue which stores the distance and a hashmap that stores distance to list of positions.  

3. a method that `searches` rows,  columns, etcetc. 

tryAllSlides: 
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/strategy/AbtractStrategy.kt#L24

The tryAllSlides method tries all the slides for rows and columns with all directions and degrees. 

4. a method that ensure that the location of the avatar _after_ the
   insertion and rotation is a good one and makes the target reachable

https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/strategy/AbtractStrategy.kt#L105

We don't have such method as we only use check this once since we abstract all the other tasks. 
However, we do have check the location of the avatar after insertion and rotation which is located inside the method try all slides. 



ALSO point to

- the data def. for what the strategy returns

Interface PlayerAction:   
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/player_referee_protocal/PlayerAction.kt#L6  
Class: PlayerPass  
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/player_referee_protocal/PlayerPass.kt#L6  
Class: PlayerSIM  
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/main/kotlin/player_referee_protocal/PlayerSIM.kt#L14  

- unit tests for the strategy
unit test for Riemann:    
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/test/kotlin/strategy/RiemannTest.kt#L19  
unit test for Euclid:   
https://github.khoury.northeastern.edu/CS4500-F22/johnkuang-ruaruarua/blob/e43e86de338de6510cc19a5f6b04d22656124dff/Maze/Common/src/test/kotlin/strategy/EuclidTest.kt#L21  

The ideal feedback for each of these points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality or realized
them differently, say so and explain yourself.


