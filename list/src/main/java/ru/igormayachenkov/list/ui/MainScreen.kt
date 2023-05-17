package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ru.igormayachenkov.list.ListViewModel
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.MainScreen"

@Composable
fun MainScreen(viewModel: ListViewModel) {

    val settings   by viewModel.settings.collectAsState()
    val openList   by viewModel.openList.collectAsState()
    val itemsState by viewModel.itemsState.collectAsState()
    val editingData = viewModel.editorData

    Log.d(TAG,"=> ${openList.list.logString}") // DO NOT print lazyListState here! it causes rerendering

    Scaffold(
        topBar = { AppBar(
            isRoot   = viewModel.isRoot,
            title    = openList.list.name,
            onSettings   = viewModel::onSettingsEditorShow,
            onBack   = viewModel::onBackButtonClick,
            onEdit   = viewModel::editListHeader,
            onCreate = viewModel::createItem,
            showOnCreate = !settings.useFab
        )},
        floatingActionButton = {
            if(settings.useFab){
                IconButton(
                    modifier = Modifier.background(color =MaterialTheme.colors.secondary, shape = CircleShape ),
                    onClick = viewModel::createItem
                ) {
                    Icon(
                        Icons.Default.AddCircle, "",
                        tint = MaterialTheme.colors.onSecondary
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
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
                        settings = settings,
                        theItems = (itemsState as ItemsState.Success).items,
                        lazyListState = openList.lazyListState,
                        onItemClick = viewModel::onListRowClick,
                        onItemCheck = viewModel::checkItem
                        // IMPORTANT: USE STATIC CALLBACKS
                        // onCheck = { viewModel.checkItem(item) } - CAUSES ALL LIST REDRAWING
                    )
                }
            }
        }
    }

//    // EDITOR DIALOG
//    editingData?.let {
//        Editor(
//            initialData = it,
//            onClose = viewModel::onEditorCancel,
//            onSave = viewModel::onEditorSave
//        )
//    }
}
