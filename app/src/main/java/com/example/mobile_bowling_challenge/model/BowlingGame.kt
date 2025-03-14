package com.example.mobile_bowling_challenge.model

interface BowlingGame {
    fun startGame()
    fun roll(pins: Int)
    fun getCurrentFrame(): Int
    fun getScore(): Int
    fun getRemainingRolls(): Int
    fun isGameOver(): Boolean
    fun getGameState(): List<Frame>
}