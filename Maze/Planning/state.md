Memorandum
To:                 Santa Claus From:            Chengyi Kuang, Zekai Shen Date:             October 7, 2022 Subject:        Game State Design 

The game state has following functionality:
Field:
1. CurrentPlayer: Player
2. CurrentStage: ROUNDMOVE
3. GameBord: IBoard
4. PlayerList: List<Player>()

Methods: 
1. getCurrentPlayer(): Player
2. getNextPlayer(): Player
3. getPlayerPos(playerName: String): Posn throws IllegalArgumentException()
4. getPlayerHome(playerName: String): Posn throws IllegalArgumentException()
5. getTreasure(playerName: String): bool throws IllegalArgumentException()
6. getRoundMove(): ROUNDMOVE
7. isRoundEnd(): bool
8. isGameEnd(): bool

new Class: 
1. Player(name: String, Position: Posn, homePosition: Posn, assignedTreasure: Gem)
2. Posn(x: Int, y: Int)

new Enum: 
ROUNDMOVE(MOVE，PASS, ROUNDEND)
