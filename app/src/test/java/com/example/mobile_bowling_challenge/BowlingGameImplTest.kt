package com.example.mobile_bowling_challenge

import com.example.mobile_bowling_challenge.game.BowlingGame
import com.example.mobile_bowling_challenge.game.BowlingGameImpl
import com.example.mobile_bowling_challenge.model.Frame
import com.example.mobile_bowling_challenge.model.RollType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BowlingGameImplTest {

    private lateinit var bowlingGameSubject: BowlingGame
    private lateinit var mockFrames: MutableList<Frame>

    @Before
    fun setUp() {
        mockFrames = mutableListOf()
        repeat(10) { mockFrames.add(Frame()) }

        bowlingGameSubject = BowlingGameImpl()

        bowlingGameSubject.roll(5)
        bowlingGameSubject.roll(5)
        bowlingGameSubject.roll(10)
        bowlingGameSubject.roll(0)

        mockFrames[0] = Frame(
            rolls = mutableListOf(5, 5),
            rollTypes = mutableListOf(RollType.REGULAR, RollType.SPARE),
            score = 10
        )

        mockFrames[1] = Frame(
            rolls = mutableListOf(10),
            rollTypes = mutableListOf(RollType.STRIKE),
            score = 20
        )

        mockFrames[2] = Frame(
            rolls = mutableListOf(0),
            rollTypes = mutableListOf(RollType.MISS),
            score = 20
        )
    }

    @Test
    fun testStartGame() {
        // startGame() is called at init, so if called again here, the value would be 20 frames

        val expected = mockFrames.size
        val actual = bowlingGameSubject.getGameState().size

        assertEquals(expected, actual)
    }

    @Test
    fun testRollWithRegularRollType() {
        val actualRoll = bowlingGameSubject.getGameState()[0].rolls[0]
        val actualRollType = bowlingGameSubject.getGameState()[0].rollTypes[0]
        val actualScore = bowlingGameSubject.getGameState()[0].score

        val expectedRoll = 5
        val expectedRollType = RollType.REGULAR
        val expectedScore = 10

        assertEquals(expectedRoll, actualRoll)
        assertEquals(expectedRollType, actualRollType)
        assertEquals(expectedScore, actualScore)
    }

    @Test
    fun testRollWithSpareRollType() {
        val actualRoll = bowlingGameSubject.getGameState()[0].rolls[1]
        val actualRollType = bowlingGameSubject.getGameState()[0].rollTypes[1]
        val actualScore = bowlingGameSubject.getGameState()[0].score

        val expectedRoll = 5
        val expectedRollType = RollType.SPARE
        val expectedScore = 10

        assertEquals(expectedRoll, actualRoll)
        assertEquals(expectedRollType, actualRollType)
        assertEquals(expectedScore, actualScore)
    }

    @Test
    fun testRollWithStrikeRollType() {
        val actualRoll = bowlingGameSubject.getGameState()[1].rolls[0]
        val actualRollType = bowlingGameSubject.getGameState()[1].rollTypes[0]
        val actualScore = bowlingGameSubject.getGameState()[1].score

        val expectedRoll = 10
        val expectedRollType = RollType.STRIKE
        val expectedScore = 20

        assertEquals(expectedRoll, actualRoll)
        assertEquals(expectedRollType, actualRollType)
        assertEquals(expectedScore, actualScore)
    }

    @Test
    fun testRollWithMissRollType() {
        val actualRoll = bowlingGameSubject.getGameState()[2].rolls[0]
        val actualRollType = bowlingGameSubject.getGameState()[2].rollTypes[0]
        val actualScore = bowlingGameSubject.getGameState()[2].score

        val expectedRoll = 0
        val expectedRollType = RollType.MISS
        val expectedScore = 20

        assertEquals(expectedRoll, actualRoll)
        assertEquals(expectedRollType, actualRollType)
        assertEquals(expectedScore, actualScore)
    }

    @Test
    fun testGetCurrentFrame() {
        val expected = 3
        val actual = bowlingGameSubject.getCurrentFrame()

        assertEquals(expected, actual)
    }

    @Test
    fun testGetScore() {
        val expected = 20
        val actual = bowlingGameSubject.getScore()

        assertEquals(expected, actual)
    }

    @Test
    fun testGetRemainingRolls() {
        val expected = 1
        val actual = bowlingGameSubject.getRemainingRolls()

        assertEquals(expected, actual)
    }

    @Test
    fun testIsGameOver() {
        val expected = false
        val actual = bowlingGameSubject.isGameOver()

        assertEquals(expected, actual)
    }

    @Test
    fun testGetGameState() {
        val actual = bowlingGameSubject.getGameState()

        assertEquals(mockFrames, actual)
    }
}