package ru.igormayachenkov.list.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun AppBar(
    isRoot:Boolean,
    title:String,
    showInfoScreen:()->Unit,
    showSettingsScreen:()->Unit,
    onBack:()->Unit,
    onEdit:()->Unit,
    onCreate:()->Unit,
    showOnCreate:Boolean
){
    var showMenu by remember { mutableStateOf(false) }
    fun onMenuItem(handler:()->Unit){showMenu=false; handler()}

    var navIcon : @Composable (() -> Unit)? = null
    if (!isRoot){
        navIcon = @Composable {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack,"")
            }
        }
    }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        navigationIcon = navIcon,
        title = {
            // Title
            Text(text = title, color = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { if (!isRoot) onEdit() }))
        },
        actions = {
            // Create Button
            if(showOnCreate) IconButton(onClick = onCreate) {
                Icon(Icons.Default.AddCircle,"",
                    tint = MaterialTheme.colors.onPrimary)
            }
            // Menu Button
            IconButton(onClick = { showMenu=true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
            }
            // Menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu=false }
            ) {
                // - Edit
                if(!isRoot) {
                    DropdownMenuItem(onClick = { onMenuItem(onEdit) }) {
                        Icon(Icons.Default.Edit,"")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit list")
                    }
                    Divider()
                }
                // - Data
                DropdownMenuItem(onClick = { onMenuItem(showInfoScreen) }) {
                    Icon(Icons.Default.Info,"")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Data")
                }
                // - Settings
                DropdownMenuItem(onClick = { onMenuItem(showSettingsScreen) }) {
                    Icon(Icons.Default.Settings,"")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Settings")
                }
            }
        }
    )
}
//--------------------------------------------------------------------------------------------------
// PREVIEW
@Preview(name = "AppBar")
@Composable
fun AppBarPreview(){
    Surface {
        AppBar(isRoot = true, title = "The open list name", showInfoScreen = {}, showSettingsScreen = {},onBack = {}, onEdit = {}, onCreate = {}, showOnCreate = true)
    }
}
@Preview(name = "AppBarDark")
@Composable
fun AppBarDarkPreview(){
    ListTheme(darkTheme = true) {
        Surface {
            AppBar(isRoot = false, title = "The open list name", showInfoScreen = {}, showSettingsScreen = {}, onBack = {}, onEdit = {}, onCreate = {}, showOnCreate = true)
        }
    }
}


