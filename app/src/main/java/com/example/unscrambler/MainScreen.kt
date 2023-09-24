package com.example.unscrambler

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscrambler.mode.ModesDialog
import com.example.unscrambler.ui.theme.shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(gameViewModel: GameViewModel = viewModel()){

    val gameUiState by gameViewModel.uiState.collectAsState()

    var isMenuVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { isMenuVisible = !isMenuVisible }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = colorScheme.primary
                        )
                    }
                }
            )
        }
    ){

        if (isMenuVisible) {
            ModesDialog(
                showDialog = true,
                gameUiState = gameUiState,
                onDismissRequest = { isMenuVisible = !isMenuVisible },
                onConfirmClick = { gameViewModel.modeChange(it)} )
//            ModeDialogWithDropdown(
//                showDialog = true,
//                onDismissRequest = { /*TODO*/ },
//                onConfirmClick = { /*TODO*/ },
//                onDropdownItemSelected = { },
//                selectedItem = "Medium"
//            )
        }


        Column(
            modifier = Modifier
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val focusManager = LocalFocusManager.current

            GameLayout(
                currentScrambleWord = if(gameUiState.isSeeing) gameViewModel.currentWord else gameUiState.currentScrambleWord,
                isGuessWordWrong = gameUiState.isGuessWordWrong,
                currentWordCount = gameUiState.currentWordCount,
                userGuess = gameViewModel.userGuess,
                onUserGuessChanged = { guessWord->
                    gameViewModel.updateUserGuess(guessWord)
                    gameViewModel.auoHideHint()
                                     },
                onKeyboardDone = {
                    if(gameUiState.isViewedWord){
                        focusManager.clearFocus()
                    }
                    else {
                        gameViewModel.checkUserGuess()
                    }
                                 },
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
                },
                enabled = !gameUiState.isViewedWord
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ){

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(dimensionResource(id = R.dimen.padding_small)),
                    onClick = { if(gameUiState.isSeeing) gameViewModel.scrambleWord() else gameViewModel.seeWord() })

                {
                    if(gameUiState.isSeeing){
                        Text(text = stringResource(R.string.scramble_word), fontSize = 16.sp)
                    }
                    else {
                        Text(text = stringResource(R.string.see_word), fontSize = 16.sp)
                    }

                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(dimensionResource(id = R.dimen.padding_small)),
                    onClick = { if(gameUiState.isHint) gameViewModel.hideHint() else gameViewModel.seeHint() })

                {
                    if(gameUiState.isHint){
                        Text(text = stringResource(R.string.hide_hint), fontSize = 16.sp)
                    }
                    else {
                        Text(text = stringResource(R.string.see_hint), fontSize = 16.sp)
                    }

                }
            }



            GameStatus(
                score = gameUiState.score,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )


        }

        if (gameUiState.isGameOver) {
            EndDialog(score = gameUiState.score, onPlayAgain =  { gameViewModel.resetGame(gameUiState.currentMode) })
        }


    }



}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameLayout(
    currentScrambleWord: String,
    currentWordCount: Int,
    isGuessWordWrong: Boolean,
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
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = onUserGuessChanged,
                label = {

                    if(isGuessWordWrong){
                        Text(text = stringResource(id = R.string.wrong_guess))
                    }
                    else {
                        Text(stringResource(R.string.enter_your_word))
                    }
                        },
                isError = isGuessWordWrong,
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


@Composable
fun EndDialog(
    score: Int,
    onPlayAgain: ()-> Unit,
    modifier: Modifier = Modifier
) {

    val thisActivity = (LocalContext.current as Activity)

    AlertDialog(
        title = { Text(text = stringResource(R.string.congratulations))},
        text = { Text(text = stringResource(R.string.you_scored,score))},
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            TextButton(onClick =  onPlayAgain ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeDialogWithDropdown(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onDropdownItemSelected: (String) -> Unit,
    selectedItem: String
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { onDismissRequest() }
        ) {
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Choose Mode",
                        fontSize = 20.sp
                    )

                    DropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = showDialog,
                        onDismissRequest = { onDismissRequest() }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(text = "Easy")
                            },
                            onClick = {
                                onDropdownItemSelected("Easy")
                                onDismissRequest()
                            })

                        DropdownMenuItem(
                            text = {
                                Text(text = "Medium")
                            },
                            onClick = {
                                onDropdownItemSelected("Medium")
                                onDismissRequest()
                            })

                        DropdownMenuItem(
                            text = {
                                Text(text = "Hard")
                            },
                            onClick = {
                                onDropdownItemSelected("Hard")
                                onDismissRequest()
                            })

                    }

                    Text(
                        text = "Selected Item: $selectedItem",
                        fontSize = 16.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { onDismissRequest() }
                        ) {
                            Text(text = "Cancel")
                        }

                        TextButton(
                            onClick = {
                                onConfirmClick()
                                onDismissRequest()
                            }
                        ) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        }
    }
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

@Preview(showBackground = true)
@Composable
fun ModeDialogPreview(){
    UnScramblerTheme(darkTheme = false) {
        ModeDialogWithDropdown(
            showDialog = true,
            onDismissRequest = { /*TODO*/ },
            onConfirmClick = { /*TODO*/ },
            onDropdownItemSelected = { },
            selectedItem = "Medium"
        )
    }

}


