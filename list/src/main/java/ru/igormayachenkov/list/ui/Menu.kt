package ru.igormayachenkov.list.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.app
import ru.igormayachenkov.list.data.Settings
import ru.igormayachenkov.list.data.SortOrder

@Composable
fun Menu(
    isRoot:Boolean,
    settings:Settings,
    showInfoScreen:()->Unit,
    showSettingsScreen:()->Unit,
    editOpenList:()->Unit,
    setSortOrder:(SortOrder)->Unit,
){
    var showMenu by remember { mutableStateOf(false) }
    fun hideMenu(){showMenu=false}

    IconButton(onClick = { showMenu=true }) {
        Icon(
            Icons.Default.MoreVert, contentDescription = "menu",
            tint = MaterialTheme.colors.onPrimary
        )
        // Menu
        DropdownMenu(
            expanded = showMenu,
            offset = DpOffset.Zero,
            onDismissRequest = ::hideMenu
        ) {

            // SORTING
            Text(text = "sorting:",
                Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
            // Sort Order NameAsc
            val enabledAsc = settings.sortOrder != SortOrder.NameAsc
            DropdownMenuItem(
                onClick = { hideMenu(); setSortOrder(SortOrder.NameAsc) },
                enabled = enabledAsc
            ) {
                Text("by name A -> Z")
                if(!enabledAsc) CheckedIcon()
            }
            // Sort Order NameDesc
            val enabledDesc = settings.sortOrder != SortOrder.NameDesc
            DropdownMenuItem(
                onClick = { hideMenu(); setSortOrder(SortOrder.NameDesc) },
                enabled = enabledDesc
            ) {
                Text("by name Z -> A")
                if(!enabledDesc) CheckedIcon()
            }
            // Keep Lists on the Top
            DropdownMenuItem(
                onClick = {  hideMenu(); app.settingsRepository.toggleSortListsUp() }
            ) {
                Text("lists above")
                if(settings.sortListsUp) CheckedIcon()
            }
            // Keep Checked on the bottom
            DropdownMenuItem(
                onClick = { hideMenu(); app.settingsRepository.toggleSortCheckedDown() }
            ) {
                Text("checked below")
                if(settings.sortCheckedDown) CheckedIcon()
            }

            // OPEN LIST ACTIONS
            if (!isRoot) {
                MenuDivider()
                // Edit Open List
                DropdownMenuItem(onClick = { hideMenu();  editOpenList() }) {
                    Icon(Icons.Default.Edit, "")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit the list")
                }
                // Delete open list
//                DropdownMenuItem(onClick = { hideMenu() }) {
//                    Icon(Icons.Default.Delete, "")
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Delete list")
//                }
            }

            // DIALOGS
            MenuDivider()
            // - Data Screen
            DropdownMenuItem(onClick = { hideMenu(); showInfoScreen() }) {
                Icon(Icons.Default.Info, "")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Data")
            }
            // - Settings Screen
            DropdownMenuItem(onClick = { hideMenu(); showSettingsScreen()}) {
                Icon(Icons.Default.Settings, "")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Settings")
            }
        }
    }
}
@Composable
private fun MenuDivider(){
    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),)
}
@Composable
private fun CheckedIcon(){
    Spacer(modifier = Modifier.width(8.dp))
    Icon(Icons.Default.Check, "")
}

@Composable
fun MenuIconPreview(){
    IconButton(onClick = {}) {
        Icon(
            Icons.Default.MoreVert, contentDescription = "menu",
            tint = MaterialTheme.colors.onPrimary
        )
    }
}