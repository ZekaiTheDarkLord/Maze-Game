To:                 Santa Claus
From:            Chengyi Kuang, Zekai Shen
Date:             November 3, 2022
Subject:         Monolithic Software System

In our design, we will have a server that accepts player and holds the game. 

Firstly, the player will send server handshake message to the server to show
that the players want to participate in the game. 
Then, the server will send reponse message back and let the player who wants 
to participate to send their information like names, color of the avatar and
strategy they want to use. If the participants send the valid information to
the server, then they will be one of the players in the game.

When the server collect enough players, the game begins. 
The server will do the similar thing with the referee, which maintains the 
game, ask the player and inform their positions and targets and provide them 
the state of the game. 

Every time when it is a player's round, the board will inform the player to 
make move, along with the information player requires to make decisions. 
After receiving the information, players have some time to think and make a 
move will be shown as send a message back to the server. Then the server 
will decide whether the player's move is valid or not. If it is valid, the 
server will perform such move. If it is not valid, the player will be kicked
from the game and they can not make any changes to the game any more. 

The player must response to the server within a specific time period, otherwise,
the player will be treated as quit the game and automatically kick from the game.

When the server fidn out that the game satisfy certain condition(like a player 
reaches home after they go to the target tile), the referee will stop the 
game and inform all players winners and cheaters of the game. 