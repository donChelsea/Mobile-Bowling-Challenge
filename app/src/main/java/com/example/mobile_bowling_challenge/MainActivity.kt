package com.example.mobile_bowling_challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_bowling_challenge.ui.theme.Mobile_Bowling_ChallengeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobile_Bowling_ChallengeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BowlingScreen(BowlingGameImpl(), modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BowlingScreen(bowlingGame: BowlingGameImpl, modifier: Modifier = Modifier) {
    var pins by remember { mutableIntStateOf(0) }

    Column(modifier = modifier) {
        // Display score and current frame
        Text(text = "Frame: ${bowlingGame.getCurrentFrame()}")
        Text(text = "Rolls remaining: ${bowlingGame.getRemainingRolls()}")
        Text(text = "Current Score: ${bowlingGame.getScore()}")

        // Input for pins knocked down
        TextField(
            value = pins.toString(),
            onValueChange = { newPins ->
                pins = newPins.toIntOrNull() ?: 0
            },
            label = { Text("Enter Pins Knocked Down") }
        )

        Button(onClick = {
            bowlingGame.roll(pins)
            pins = 0
        }) {
            Text("Submit Roll")
        }

        // Show game state
        BowlingGameStateView(bowlingGame.getGameState())
    }
}

@Composable
fun BowlingGameStateView(frames: List<Frame>) {
    // Render frames and their respective scores
    LazyColumn {
        items(frames) { frame ->
            Text(text = "Rolls: ${frame.rolls.joinToString(", ")} Score: ${frame.score}")
        }
    }
}


// Enum to represent different types of rolls
enum class RollType {
    STRIKE, SPARE, MISS
}

// A frame represents a single round in bowling.
data class Frame(
    val rolls: MutableList<Int> = mutableListOf(),
    var score: Int = 0
)

interface BowlingGame {
    fun roll(pins: Int)
    fun getCurrentFrame(): Int
    fun getScore(): Int
    fun getRemainingRolls(): Int
    fun isGameOver(): Boolean
    fun getGameState(): List<Frame>
}

class BowlingGameImpl : BowlingGame {
    private val frames = mutableListOf<Frame>()
    private var currentFrame = 0

    init {
        // Initialize the frames (10 frames for a game)
        repeat(10) { frames.add(Frame()) }
    }

    override fun roll(pins: Int) {
        if (isGameOver()) return

        val frame = frames[currentFrame]

        // Add the roll to the current frame
        frame.rolls.add(pins)

        if (pins == 10 && frame.rolls.size == 1) { // If strike, move to next frame (no second roll in the frame)
            currentFrame++
        } else if (frame.rolls.size == 2 || (currentFrame == 9 && frame.rolls.size == 3)) {
            currentFrame++
        }

        if (currentFrame == 10 && frame.rolls.sum() == 10) frames.add(Frame())

        // Calculate score after each roll
        calculateScore()

        // If scored 10 at tenth frame, add bonus round
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
            currentFrame == 9 -> {  // Special logic for the 10th frame
                if (frame.rolls.size == 2 && frame.rolls.sum() == 10) {
                    1 // Spare in 10th frame, 1 roll remaining
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
//        if (isGameOver()) return

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
