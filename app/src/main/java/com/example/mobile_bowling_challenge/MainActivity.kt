package com.example.mobile_bowling_challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.mobile_bowling_challenge.game.BowlingGameImpl
import com.example.mobile_bowling_challenge.game.ui.BowlingScreen
import com.example.mobile_bowling_challenge.ui.theme.Mobile_Bowling_ChallengeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobile_Bowling_ChallengeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BowlingScreen(
                        bowlingGame = BowlingGameImpl(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

