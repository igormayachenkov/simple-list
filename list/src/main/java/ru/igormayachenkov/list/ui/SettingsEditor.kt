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
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.Settings
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun SettingsEditor(
    settings: Settings,
    onClose:()->Unit,
    onSave:(Settings)->String?
) {
    var useFab by rememberSaveable{ mutableStateOf<Boolean>(settings.useFab) }
    val newSettings = Settings(useFab)

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

                // Is Checkable
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

                // BUTTONS
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                {
                    // Cancel
                    Button(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, "close")
                    }
                    // Spacer
                    Spacer(modifier = Modifier.weight(1F))
                    // Save
                    Button(
                        onClick = {onSave(newSettings)},
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
fun SettingsEditorPreview() {
    ListTheme(darkTheme = true) {
        SettingsEditor(settings = Settings(), onClose={}, onSave={null} )
    }
}