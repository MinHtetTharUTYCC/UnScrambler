package com.example.unscrambler

import com.example.unscrambler.mode.Mode

data class GameUiState(
    val currentScrambleWord: String = "",
    val currentWordCount: Int = 1,
    val isGuessWordWrong: Boolean = false,
    val currentMode: Mode = Mode.MEDIUM,
    val customCategory: String = "",
    val score: Int = 0,
    val isViewedWord: Boolean = false,
    val isSeeing: Boolean = false,
    val isHint: Boolean = false,
    val isGameOver: Boolean = false
)
