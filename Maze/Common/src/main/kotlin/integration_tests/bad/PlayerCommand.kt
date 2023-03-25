package integration_tests.bad

enum class PlayerCommand(val commandString: String) {
    SETUP("setUp"),
    TAKETURN("takeTurn"),
    WIN("win")

}