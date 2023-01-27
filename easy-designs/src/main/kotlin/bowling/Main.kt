package bowling

import kotlin.math.abs
import kotlin.random.Random

/**
 * Simulating a bowling game, where a group of people can compete in an ally
 * given a group of people we have to assign them an ally to play for
 * and at the end of game session we have to declare winner as well
 */

class Player constructor(private val name: String) {

    companion object {
        const val MAX_ROLL_ALLOWED = 23
    }

    private var score = 0
    private val rolls = MutableList(MAX_ROLL_ALLOWED) {
        0
    }
    private var firstRoll = true
    private var frameIndex = 0
    private var canPlay = true
    private var currentRoll = 0

    private fun isStrike(): Boolean {
        return firstRoll && rolls[frameIndex] == 10
    }

    private fun isSpare(): Boolean {
        return rolls[frameIndex] + rolls[frameIndex + 1] == 10
    }

    private fun updateScore() {
        if (isStrike()) {
            score += 20
            rolls[currentRoll++] = 0
            frameIndex += 2
            if (frameIndex >= MAX_ROLL_ALLOWED) {
                canPlay = false
            }
        } else {
            if (frameIndex >= MAX_ROLL_ALLOWED - 1) {
                score += rolls[frameIndex]
                canPlay = false
            } else if (firstRoll) {
                firstRoll = false
            } else {
                if (isSpare()) {
                    score += 5
                }
                score += (rolls[frameIndex] + rolls[frameIndex + 1])
                frameIndex += 2
                firstRoll = true
                if (frameIndex >= MAX_ROLL_ALLOWED - 3) {
                    canPlay = false
                }
            }
        }
    }

    fun getName() = name
    fun getScore() = score
    fun roll(pins: Int) {
        if (!canPlay)
            return
        rolls[currentRoll++] = pins
        updateScore()
    }

    fun canPlay(): Boolean {
        return canPlay
    }
}

class GameSession {
    private var alley = -1
    private var id = -1
    private val players = mutableListOf<Player>()

    companion object {
        var gameSessionId = 1
    }

    init {
        id = getUniqueId()
        players.clear()
    }

    private fun getUniqueId(): Int {
        return gameSessionId++
    }

    fun setAlley(alley: Int) {
        this.alley = alley
    }

    fun getId() = id
    fun setPlayers(players: List<Player>) {
        this.players.clear()
        this.players.addAll(players)
    }

    fun declareWinner(): Boolean {
        var maxScore = 0
        var winner: Player? = null
        for (p: Player in players) {
            if (p.canPlay()) {
                println("Player: ${p.getName()} has not completed yet and his current score is ${p.getScore()}")
                println("match is still in progress")
                return false
            }
            if (p.getScore() > maxScore) {
                maxScore = p.getScore()
                winner = p
            }
        }
        if (winner == null) {
            println("winner is still null")
            return false
        }
        println("Winner is ${winner.getName()} with score ${winner.getScore()}")
        // make this alley available for next session
        Game.makeActive(alley)
        return true
    }

    fun makeRoll(player: Player, pins: Int) {
        for (p: Player in players) {
            if (p.getName() == player.getName()) {
                p.roll(pins)
            }
        }
    }
}

class Game {
    private val gameSessions = HashMap<Int, GameSession>()

    companion object {
        val alleys = mutableListOf<Int>(1, 2, 3, 4)
        fun makeActive(alley: Int) {
            alleys.add(alley)
        }
    }

    fun createSession(players: List<Player>): Int {
        if (alleys.isEmpty()) {
            println("All alleys are currently occupied!")
            return 0
        }
        val gameSession = GameSession()
        gameSession.apply {
            setPlayers(players)
            setAlley(alleys.last())
        }
        alleys.removeLast()
        gameSessions[gameSession.getId()] = gameSession
        return gameSession.getId()
    }

    fun roll(index: Int, player: Player, pins: Int) {
        val gameSession = gameSessions[index]
        gameSession ?: return
        gameSession.makeRoll(
            player, pins
        )
    }

    fun declareWinner(index: Int) {
        val flag = gameSessions[index]?.declareWinner()
        flag ?: return
        if (!flag) {
            println("No winner for this game yet")
        }
    }
}

fun main() {
    val player1 = Player("Thor")
    val player2 = Player("Loki")
    val player3 = Player("Hela")
    val player4 = Player("Odin")

    val game = Game()
    val sessionIdx = game.createSession(
        listOf(player1, player2, player3, player4)
    )
    val scoreOfOne = mutableListOf<Int>()
    val scoreOfTwo = mutableListOf<Int>()
    val scoreOfThree = mutableListOf<Int>()
    val scoreOfFour = mutableListOf<Int>()
    var score = 0
    for( i in 0..19){
        score = generateRandom()
        scoreOfOne.add(score)
        game.roll(sessionIdx, player1, score)

        score = generateRandom()
        scoreOfTwo.add(score)
        game.roll(sessionIdx, player2, score)

        score = generateRandom()
        scoreOfThree.add(score)
        game.roll(sessionIdx, player3, score)

        score = generateRandom()
        scoreOfFour.add(score)
        game.roll(sessionIdx, player4, score)
    }
    print("\nPlayer 1: ")
    for(s: Int in scoreOfOne){
        print("$s ")
    }
    print("\nPlayer 2: ")
    for(s: Int in scoreOfTwo){
        print("$s ")
    }
    print("\nPlayer 3: ")
    for(s: Int in scoreOfThree){
        print("$s ")
    }
    print("\nPlayer 4: ")
    for(s: Int in scoreOfFour){
        print("$s ")
    }
    println()

    game.createSession(
        listOf(player1, player2, player3, player4)
    )
    game.createSession(
        listOf(player1, player2, player3, player4)
    )
    game.createSession(
        listOf(player1, player2, player3, player4)
    )
    // we have to declare winner so that alley can be freed
    game.declareWinner(sessionIdx)
    // error
    val gameIdx2 = game.createSession(
        listOf(player1, player2, player3, player4)
    )
    game.declareWinner(gameIdx2)
}

fun generateRandom(): Int{
    return abs(Random.nextInt(1, 10))
}