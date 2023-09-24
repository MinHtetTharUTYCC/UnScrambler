package com.example.unscrambler

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.ViewModel
import com.example.unscrambler.data.MAX_NO_OF_WORDS
import com.example.unscrambler.data.SCORE_INCREASE
import com.example.unscrambler.data.allWords
import com.example.unscrambler.data.easyWords
import com.example.unscrambler.data.hardWords
import com.example.unscrambler.data.mediumWords
import com.example.unscrambler.mode.Mode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update



class GameViewModel: ViewModel() {

    var userGuess by mutableStateOf("")
        private set

    //game ui state
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

//    var currentMode: Mode = _uiState.value.currentMode

    lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()


    fun updateUserGuess(guessWord: String) {
        userGuess = guessWord
        Log.d("userGuess:",guessWord)
    }

    fun checkUserGuess() {

        if(userGuess.equals(currentWord, ignoreCase = true)){
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        }
        else {
            updateGameWrongState()
        }

        //reset user Guess
        updateUserGuess("")
        
    }



    private fun updateGameState(updatedScore: Int) {
        if(usedWords.size == MAX_NO_OF_WORDS){
            //last round
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessWordWrong = false,
                    isSeeing = false,
                    isViewedWord = false,
                    isHint = false,
                    score = updatedScore,
                    currentMode = currentState.currentMode,
                    isGameOver = true
                )
            }

        }
        else {
            //normal round
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessWordWrong = false,
                    isSeeing = false,
                    isViewedWord = false,
                    isHint = false,
                    score = updatedScore,
                    currentMode = currentState.currentMode,
                    currentScrambleWord = pickRandomWordAndShuffle(currentState.currentMode),
                    currentWordCount = currentState.currentWordCount.inc()

                )
            }
            
        }
    }

    private fun updateGameWrongState(){
        _uiState.update { currentState ->
            _uiState.value.copy(
                isGuessWordWrong = true,
                isHint = false,
                currentMode = currentState.currentMode
            )
        }
    }

    private fun pickRandomWordAndShuffle(mode: Mode): String {

        currentWord = when(mode) {
            Mode.EASY -> easyWords.random();
            Mode.MEDIUM -> mediumWords.random()
            Mode.HARD -> hardWords.random()
        }


        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle(mode)
        }
        else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(currentWord: String): String {
        val tempWord = currentWord.toCharArray()

        tempWord.shuffle()
        while (String(tempWord).equals(currentWord)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }



    fun skipGame() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    fun seeWord() {
        _uiState.update { currentState->
            currentState.copy(
                isSeeing =  true,
                isViewedWord = true
            )
        }
    }

    fun scrambleWord(){
        _uiState.update { currentState->
            currentState.copy(
                isSeeing =  false
            )
        }
    }

    fun seeHint(){
        updateUserGuess(currentWord.get(0).toString())

        _uiState.update { currentState->
            currentState.copy(
                isHint = true,
                isGuessWordWrong = false
            )

        }
    }
    fun hideHint(){
        updateUserGuess("")

        _uiState.update { currentState->
            currentState.copy(
                isHint = false,
                isGuessWordWrong = false
            )

        }
    }

    fun auoHideHint(){
        _uiState.update { currentState->
            currentState.copy(
                isHint = false,
            )
        }

    }



    fun modeChange(mode: Mode){
        updateUserGuess("")
        resetGame(mode)
    }


    init {
        resetGame(Mode.MEDIUM)

    }

    fun resetGame(mode: Mode) {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambleWord = pickRandomWordAndShuffle(mode), currentMode = mode)

    }

}

