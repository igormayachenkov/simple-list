package ru.igormayachenkov.list.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun AppBar(
    isRoot:Boolean,
    title:String,
    onBack:()->Unit,
    onEdit:()->Unit,
    onCreate:()->Unit,
    showOnCreate:Boolean
){
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        navigationIcon = {
            // Back button
            if (isRoot) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Menu, "")
                }
            }else{
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack,"")
                }
            }
        },
        title = {
            // Title
            Text(text = title, color = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onEdit))
        },
        actions = {
            // Create Button
            if(showOnCreate) IconButton(onClick = onCreate) {
                Icon(Icons.Default.AddCircle,"",
                    tint = MaterialTheme.colors.onPrimary)
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
        AppBar(isRoot = true, title = "The open list name", onBack = {}, onEdit = {}, onCreate = {}, showOnCreate = true)
    }
}
@Preview(name = "AppBarDark")
@Composable
fun AppBarDarkPreview(){
    ListTheme(darkTheme = true) {
        Surface {
            AppBar(isRoot = false, title = "The open list name", onBack = {}, onEdit = {}, onCreate = {}, showOnCreate = true)
        }
    }
}


