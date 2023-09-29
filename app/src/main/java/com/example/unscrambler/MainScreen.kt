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
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unscrambler.dictionary.Dictionary

import com.example.unscrambler.mode.ModesDialog
import com.example.unscrambler.ui.theme.shapes
import java.util.jar.Attributes.Name


enum class Names{
    Main,Definition
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(gameViewModel: GameViewModel = viewModel(),navController: NavHostController = rememberNavController()){

    val gameUiState by gameViewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = Names.Main.name) {

        composable(Names.Main.name) {
            UnScrambleScreen(
                gameViewModel = gameViewModel,
                gameUiState = gameUiState,
                onDeff = {
                    navController.navigate(Names.Definition.name)
                    Log.d("onDef:","1111111")
                }
            )
        }

        composable(Names.Definition.name) {
            Dictionary(
                gameViewModel = gameViewModel,
                onDefOff = { navController.navigate(Names.Definition.name) }
            )
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




