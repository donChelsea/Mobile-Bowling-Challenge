package com.example.mobile_bowling_challenge.game

import com.example.mobile_bowling_challenge.model.Frame
import com.example.mobile_bowling_challenge.model.RollType

class BowlingGameImpl : BowlingGame {
    private val frames = mutableListOf<Frame>()
    private var currentFrame = 0

    init {
        startGame()
    }

    override fun startGame() {
        repeat(10) { frames.add(Frame()) }
    }

    override fun roll(pins: Int) {
        if (isGameOver()) return

        val frame = frames[currentFrame]

        // Determine roll type before score calculation
        val rollType = when {
            pins == 10 && frame.rolls.size == 0 -> RollType.STRIKE
            frame.rolls.size == 1 && frame.rolls[0] + pins == 10 -> RollType.SPARE
            pins == 0 -> RollType.MISS
            else -> RollType.REGULAR
        }

        // Add the roll and roll type to the current frame
        frame.rolls.add(pins)
        frame.rollTypes.add(rollType)

        if (pins == 10 && frame.rolls.size == 1) { // If strike, move to next frame (no second roll)
            currentFrame++
        } else if (frame.rolls.size == 2) { // Regular roll
            currentFrame++
        }

        // Add bonus frame after strike/spare at 10th frame
        if (currentFrame == 10 && frame.rolls.sum() == 10) frames.add(Frame())

        // Calculate score after each roll
        calculateScore()
    }

    override fun getCurrentFrame(): Int {
        return currentFrame + 1 // Frames are 0-based
    }

    override fun getScore(): Int {
        return frames.sumOf { it.score }
    }

    override fun getRemainingRolls(): Int {
        if (isGameOver()) return 0

        val frame = frames[currentFrame]
        return when {
            currentFrame == 9 -> {  // At 10th frame
                if (frame.rolls.size == 2 && frame.rolls.sum() == 10) {
                    2 // Spare in 10th frame, 2 rolls remaining
                } else if (frame.rolls.size == 1 && frame.rolls[0] == 10) {
                    2 // Strike in 10th frame, 2 rolls remaining
                } else {
                    0 // No extra roll needed in 10th frame
                }
            }

            frame.rolls.isEmpty() -> {
                2 // First roll of a normal frame, 2 rolls left
            }

            frame.rolls.size == 1 -> {
                if (frame.rolls[0] == 10) {
                    0 // Strike, no second roll
                } else {
                    1 // First roll done, 1 roll left in normal frame
                }
            }

            else -> {
                0 // The frame is complete (2 rolls done or strike)
            }
        }
    }

    override fun isGameOver(): Boolean {
        return currentFrame == frames.size
    }

    override fun getGameState(): List<Frame> {
        return frames
    }

    private fun calculateScore() {
        // Calculate score for each frame
        frames.forEachIndexed { index, frame ->
            // Calculate for strikes, spares, and normal rolls
            if (frame.rolls.isNotEmpty()) {
                frame.score = frame.rolls.sum()
                if (frame.rolls[0] == 10 && index < 9) { // Strike
                    frame.score += getStrikeBonus(index)
                } else if (frame.rolls.size == 2 && frame.rolls.sum() == 10) { // Spare
                    frame.score += getSpareBonus(index)
                }
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
}
