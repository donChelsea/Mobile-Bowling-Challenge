package com.example.mobile_bowling_challenge.model

data class Frame(
    val rolls: MutableList<Int> = mutableListOf(),
    val rollTypes: MutableList<RollType> = mutableListOf(),
    var score: Int = 0
)
