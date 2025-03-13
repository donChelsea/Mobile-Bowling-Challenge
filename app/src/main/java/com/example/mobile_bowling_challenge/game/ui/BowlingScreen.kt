package com.example.mobile_bowling_challenge.game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mobile_bowling_challenge.game.BowlingGameImpl
import com.example.mobile_bowling_challenge.model.Frame
import com.example.mobile_bowling_challenge.model.RollType

@Composable
fun BowlingScreen(
    bowlingGame: BowlingGameImpl,
    modifier: Modifier = Modifier
) {
    var pins by remember { mutableIntStateOf(0) }

    Column(modifier = modifier) {
        Text(text = "Frame: ${bowlingGame.getCurrentFrame()}")
        Text(text = "Rolls remaining: ${bowlingGame.getRemainingRolls()}")
        Text(text = "Current Score: ${bowlingGame.getScore()}")

        TextField(
            value = pins.toString(),
            onValueChange = { newPins ->
                pins = newPins.toIntOrNull() ?: 0
            },
            label = { Text("Enter Pins Knocked Down") }
        )

        Button(
            onClick = {
                bowlingGame.roll(pins)
                pins = 0
            },
            enabled = !bowlingGame.isGameOver()
        ) {
            Text("Submit Roll")
        }

        BowlingGameStateView(bowlingGame.getGameState())
    }
}

@Composable
fun BowlingGameStateView(frames: List<Frame>) {
    LazyColumn {
        items(frames) { frame ->
            val rollDisplay = frame.rolls.zip(frame.rollTypes) { pins, rollType ->
                when (rollType) {
                    RollType.STRIKE -> RollType.STRIKE.symbol
                    RollType.SPARE -> RollType.SPARE.symbol
                    RollType.MISS -> RollType.MISS.symbol
                    RollType.REGULAR -> pins.toString()
                }
            }.joinToString(", ", prefix = "[", postfix = "]")

            Text(text = "Rolls: $rollDisplay, Score: ${frame.score}")
        }
    }
}
