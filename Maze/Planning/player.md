To: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Santa Claus<br />
From: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Chengyi Kuang, Zekai Shen<br />
Date: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; October 13, 2022<br />
Subject: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Player Data Representation Design<br />

The Player should have the following properties:

Fields:
1. sourceTile: ITileState
2. targetTile: ITileState
3. currentPosition: Position
4. hasReachedTarget: Boolean
5. stage: ROUNDMOVE

Methods:
1. getSourceTile(): ITileState [get this player's source tile]
2. getTargetTile(): ITileState [get this player's target tile]
3. getCurrentPosition: Position [get this player's current position]
4. hasPlayerReachedTarget: Boolean [if this player has reached its target tile]
5. getStage: ROUNDMOVE [get this player's game stage]
6. getRoundAction: PlayerRequestedAction [The action requested by this player to the referee]
   
Enum: 
1. ROUNDMOVE(move, pass)
2. SlideDirection(UP, DOWN, LEFT, RIGHT)

Data class:
PlayerRequestedAction()

Fields:
1. toTakeAction: Boolean [whether to pass or to take action]
2. slideDirection: SlideDirection [direction of a slide]
3. spareTileRotationDegree: Int [number of degrees to rotate the spare tile]
4. desiredMoveCoordinate: Position [desired coordinate after move]

Methods:
Kotlin data class comes with getters and setters.