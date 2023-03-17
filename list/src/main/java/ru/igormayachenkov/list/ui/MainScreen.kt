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
//    val theItems:List<DataItem> = viewModel.openListItems
    val itemsState by viewModel.itemsState.collectAsState()
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
            when(itemsState){
                ItemsState.Loading -> {
                    Text(text = "Loading...", style = MaterialTheme.typography.h4)
                }
                is ItemsState.Error -> {
                    Text(text = "Error: ${(itemsState as ItemsState.Error).message} ", style = MaterialTheme.typography.h4)
                }
                is ItemsState.Success ->{
                    // ITEMS LIST
                    ListView(
                        theItems = (itemsState as ItemsState.Success).items,
                        lazyListState = viewModel.lazyListState,
                        onItemClick = viewModel::onListRowClick,
                        onItemCheck = viewModel::checkItem
                        // IMPORTANT: USE STATIC CALLBACKS
                        // onCheck = { viewModel.checkItem(item) } - CAUSES ALL LIST REDRAWING
                    )
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
