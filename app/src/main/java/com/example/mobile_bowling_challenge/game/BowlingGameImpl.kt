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
        val rolls = frame.rolls

        val rollType = when {
            pins == ALL_PINS && rolls.size == 0 -> RollType.STRIKE
            rolls.size == 1 && rolls[0] + pins == ALL_PINS -> RollType.SPARE
            pins == 0 -> RollType.MISS
            else -> RollType.REGULAR
        }

        rolls.add(pins)
        frame.rollTypes.add(rollType)

        calculateScore()

        if (pins == ALL_PINS && rolls.size == 1) { // If strike, move to next frame (no second roll)
            currentFrame++
        } else if (rolls.size == MAX_ROLLS) { // Regular roll
            currentFrame++
        }

        // Add bonus frame after strike/spare at 10th frame
        if (currentFrame == 10 && rolls.sum() == ALL_PINS) frames.add(Frame())
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
        val rolls = frame.rolls

        return when {
            // For spare or strike in 10th frame, 2 rolls remaining otherwise no extra rolls
            currentFrame == TENTH_FRAME -> {
                if ((rolls.size == MAX_ROLLS && rolls.sum() == ALL_PINS) ||
                    (rolls.size == 1 && rolls[0] == ALL_PINS)
                ) MAX_ROLLS else 0
            }

            // First roll of a normal frame, 2 rolls left
            rolls.isEmpty() -> MAX_ROLLS

            // Strike, no second roll otherwise first roll done, 1 roll left
            rolls.size == 1 -> if (rolls[0] == ALL_PINS) 0 else 1

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
            val rolls = frame.rolls

            if (rolls.isNotEmpty()) {
                // If it's a strike and there are two subsequent rolls
                if (rolls[0] == ALL_PINS && index < TENTH_FRAME) {
                    // Ensure that the next two rolls are available
                    if (frames.getOrNull(index + 1)?.rolls?.size == 2) {
                        frame.isComplete = true
                    }
                }

                // If it's a spare and there is a next roll
                else if (rolls.size == MAX_ROLLS && rolls.sum() == ALL_PINS) {
                    // Ensure that the next roll is available
                    if (frames.getOrNull(index + 1)?.rolls?.isNotEmpty() == true) {
                        frame.isComplete = true
                    }
                }

                // Regular frame
                else {
                    // Frame is complete once both rolls are done
                    if (rolls.size == MAX_ROLLS) {
                        frame.isComplete = true
                    }
                }

                if (frame.isComplete) {
                    var frameScore = rolls.sum()
                    frameScore += cumulativeScore

                    // Add bonuses if it's a strike or spare
                    if (rolls[0] == ALL_PINS && index < TENTH_FRAME) {
                        frameScore += getStrikeBonus(index)
                    } else if (rolls.size == MAX_ROLLS && rolls.sum() == ALL_PINS) {
                        frameScore += getSpareBonus(index)
                    }

                    // Set the frame's score
                    frame.score = frameScore
                    cumulativeScore = frame.score
                    gameScore = cumulativeScore
                }
            }
        }
    }


    private fun getStrikeBonus(index: Int): Int {
        // If strike, add next two rolls from subsequent frames
        val nextFrame = frames.getOrNull(index + 1)
        val bonus = (nextFrame?.rolls?.getOrNull(0) ?: 0) + (nextFrame?.rolls?.getOrNull(1) ?: 0)

        // Mark the current frame as complete only after getting the bonus
        if (nextFrame?.rolls?.size == 2) {
            frames[index].isComplete = true
        }

        return bonus
    }

    private fun getSpareBonus(index: Int): Int {
        // If spare, add next roll from the following frame
        val nextFrame = frames.getOrNull(index + 1)
        val bonus = nextFrame?.rolls?.getOrNull(0) ?: 0

        // Mark the current frame as complete only getting the bonus
        if (nextFrame?.rolls?.isNotEmpty() == true) {
            frames[index].isComplete = true
        }

        return bonus
    }

    companion object {
        const val ALL_PINS = 10
        const val TENTH_FRAME = 9
        const val MAX_ROLLS = 2
    }
}
