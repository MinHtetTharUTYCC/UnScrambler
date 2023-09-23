package com.example.unscrambler.mode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.unscrambler.UnScramblerTheme

@Composable
fun ModesDialog(
    showDialog: Boolean,
    onDismissRequest: ()-> Unit,
    onConfirmClick: () -> Unit
) {

    var easyMode by remember { mutableStateOf(false) }
    var mediumMode by remember { mutableStateOf(true) }
    var hardMode by remember { mutableStateOf(false) }

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
                            }
                            Mode.MEDIUM -> {
                                easyMode = false
                                hardMode = false
                            }
                            Mode.HARD -> {
                                easyMode = false
                                mediumMode = false
                            }
                        }

                    }
                    
                    ModeToggle(
                        label = Mode.EASY,
                        desc = "3-4",
                        isChecked = easyMode,
                        onToggle = {
                            easyMode = it
                            makeOnlyOneMode(Mode.EASY)
                        }
                    )
                    ModeToggle(
                        label = Mode.MEDIUM,
                        desc = "5-7",
                        isChecked = mediumMode,
                        onToggle = {
                            mediumMode = it
                            makeOnlyOneMode(Mode.MEDIUM)
                        }
                    )
                    ModeToggle(
                        label = Mode.HARD,
                        desc = "8 and above ",
                        isChecked = hardMode,
                        onToggle = {
                            hardMode = it
                            makeOnlyOneMode(Mode.HARD)
                        }
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
            .padding(8.dp),
        horizontalArrangement =  Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (
        ){
            Text(text = label.toString(), fontSize = 16.sp)
            Text(text = desc + " characters", fontSize = 16.sp, color = colorScheme.primary)
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

enum class Mode{
    EASY,MEDIUM,HARD
}



@Preview
@Composable
fun ModesDialogPrw(){
    UnScramblerTheme {
        ModesDialog(
            showDialog = true,
            onDismissRequest = { /*TODO*/ },
            onConfirmClick = { /*TODO*/ })
    }
}
