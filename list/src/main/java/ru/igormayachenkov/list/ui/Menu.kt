package ru.igormayachenkov.list.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
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
            // Sort Order NameAsc
            DropdownMenuItem(
                onClick = { setSortOrder(SortOrder.NameAsc); hideMenu() },
                enabled = settings.sortOrder != SortOrder.NameAsc
            ) {
                //Icon(Icons.Default.Check,"")
                //Spacer(modifier = Modifier.width(8.dp))
                Text("Sort by name A-Z")
            }
            // Sort Order NameDesc
            DropdownMenuItem(
                onClick = { setSortOrder(SortOrder.NameDesc); hideMenu() },
                enabled = settings.sortOrder != SortOrder.NameDesc
            ) {
                //Icon(Icons.Default.Check,"")
                //Spacer(modifier = Modifier.width(8.dp))
                Text("Sort by name Z-A")
            }

            // OPEN LIST ACTIONS
            if (!isRoot) {
                Divider()
                // Edit Open List
                DropdownMenuItem(onClick = { editOpenList(); hideMenu() }) {
                    Icon(Icons.Default.Edit, "")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit list")
                }
                // Delete open list
//                DropdownMenuItem(onClick = { hideMenu() }) {
//                    Icon(Icons.Default.Delete, "")
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Delete list")
//                }
            }

            // DIALOGS
            Divider()
            // - Data Screen
            DropdownMenuItem(onClick = { showInfoScreen(); hideMenu() }) {
                Icon(Icons.Default.Info, "")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Data")
            }
            // - Settings Screen
            DropdownMenuItem(onClick = { showSettingsScreen(); hideMenu() }) {
                Icon(Icons.Default.Settings, "")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Settings")
            }
        }
    }
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