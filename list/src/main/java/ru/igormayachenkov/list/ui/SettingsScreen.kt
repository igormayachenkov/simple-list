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
import ru.igormayachenkov.list.data.Settings
import ru.igormayachenkov.list.ui.theme.ListTheme
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import ru.igormayachenkov.list.R
import ru.igormayachenkov.list.app


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel// = viewModel()
) {
    if(!viewModel.isVisible) return

    val settings:Settings by viewModel.settings.collectAsState()

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

    BackHandler(enabled = true, onBack = viewModel::hideSettings)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = viewModel::hideSettings),
        color = Color(0x80000040)
    ){
        Card(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 10.dp)
                .wrapContentHeight(align = Alignment.Top),
        ){
            Column(
                Modifier
                    .padding(all = 10.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(text = stringResource(R.string.settings_title),
                    Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6
                )
                Text(text = stringResource(R.string.common_version)+" "+app.version,
                    Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle2
                )
                Spacer(modifier = Modifier.height(8.dp))
                SwitcherRow(text = stringResource(R.string.settings_useadd_text), state = useAdd, help=help,
                        helpText = stringResource(R.string.settings_useadd_help))
                SwitcherRow(text = stringResource(R.string.settings_usefab_text),      state = useFab, help=help,
                        helpText = stringResource(R.string.settings_usefab_help))
//                SectionTitle(text = "Sorting")
//                SwitcherRow(text = "lists on the top",          state = sortListsUp, help=help,
//                    helpText="group lists together and keep them on the top of the sorted list")
//                SwitcherRow(text = "checked on the bottom",     state = sortCheckedDown, help=help,
//                    helpText="group checked items together and keep them on the bottom of the sorted list")
                SwitcherRow(text = stringResource(R.string.settings_confirmdelete_text),    state = confirmDelete, help=help,
                        helpText = stringResource(R.string.settings_confirmdelete_help))
                SwitcherRow(text = stringResource(R.string.settings_checkedcolor_text), enabled=!useOldListUi.value, state = useCheckedColor, help=help,
                        helpText = stringResource(R.string.settings_checkedcolor_help))
                SwitcherRow(text = stringResource(R.string.settings_oldui_text),    state = useOldListUi, help=help,
                        helpText = stringResource(R.string.settings_oldui_help))

                //----------------------------------------------------------------------------------
                // BUTTONS
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                {
                    // Cancel
                    Button(onClick = viewModel::hideSettings) {
                        Icon(Icons.Default.ArrowBack, "")
                    }
                    // Spacer
                    Spacer(modifier = Modifier.weight(1F))
                    // Save
                    Button(
                        onClick = { viewModel.onSave(newSettings); viewModel.hideSettings() },
                        enabled = settings!=newSettings
                    ){
                        Text(text = stringResource(R.string.common_button_save))
                    }
                }

                // Help text
                Text(text = stringResource(R.string.settings_help_label),
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
    Spacer(modifier = Modifier.height(5.dp))

}

@Preview(showBackground = false)
@Composable
private fun SettingsScreenPreview() {
    ListTheme(darkTheme = true) {
        SettingsScreen(viewModel = SettingsViewModel())
    }
}