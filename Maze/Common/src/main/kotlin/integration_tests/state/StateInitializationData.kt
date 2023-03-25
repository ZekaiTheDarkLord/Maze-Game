package integration_tests.state

import components.boards.SlideDirection
import state.IState

data class StateInitializationData(
    val state: IState,
    val rowOrColumnIndex: Int,
    val slideDirection: SlideDirection,
    val rotationDegree: Int
)
