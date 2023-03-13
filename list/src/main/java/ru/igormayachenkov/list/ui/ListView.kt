package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.igormayachenkov.list.ListViewModel
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.ListView"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListView(viewModel: ListViewModel) {

    val openList = viewModel.openList
    val theItems:List<DataItem> = viewModel.openListItems
    val editingData = viewModel.editorData

    Log.d(TAG,"=> ${openList.logString} ") // DO NOT print lazyListState here! it causes rerendering

    Scaffold(
        topBar = { AppBar(
            isRoot   = viewModel.pageStack.isEmpty(),
            title    = openList.name,
            onBack   = viewModel::onBackButtonClick,
            onEdit   = viewModel::editListHeader,
            onCreate = viewModel::createItem,
        )}
    ) { innerPadding ->
        // ITEMS LIST
        LazyColumn(
            state = viewModel.lazyListState,
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(items=theItems, key={ it.id }) { item ->
                ItemView(
                    modifier = Modifier.animateItemPlacement(),
                    item = item,
                    onClick = viewModel::onListRowClick,
                    onCheck = viewModel::checkItem
                    // IMPORTANT: USE STATIC CALLBACKS
                    // onCheck = { viewModel.checkItem(item) } - CAUSES ALL LIST REDRAWING
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
