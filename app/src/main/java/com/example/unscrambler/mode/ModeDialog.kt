package com.example.unscrambler.mode

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.unscrambler.GameUiState
import com.example.unscrambler.GameViewModel
import com.example.unscrambler.UnScramblerTheme
import com.example.unscrambler.data.customList
import kotlinx.coroutines.flow.MutableStateFlow



@Composable
fun ModesDialog(
    showDialog: Boolean,
    gameUiState: GameUiState,
    onDismissRequest: ()-> Unit,
    onConfirmClick: (mode: Mode,category: String) -> Unit
) {

    var easyMode by remember { mutableStateOf(false) }
    var mediumMode by remember { mutableStateOf(false) }
    var hardMode by remember { mutableStateOf(false) }
    var customMode by remember { mutableStateOf(false) }

    var isCustomPickerAvailable by remember { mutableStateOf(false) }
    var customFilter by remember { mutableStateOf("Dream") }

    when(gameUiState.currentMode){
        Mode.EASY-> easyMode = true
        Mode.MEDIUM-> mediumMode = true
        Mode.HARD-> hardMode = true
        Mode.CUSTOM -> customMode = true
    }


    if (showDialog) {
        Dialog(onDismissRequest = { onDismissRequest()}) {
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(35.dp))
            ) {
                Column (
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = "Select Modes", style = typography.titleLarge)

                    fun makeOnlyOneMode(mode: Mode){
                        when(mode){
                            Mode.EASY -> {
                                mediumMode = false
                                hardMode = false
                                customMode = false
                            }
                            Mode.MEDIUM -> {
                                easyMode = false
                                hardMode = false
                                customMode = false
                            }
                            Mode.HARD -> {
                                easyMode = false
                                mediumMode = false
                                customMode = false
                            }
                            Mode.CUSTOM -> {
                                easyMode = false
                                mediumMode = false
                                hardMode = false
                            }
                        }

                    }
                    
                    ModeToggle(
                        label = Mode.EASY,
                        desc = "3-4 characters",
                        isChecked = easyMode,
                        onToggle = {
                            if (it) {
                                easyMode = it
                                makeOnlyOneMode(Mode.EASY)
                            }

                        }
                    )
                    ModeToggle(
                        label = Mode.MEDIUM,
                        desc = "5-7 characters",
                        isChecked = mediumMode,
                        onToggle = {
                            if (it) {
                                mediumMode = it
                                makeOnlyOneMode(Mode.MEDIUM)
                            }

                        }
                    )
                    ModeToggle(
                        label = Mode.HARD,
                        desc = "8 and above characters ",
                        isChecked = hardMode,
                        onToggle = {
                            if(it){
                                hardMode = it
                                makeOnlyOneMode(Mode.HARD)
                            }

                        }
                    )
                    ModeToggle(
                        label = Mode.CUSTOM,
                        desc = "Categorized by $customFilter",
                        isChecked = customMode,
                        onToggle = {
                            if (it) {
                                customMode = it
                                makeOnlyOneMode(Mode.CUSTOM)
                                isCustomPickerAvailable = it
                            }

                        }
                    )

                    if (isCustomPickerAvailable) {
                        VerticalCustomPicker(
                            list = customList,
                            onValueChange = { currentValue ->
                                Log.d("my_tag:",currentValue)
                                isCustomPickerAvailable = !isCustomPickerAvailable
                                customFilter = currentValue
                            })
                    }


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
                                val selectedMode = when{
                                    easyMode -> Mode.EASY
                                    mediumMode -> Mode.MEDIUM
                                    hardMode -> Mode.HARD
                                    else -> Mode.CUSTOM
                                }
                                val category = if(selectedMode == Mode.CUSTOM) customFilter else ""
                                onConfirmClick(selectedMode,category)
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

@Composable
fun ModeToggle(
    label: Mode,
    desc: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onToggle(true) },
        horizontalArrangement =  Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (
        ){
            Text(text = label.toString(), fontSize = 16.sp)
            Text(text = desc , fontSize = 16.sp, color = colorScheme.primary)
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle(it) },
            thumbContent = {
                if(isChecked){
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                } else {
                    null
                }
            }
        )

    }
}

@Composable
fun VerticalCustomPicker(
    list: List<String>,
    onValueChange: (String) -> Unit
) {
    var currentValue by remember { mutableStateOf(list[0]) }

    Dialog(onDismissRequest = { /*TODO*/ }) {

        Surface(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(35.dp))
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(list) { value ->
                    CustomPickerItem(
                        value = value,
                        isSelected = value == currentValue,
                        onValueSelected = { selectedValue ->
                            currentValue = selectedValue
                            onValueChange(selectedValue)
                        }
                    )
                }
            }


        }


    }


}


@Composable
fun CustomPickerItem(
    value: String,
    isSelected: Boolean,
    onValueSelected: (String) -> Unit
) {
//    val backgroundColor = if (isSelected) colorScheme.primary else Color.Transparent

    Card (
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onValueSelected(value) },
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        )
        {

            Text(
                text = value,
                fontSize = 24.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                color = if (isSelected) colorScheme.primary else Color.Black
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = colorScheme.primary
                )
            }
        }
    }


}


enum class Mode{
    EASY,MEDIUM,HARD,CUSTOM
}

@Preview
@Composable
fun newPrw(){
    UnScramblerTheme {
        VerticalCustomPicker(
            list = customList,
            onValueChange = {})
    }
}

@Preview
@Composable
fun EachItemPrw(){
    UnScramblerTheme {
        CustomPickerItem(
            value = "Good",
            isSelected = true,
            onValueSelected = { })
    }
}


@Preview
@Composable
fun ModesDialogPrw(){
    UnScramblerTheme {

        val gameUiState by GameViewModel().uiState.collectAsState()

        ModesDialog(
            showDialog = true,
            gameUiState = gameUiState,
            onDismissRequest = { /*TODO*/ },
            onConfirmClick = { selectedMode, category ->

            })
    }
}
