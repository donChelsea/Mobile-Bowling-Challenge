package com.example.mobile_bowling_challenge.game

import com.example.mobile_bowling_challenge.model.Frame

interface BowlingGame {
    fun startGame()
    fun roll(pins: Int)
    fun getCurrentFrame(): Int
    fun getScore(): Int
    fun getRemainingRolls(): Int
    fun isGameOver(): Boolean
    fun getGameState(): List<Frame>
}