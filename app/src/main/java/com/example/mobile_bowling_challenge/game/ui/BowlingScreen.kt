package com.example.mobile_bowling_challenge.game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.mobile_bowling_challenge.R
import com.example.mobile_bowling_challenge.game.BowlingGameImpl
import com.example.mobile_bowling_challenge.model.Frame
import com.example.mobile_bowling_challenge.model.RollType

@Composable
fun BowlingScreen(
    bowlingGame: BowlingGameImpl,
    modifier: Modifier = Modifier
) {
    var pins by remember { mutableIntStateOf(0) }
    val buttonText = if (bowlingGame.isGameOver()) R.string.game_over else R.string.submit_roll

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_8dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.label_frame, bowlingGame.getCurrentFrame()),
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_8dp)))

        Text(
            text = stringResource(R.string.label_rolls_remaining, bowlingGame.getRemainingRolls()),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_8dp)))

        Text(
            text = stringResource(R.string.label_current_score, bowlingGame.getScore()),
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_20dp)))

        TextField(
            value = pins.toString(),
            onValueChange = { newPins ->
                pins = newPins.toIntOrNull() ?: 0
            },
            label = { Text(stringResource(R.string.enter_pins)) }
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_12dp)))

        Button(
            onClick = {
                bowlingGame.roll(pins)
                pins = 0
            },
            enabled = !bowlingGame.isGameOver()
        ) {
            Text(stringResource(buttonText))
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_20dp)))

        BowlingGameStateView(bowlingGame.getGameState())
    }
}

@Composable
fun BowlingGameStateView(frames: List<Frame>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(frames) { frame ->
            val rollDisplay = frame.rolls.zip(frame.rollTypes) { pins, rollType ->
                when (rollType) {
                    RollType.STRIKE -> RollType.STRIKE.symbol
                    RollType.SPARE -> RollType.SPARE.symbol
                    RollType.MISS -> RollType.MISS.symbol
                    RollType.REGULAR -> pins.toString()
                }
            }.joinToString(", ", prefix = "[", postfix = "]")

            Text(
                text = stringResource(R.string.label_rolls_score, rollDisplay, frame.score),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_12dp)))
        }
    }
}
