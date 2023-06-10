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


@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
    onHide:()->Unit
) {
    val settings:Settings by settingsViewModel.settings.collectAsState()

    var useFab by rememberSaveable{ mutableStateOf<Boolean>(settings.useFab) }
    var useAdd by rememberSaveable{ mutableStateOf<Boolean>(settings.useAdd) }
    var useCheckedColor by rememberSaveable{ mutableStateOf<Boolean>(settings.useCheckedColor) }
    val newSettings = settings.copy(useFab=useFab, useAdd=useAdd, useCheckedColor=useCheckedColor)

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
                Text("Settings")

                // Add
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Text( text = "Use Add button")
                    Spacer( modifier = Modifier.width(5.dp))
                    Switch(checked = useAdd, onCheckedChange = { useAdd = it })
                }
                // Fab
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Text( text = "Use FAB")
                    Spacer( modifier = Modifier.width(5.dp))
                    Switch(checked = useFab, onCheckedChange = { useFab = it })
                }
                // Use checked color
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Text( text = "Dimm checked items")
                    Spacer( modifier = Modifier.width(5.dp))
                    Switch(checked = useCheckedColor, onCheckedChange = { useCheckedColor = it })
                }

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

@Preview(showBackground = false)
@Composable
fun SettingsScreenPreview() {
    ListTheme(darkTheme = true) {
        SettingsScreen(onHide={} )
    }
}