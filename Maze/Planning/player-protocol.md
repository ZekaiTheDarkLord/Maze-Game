Memorandum To: Santa Claus <br /> 
From: Chengyi Kuang, Zekai Shen <br /> 
Date: October 20, 2022 <br /> 
Subject: Game State Design <br /> 

<br /> 
Playerside:
<br /> 
Class: Player
 <br /> 
Fields: 
 <br /> 
board: IBoard
 <br /> 
playerInfo: PlayerGroundTruthData
 <br /> 
PlayerCurrentGoal: PlayerCurrentGoal
 <br /> 
Methods: 
 <br /> 
receiveMessage(refereeMessage: RefereeMessage): void
 <br /> 
takeTurn(): PlayerAction

-----------------------------------------------------
 <br /> 
Interface: PlayerAction
 <br /> 
Fields: 
 <br /> 
actionType: ActionType
 <br /> 
playerAvatar: Avatar

-----------------------------------------------------
 <br /> 
Class: PlayerPass implements PlayerAction

 <br /> 
Class: PlayerSIM implements PlayerAction
 <br /> 
Fields: 
 <br /> 
actionType: ActionType
 <br /> 
rowOrColumnIdx: SlideDirection
 <br /> 
rotateDegree: Int
 <br /> 
targetPos: Position
 <br /> 
Enum: PlayerCurrentGoal(GET_TARGET_TILE, RETURN_HOME)
Enum: PlayerActionType(SLIDE_INSERT_MOVE, PASS)
 <br /> 
Refereeside: 

-----------------------------------------------------
 <br /> 
Class: Referee
 <br /> 
Fields: 
 <br /> 
gameState: GameState
 <br /> 
currentPlayer: Player
 <br /> 
Methods: 
 <br /> 
receiveAction(playerAction: PlayerAction): void
 <br /> 
nextPlayer(): void
 <br /> 
kickPlayer(): void 

-----------------------------------------------------
 <br /> 
Interface: RefereeMessage
 <br /> 
field: 
 <br /> 
refereeActionType: RefereeActionType

-----------------------------------------------------
 <br /> 
Class: TakeTurnMessage
 <br /> 
field: 
 <br /> 
board: IBoard
 <br /> 
playerPos: Position
 <br /> 
goalTilePos: Position
 
-----------------------------------------------------
Class: BanMessage
 <br /> 
Enum: RefereeActionType(PLEASE_TAKE_TURN, YOU_HAVE_BEEN_BANNED)
