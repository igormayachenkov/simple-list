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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import ru.igormayachenkov.list.app


@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    onHide:()->Unit
) {
    val settings:Settings by settingsViewModel.settings.collectAsState()

    val useFab          = rememberSaveable{ mutableStateOf<Boolean>(settings.useFab) }
    val useAdd          = rememberSaveable{ mutableStateOf<Boolean>(settings.useAdd) }
    val useCheckedColor = rememberSaveable{ mutableStateOf<Boolean>(settings.useCheckedColor) }
    val useOldListUi    = rememberSaveable{ mutableStateOf<Boolean>(settings.useOldListUi) }
    val confirmDelete   = rememberSaveable{ mutableStateOf<Boolean>(settings.confirmDelete) }
//    val sortListsUp     = rememberSaveable{ mutableStateOf<Boolean>(settings.sortListsUp) }
//    val sortCheckedDown = rememberSaveable{ mutableStateOf<Boolean>(settings.sortCheckedDown) }
    val help            = rememberSaveable{ mutableStateOf<String?>(null) }


    val newSettings = settings.copy(
        useFab=useFab.value,
        useAdd=useAdd.value,
        useCheckedColor=useCheckedColor.value,
        useOldListUi=useOldListUi.value,
        confirmDelete=confirmDelete.value
        //sortListsUp = sortListsUp.value,
        //sortCheckedDown = sortCheckedDown.value
    )

    BackHandler(enabled = true, onBack = onHide)

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0x80000040)
    ){
        Card(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .wrapContentHeight(align = Alignment.Top),
        ){
            Column(
                Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(text = "Settings",
                    Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6
                )
                Text(text = "app version: ${app.version}",
                    Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle2
                )
                SwitcherRow(text = "\"new\" icon",         state = useAdd, help=help,
                    helpText="show \"add new\" icon on the top bar")
                SwitcherRow(text = "\"new\" floating button",      state = useFab, help=help,
                    helpText="show \"add new\" floating button in the bottom right screen corner")
//                SectionTitle(text = "Sorting")
//                SwitcherRow(text = "lists on the top",          state = sortListsUp, help=help,
//                    helpText="group lists together and keep them on the top of the sorted list")
//                SwitcherRow(text = "checked on the bottom",     state = sortCheckedDown, help=help,
//                    helpText="group checked items together and keep them on the bottom of the sorted list")
                SwitcherRow(text = "confirm delete item",    state = confirmDelete, help=help,
                    helpText="show confirmation dialog on delete item click")
                SwitcherRow(text = "checked items dimming", enabled=!useOldListUi.value, state = useCheckedColor, help=help,
                    helpText="paint the checked elements by gray color")
                SwitcherRow(text = "use old (version 1) UI",    state = useOldListUi, help=help,
                    helpText="click: check/uncheck the item\nlong click: open the item")

                //----------------------------------------------------------------------------------
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

                // Help text
                Text(text = "Click the texts to get help",
                    Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colors.onPrimary.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.subtitle2

                )

                // HELP POPUP
                help.value?.let { helpText->
                    AlertDialog(
                        text = { Text(text = helpText)},
                        onDismissRequest = { help.value=null },
                        buttons = { }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text:String){
    Text(
        text = text,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colors.onPrimary.copy(alpha = 0.6f),
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
private fun SwitcherRow(text:String, helpText:String, state:MutableState<Boolean>, help:MutableState<String?>, enabled:Boolean=true){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text( text = text, Modifier
            .weight(1f)
            .clickable { help.value = helpText }
        )
        Switch(checked = state.value, onCheckedChange = {state.value=it}, enabled = enabled)
    }

}

@Preview(showBackground = false)
@Composable
private fun SettingsScreenPreview() {
    ListTheme(darkTheme = true) {
        SettingsScreen(settingsViewModel = SettingsViewModel(), onHide={} )
    }
}