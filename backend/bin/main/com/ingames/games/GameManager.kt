package com.ingames.games

import com.ingames.models.GameInfo
import com.ingames.websocket.WebSocketHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class GameManager(val wsHub: WebSocketHub) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val sevenUpDownEngine = SevenUpDownEngine(scope, wsHub)
    val crushEngine = CrushEngine(scope, wsHub)
    val dragonTigerEngine = DragonTigerEngine(scope, wsHub)

    val gamesList = listOf(
        GameInfo(
            id = "seven_up_down",
            title = "7 Up Down",
            description = "Predict 2 dice total (2-6, 7, 8-12)",
            imagePath = "images/7updown.png",
            accentColorHex = "#FF5722",
            route = "/game/seven_up_down",
            activePlayers = 432
        ),
        GameInfo(
            id = "crush",
            title = "Crush / Crash",
            description = "Cashout before the curve crashes!",
            imagePath = "images/double.png",
            accentColorHex = "#E91E63",
            route = "/game/crush",
            activePlayers = 680
        ),
        GameInfo(
            id = "dragon_tiger",
            title = "Dragon Tiger",
            description = "High card duel with fast payouts",
            imagePath = "images/dtgame.png",
            accentColorHex = "#FFC107",
            route = "/game/dragon_tiger",
            activePlayers = 310
        ),
        GameInfo(
            id = "classic_dice",
            title = "Classic Dice",
            description = "Roll dice & win instant rewards",
            imagePath = "images/classic_dice.png",
            accentColorHex = "#4CAF50",
            route = "/game/html5/seven_up_down",
            activePlayers = 190
        ),
        GameInfo(
            id = "mines",
            title = "Mines",
            description = "Uncover diamonds and dodge hidden mines",
            imagePath = "images/mines.png",
            accentColorHex = "#00BCD4",
            route = "/game/html5/fruit_slice",
            activePlayers = 240
        ),
        GameInfo(
            id = "ludo",
            title = "Ludo Express",
            description = "4 Player classic multiplayer board",
            imagePath = "images/ludo.jpg",
            accentColorHex = "#9C27B0",
            route = "/game/ludo",
            activePlayers = 115
        ),
        GameInfo(
            id = "rummy",
            title = "Indian Rummy",
            description = "13-card classic rummy tourneys",
            imagePath = "images/rummy.jpg",
            accentColorHex = "#3F51B5",
            route = "/game/rummy",
            activePlayers = 520
        )
    )

    fun startAll() {
        sevenUpDownEngine.start()
        crushEngine.start()
        dragonTigerEngine.start()
    }
}
