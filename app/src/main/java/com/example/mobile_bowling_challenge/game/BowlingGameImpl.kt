package com.example.mobile_bowling_challenge.game

import com.example.mobile_bowling_challenge.model.Frame
import com.example.mobile_bowling_challenge.model.RollType

class BowlingGameImpl : BowlingGame {
    private val frames = mutableListOf<Frame>()
    private var currentFrame = 0
    private var gameScore = 0

    init {
        startGame()
    }

    override fun startGame() {
        repeat(10) { frames.add(Frame()) }
    }

    override fun roll(pins: Int) {
        if (isGameOver()) return

        val frame = frames[currentFrame]

        val rollType = when {
            pins == ALL_PINS && frame.rolls.size == 0 -> RollType.STRIKE
            frame.rolls.size == 1 && frame.rolls[0] + pins == ALL_PINS -> RollType.SPARE
            pins == 0 -> RollType.MISS
            else -> RollType.REGULAR
        }

        frame.rolls.add(pins)
        frame.rollTypes.add(rollType)

        calculateScore()

        if (pins == ALL_PINS && frame.rolls.size == 1) { // If strike, move to next frame (no second roll)
            currentFrame++
        } else if (frame.rolls.size == MAX_ROLLS) { // Regular roll
            currentFrame++
        }

        // Add bonus frame after strike/spare at 10th frame
        if (currentFrame == 10 && frame.rolls.sum() == ALL_PINS) frames.add(Frame())
    }

    override fun getCurrentFrame(): Int {
        return currentFrame + 1 // Frames are 0-based
    }

    override fun getScore(): Int {
        return gameScore
    }

    override fun getRemainingRolls(): Int {
        if (isGameOver()) return 0

        val frame = frames[currentFrame]
        return when {
            // For spare or strike in 10th frame, 2 rolls remaining otherwise no extra rolls
            currentFrame == TENTH_FRAME -> {
                if ((frame.rolls.size == MAX_ROLLS && frame.rolls.sum() == ALL_PINS) ||
                    (frame.rolls.size == 1 && frame.rolls[0] == ALL_PINS)
                ) MAX_ROLLS else 0
            }

            // First roll of a normal frame, 2 rolls left
            frame.rolls.isEmpty() -> MAX_ROLLS

            // Strike, no second roll otherwise first roll done, 1 roll left
            frame.rolls.size == 1 -> if (frame.rolls[0] == ALL_PINS) 0 else 1

            // The frame is complete (2 rolls done or strike)
            else -> 0
        }
    }

    override fun isGameOver(): Boolean {
        return currentFrame == frames.size
    }

    override fun getGameState(): List<Frame> {
        return frames
    }

    private fun calculateScore() {
        var cumulativeScore = 0

        frames.forEachIndexed { index, frame ->
            if (frame.rolls.isNotEmpty()) {
                var frameScore = frame.rolls.sum()

                // Add the cumulative score up to the previous frame
                frameScore += cumulativeScore

                if (frame.rolls[0] == ALL_PINS && index < TENTH_FRAME) {
                    frameScore += getStrikeBonus(index)
                } else if (frame.rolls.size == MAX_ROLLS && frame.rolls.sum() == ALL_PINS) {
                    frameScore += getSpareBonus(index)
                }

                frame.score = frameScore

                cumulativeScore = frame.score

                gameScore = cumulativeScore
            }
        }
    }

    private fun getStrikeBonus(index: Int): Int {
        // If strike, add next two rolls from subsequent frames
        val nextFrame = frames.getOrNull(index + 1)
        return (nextFrame?.rolls?.getOrNull(0) ?: 0) + (nextFrame?.rolls?.getOrNull(1) ?: 0)
    }

    private fun getSpareBonus(index: Int): Int {
        // If spare, add next roll from the following frame
        val nextFrame = frames.getOrNull(index + 1)
        return nextFrame?.rolls?.getOrNull(0) ?: 0
    }

    companion object {
        const val ALL_PINS = 10
        const val TENTH_FRAME = 9
        const val MAX_ROLLS = 2
    }
}
