Memorandum To: CEO's of Maze 
From: David Yan, Zekai Shen   
Date: November 10th, 2022    
Subject: Interactive Player Mechanism Design


Dear our co-CEO's

Your consideration of changing the game will be sure to attract a lot of fun. Our 
team have analyzed the changes you've requested us to consider. We have summarized 
the difficulty of changing our internal representation of the game below:

- Blank tiles for the board  

Rating: 2  
Blank tiles can be done by adding a new class that extends ITileModel and having the
 methods like getPathDirections returning an empty list (if a blank tile means without a path), getGems returning a null etc.
We would need a small refactor across since gems/paths can be nullable but it should not be too difficult. 

- Use movable tiles as goals

Rating 4  
This would be less than ideal since our move logic would now have to move goal positions as it shifts. In addition, it also requires
looping through players (PlayerGroundTruth) to check if their goal position moves. This is like a parallel data structure, these changes can fall out 
of sync.

- Ask Player to sequentially pursue several goals, one at a time

Rating: 1  
Not difficult. It should require minimal work as the API exists to notifying the
player that a new goal is present. setUp in IPlayer.kt can be called several times with a null state.
The Player mechanism can have a field keeping track of the current target whether be a treasure, home or something else 
and having that position be fed into strategy.