package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.igormayachenkov.list.ListViewModel
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.MainScreen"

@Composable
fun MainScreen(viewModel: ListViewModel) {

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
    ) {  innerPadding ->
        Surface(Modifier.padding(innerPadding), color=MaterialTheme.colors.background) {
            // ITEMS LIST
            ListView(
                theItems = theItems,
                lazyListState = viewModel.lazyListState,
                onItemClick = viewModel::onListRowClick,
                onItemCheck = viewModel::checkItem
                // IMPORTANT: USE STATIC CALLBACKS
                // onCheck = { viewModel.checkItem(item) } - CAUSES ALL LIST REDRAWING
            )
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
