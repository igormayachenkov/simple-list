package ru.igormayachenkov.list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.igormayachenkov.list.data.Settings
import ru.igormayachenkov.list.ui.theme.ListTheme
import androidx.activity.compose.BackHandler
import androidx.compose.ui.text.style.TextAlign


@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    onHide:()->Unit
) {
    val settings:Settings by settingsViewModel.settings.collectAsState()

    val useFab          = rememberSaveable{ mutableStateOf<Boolean>(settings.useFab) }
    val useAdd          = rememberSaveable{ mutableStateOf<Boolean>(settings.useAdd) }
    val useCheckedColor = rememberSaveable{ mutableStateOf<Boolean>(settings.useCheckedColor) }
    val sortListsUp     = rememberSaveable{ mutableStateOf<Boolean>(settings.sortListsUp) }
    val sortCheckedDown = rememberSaveable{ mutableStateOf<Boolean>(settings.sortCheckedDown) }

    val newSettings = settings.copy(
        useFab=useFab.value,
        useAdd=useAdd.value,
        useCheckedColor=useCheckedColor.value,
        sortListsUp = sortListsUp.value,
        sortCheckedDown = sortCheckedDown.value
    )

    BackHandler(enabled = true, onBack = onHide)

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0x80000040)
    ){
        Card(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentHeight(align = Alignment.Top),
        ){
            Column(
                Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth()
                //.padding(all = 16.dp)
            ) {
                Text(text = "Settings",
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                SwitcherRow(text = "\"Create\" icon",           state = useAdd)
                SwitcherRow(text = "\"Create\" floating button",state = useFab)
                SwitcherRow(text = "Checked items dimming",     state = useCheckedColor)
                SwitcherRow(text = "Lists on the top",          state = sortListsUp)
                SwitcherRow(text = "Checked on the bottom",     state = sortCheckedDown)

                // BUTTONS
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                {
                    // Cancel
                    Button(onClick = onHide) {
                        Icon(Icons.Default.ArrowBack, "close")
                    }
                    // Spacer
                    Spacer(modifier = Modifier.weight(1F))
                    // Save
                    Button(
                        onClick = { settingsViewModel.onSave(newSettings); onHide() },
                        enabled = settings!=newSettings
                    ){
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitcherRow(text:String, state:MutableState<Boolean>){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text( text = text, Modifier.weight(1f))
        Switch(checked = state.value, onCheckedChange = {state.value=it})
    }

}

@Preview(showBackground = false)
@Composable
fun SettingsScreenPreview() {
    ListTheme(darkTheme = true) {
        SettingsScreen(settingsViewModel = SettingsViewModel(), onHide={} )
    }
}