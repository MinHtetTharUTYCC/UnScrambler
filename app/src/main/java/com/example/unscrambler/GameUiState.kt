package com.example.unscrambler

data class GameUiState(
    val currentScrambleWord: String = "",
    val currentWordCount: Int = 1,
    val isGuessWordWrong: Boolean = false,
    val score: Int = 0,
    val isViewedWord: Boolean = false,
    val isSeeing: Boolean = false,
    val isGameOver: Boolean = false
)
