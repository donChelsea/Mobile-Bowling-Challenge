package com.example.mobile_bowling_challenge.model

enum class RollType(var symbol: String) {
    STRIKE("X"),
    SPARE("/"),
    MISS("-"),
    REGULAR("")
}