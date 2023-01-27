package snake_n_ladder

import kotlin.random.Random

data class Snake(
    val head: Int,
    val tail: Int
)

data class Ladder(
    val baseAt: Int,
    val topAt: Int
)

class Player constructor(
    private val name: String,
) {

    fun getName() = name

    companion object {
        private var uniqueIndex = 0
        fun getUniqueId(): Int {
            uniqueIndex++
            return uniqueIndex
        }
    }

    private var id: Int = 0
    private var currentPosition = 0

    init {
        id = getUniqueId()
    }

    fun setPositionAs(position: Int){
        currentPosition = position
    }

    fun diceRolledAndWalkThisMuch(steps: Int) {
        if (currentPosition + steps > 100)
            return
        currentPosition += steps
    }

    fun getCurrentPosition() = currentPosition
}

class GameSimulator constructor(
    private val players: List<Player>,
    private val snakes: List<Snake>,
    private val ladders: List<Ladder>,
) {
    private var currentTurn = 0
    private val snakesAndLaddersMappings = HashMap<Int, Int>()
    private var winner: Player? = null

    fun setUpGame() {
        snakes.forEach { snake ->
            snakesAndLaddersMappings[snake.head] = snake.tail
        }
        ladders.forEach { ladd ->
            snakesAndLaddersMappings[ladd.baseAt] = ladd.topAt
        }
    }

    private fun rollDice(): Int {
        return Random.nextInt(1, 7)
    }

    fun startGame(): Player? {
        // we are assuming that snake and ladder dont
        // form infinite loop and we get
        // at least 1 winner
        while (winner == null) {
            val numberOnDice = rollDice()
            // print("Its ${players[currentTurn].getName()} turn, position: ${players[currentTurn].getCurrentPosition()}")
            players[currentTurn].diceRolledAndWalkThisMuch(numberOnDice)
            // print(" and he got $numberOnDice and now his position is ${players[currentTurn].getCurrentPosition()}\n")
            if (players[currentTurn].getCurrentPosition() == 100) {
                winner = players[currentTurn]
            }
            // find if there was a snake or ladder, and if so then set it to that
            if (snakesAndLaddersMappings.containsKey(players[currentTurn].getCurrentPosition())) {
                players[currentTurn].setPositionAs(
                    snakesAndLaddersMappings[
                        players[currentTurn].getCurrentPosition()
                    ] ?: 0
                )
            }
            currentTurn = (currentTurn + 1) % players.size
        }
        return winner
    }
}

fun main() {
    val player1 = Player("Nikhil")
    val player2 = Player("Nitin")
    val player3 = Player("Saurabh")

    val gameSimulator = GameSimulator(
        players = listOf(player1, player2, player3),
        snakes = listOf(
            Snake(17, 7),
            Snake(54, 34),
            Snake(62, 19),
            Snake(64, 60),
            Snake(87, 36),
        ),
        ladders = listOf(
            Ladder(1, 38),
            Ladder(4, 14),
            Ladder(9, 31),
            Ladder(21, 42),
            Ladder(28, 84),
            Ladder(51, 67),
            Ladder(72, 91),
            Ladder(80, 99),
        )
    )
    gameSimulator.setUpGame()
    println("winner is ${gameSimulator.startGame()?.getName()}")

}