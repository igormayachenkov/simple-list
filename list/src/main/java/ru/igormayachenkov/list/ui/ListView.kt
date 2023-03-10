package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.ListViewModel
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.ListView"

@Composable
fun ListView(viewModel: ListViewModel) {

    val openList = viewModel.openList
    val theItems:List<DataItem> = viewModel.openListItems
    val editingData = viewModel.editorData

    Log.d(TAG,"=>")
    Scaffold(
        topBar = { AppBar(
            isRoot   = viewModel.backStack.isEmpty(),
            title    = openList.name,
            onBack   = viewModel::onBackButtonClick,
            onEdit   = viewModel::editListHeader,
            onCreate = viewModel::createItem,
        )}
    ) { innerPadding ->
        // ITEMS LIST
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(theItems, { it.id }) { item ->
                ItemView(
                    item = item,
                    onClick = { viewModel.onListRowClick(item) },
                    onCheck = { viewModel.checkItem(item) }
                )
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
