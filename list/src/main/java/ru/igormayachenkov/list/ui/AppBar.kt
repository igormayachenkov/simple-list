package ru.igormayachenkov.list.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.data.Settings
import ru.igormayachenkov.list.ui.theme.ListTheme

@Composable
fun AppBar(
    isRoot:Boolean,
    title:String,
    onBack:()->Unit,
    onEdit:()->Unit,
    onCreate:()->Unit,
    settings:Settings,
    menu: @Composable ()->Unit
){

    Row(
        Modifier
            .background(color = MaterialTheme.colors.primary)
            .defaultMinSize(minHeight = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button (Navigation icon)
        if (isRoot)
            Spacer(modifier = Modifier.width(8.dp))
        else
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack,"")
            }


        // Title
        Text(text = title, color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .weight(1F)
                .clickable(onClick = { if (!isRoot) onEdit() }))

        // Add (Create) Button
        if(settings.useAdd) IconButton(onClick = onCreate) {
            Icon(Icons.Default.AddCircle,contentDescription = "add",
                tint = MaterialTheme.colors.onPrimary)
        }

        // Menu Button + menu
        menu()

    }
}

//--------------------------------------------------------------------------------------------------
// PREVIEW

@Preview(name = "AppBar")
@Composable
fun AppBarPreview(){
    Surface {
        AppBar(isRoot = true, title = "The open list name",
            onBack = {}, onEdit = {}, onCreate = {}, menu = { MenuIconPreview() },
            settings = Settings(useAdd = true))
    }
}
@Preview(name = "AppBarDark")
@Composable
fun AppBarDarkPreview(){
    ListTheme(darkTheme = true) {
        Surface {
            AppBar(isRoot = false, title = "The open list name",
                onBack = {}, onEdit = {}, onCreate = {}, menu = {MenuIconPreview()},
                settings = Settings(useAdd = true))
        }
    }
}
