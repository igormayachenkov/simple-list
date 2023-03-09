package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.igormayachenkov.list.ListViewModel
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.ListView"

@Composable
fun ListView() {
    val viewModel: ListViewModel = viewModel()
    
    val openList = viewModel.openList
    val theItems:List<DataItem> = viewModel.openListItems
    val editingData = viewModel.editorData

    Log.d(TAG,"=>")
    Scaffold(
        topBar = {
            Header(viewModel = viewModel)
        }
    ) { innerPadding ->


        Column() {
            // ITEMS LIST
            LazyColumn {
                items(theItems, { it.id }) { item ->
                    Box(
                        Modifier
                            .clickable(onClick = { viewModel.onListRowClick(item) })
                    ) {
                        if (item.type.hasChildren)
                            ListRow(item = item)
                        else
                            ItemRow(item = item)
                    }
                }
            }
        }

        // EDITOR DIALOG
        editingData?.let {
            Editor(
                initialData = it,
                onClose = viewModel::onEditorCancel,
                onSave = viewModel::onEditorSave
            )
        }
    }
}

@Composable
fun Header(viewModel: ListViewModel){
    TopAppBar(
        backgroundColor = Color.Blue,
        navigationIcon = {
            // Back button
            if (viewModel.backStack.isNotEmpty()) {
                IconButton(onClick = viewModel::onBackButtonClick) {
                    Icon(Icons.Default.ArrowBack,"")
                }
            }
        },
        title = {
            Text(viewModel.openList.name)
        },
        actions = {
            // Create Button
            IconButton(onClick = viewModel::createItem ) {
                Icon(Icons.Default.Add,"")
            }
        }
    )
}


@Composable
fun ListRow(item:DataItem){
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.List, "list icon")
        Text(text = item.name, style = MaterialTheme.typography.h5)
        if (item.type.isCheckable)
            Text(if (item.state.isChecked) "+" else "-")
    }
}

@Composable
fun ItemRow(item:DataItem){
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.name, style = MaterialTheme.typography.h5,
                modifier = Modifier.weight(1f))
            if (item.type.isCheckable)
                Text(if (item.state.isChecked) "+" else "-")
        }
        item.description?.let { Text(text = it) }
    }
}
//--------------------------------------------------------------------------------------------
// PREVIEW
fun fakeList():DataItem=DataItem(
    13,
    0,
    DataItem.Type(true, false),
    DataItem.State(false),
    "The List",
    "This is the list description text"
)
fun fakeItem():DataItem=DataItem(
    13,
    0,
    DataItem.Type(false, true),
    DataItem.State(true),
    "The Item",
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque faucibus, sapien eget tristique commodo, magna nibh rutrum mauris, et tempor neque libero et lectus. Etiam vitae maximus diam, eu pulvinar nibh. Fusce sodales at nibh id accumsan."
)

@Preview(name = "ListRow")
@Composable
fun ListRowPreview(){
    Surface {
        ListRow(item = fakeList())
    }
}

@Preview(name = "ItemRow")
@Composable
fun ItemRowPreview(){
    Surface {
        ItemRow(item = fakeItem())
    }
}
