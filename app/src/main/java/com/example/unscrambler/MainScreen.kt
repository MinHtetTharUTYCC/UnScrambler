package com.example.unscrambler

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscrambler.ui.theme.shapes

@Composable
fun MainScreen(gameViewModel: GameViewModel = viewModel()){

    val gameUiState by gameViewModel.uiState.collectAsState()


    Column(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.app_name),
            style = typography.titleLarge
        )

        GameLayout(
            currentScrambleWord = gameUiState.currentScrambleWord,
            currentWordCount = gameUiState.currentWordCount,
            userGuess = gameViewModel.userGuess,
            onUserGuessChanged = { guessWord-> gameViewModel.updateUserGuess(guessWord)},
            onKeyboardDone = { gameViewModel.checkUserGuess() },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_small)),
            onClick = {
                gameViewModel.checkUserGuess()
            }
        ) {
            Text(text = stringResource(R.string.submit), fontSize = 16.sp)
            
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_small)),
            onClick = { gameViewModel.skipGame() }
        ) {
            Text(text = "Skip", fontSize = 16.sp)
        }

        GameStatus(
            score = gameUiState.score,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        )
        

    }

    //EndDialog(100)


}



@Composable
fun GameStatus(
    score: Int,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier
    ){
        Text(
            text = stringResource(R.string.score,score),
            style = typography.headlineMedium,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)))
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameLayout(
    currentScrambleWord: String,
    currentWordCount: Int,
    userGuess: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
            modifier = modifier
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(
                modifier = Modifier
                    .clip(shapes.medium)
                    .background(colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(Alignment.End),
                text = stringResource(R.string.word_count,currentWordCount),
                style = typography.titleMedium,
                color = colorScheme.onPrimary,
                )

            Text(
                text = currentScrambleWord,
                fontSize = 45.sp,
                style = typography.displayMedium
            )
            
            Text(
                text = stringResource(R.string.instruction),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )

            OutlinedTextField(
                value = userGuess,
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onUserGuessChanged,
                label = {
                    Text(stringResource(R.string.enter_your_word))
                        },
                isError = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDone() }
                )
            )

        }


    }

}


@Composable
fun EndDialog(
    score: Int,
    modifier: Modifier = Modifier
) {

    val thisActivity = (LocalContext.current as Activity)

    AlertDialog(
        title = { Text(text = stringResource(R.string.congratulations))},
        text = { Text(text = stringResource(R.string.you_scored,score))},
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = stringResource(R.string.play_again))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                thisActivity.finish()
            }) {
                Text(text = stringResource(R.string.exit))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    UnScramblerTheme(darkTheme = false) {
        MainScreen()
    }

}

@Preview(showBackground = false)
@Composable
fun MainScreenDarkPreview(){
    UnScramblerTheme(darkTheme = true) {
        MainScreen()
    }

}

