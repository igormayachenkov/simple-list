package ru.igormayachenkov.list.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.igormayachenkov.list.app
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.MainScreen"

const val FAB_SIZE = 56

@Composable
fun MainScreen(
    viewModel: ListViewModel = viewModel(factory = ListViewModel.Factory),
    settingsViewModel: SettingsViewModel
) {

    val settings   by settingsViewModel.settings.collectAsState()
    val openList   by viewModel.openList.collectAsState()
    val itemsState by viewModel.itemsState.collectAsState()
    val editingData = viewModel.editorData

    Log.d(TAG,"=> ${openList.list.logString}") // DO NOT print lazyListState here! it causes rerendering

    BackHandler(
        enabled = viewModel.isBackHandlerEnabled,
        onBack  = viewModel::onBackButtonClick
    )

    Scaffold(
        topBar = {
            AppBar(
                isRoot              = viewModel.isRoot,
                title               = openList.list.name,
                onBack              = viewModel::onBackButtonClick,
                onEdit              = viewModel::editOpenList,
                onCreate            = viewModel::createItem,
                settings            = settings,
            ) {
                Menu(
                    isRoot              = viewModel.isRoot,
                    settings            = settings,
                    showInfoScreen      = app.infoRepository::calculate ,
                    showSettingsScreen  = settingsViewModel::showSettings,
                    editOpenList        = viewModel::editOpenList,
                    setSortOrder        = app.settingsRepository::setSortOrder,
                )
            }
        },
        floatingActionButton = {
            if(settings.useFab){
                IconButton(
                    onClick = viewModel::createItem,
                    modifier = Modifier
                        .padding(end = (FAB_SIZE*0.7).dp, bottom = (FAB_SIZE*0.2).dp)
                        .background(color = MaterialTheme.colors.secondary, shape = CircleShape)
                        .defaultMinSize(FAB_SIZE.dp, FAB_SIZE.dp)
                ) {
                    Icon(
                        Icons.Default.AddCircle, "",
                        tint = MaterialTheme.colors.onSecondary,
                        modifier = Modifier.defaultMinSize((FAB_SIZE/2).dp, (FAB_SIZE/2).dp)
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
                        onOpenItem  = viewModel::openItem,
                        onCheckItem = viewModel::checkItem
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
            settings = settings,
            onClose = viewModel::onEditorCancel,
            onSave = viewModel::onEditorSave
        )
    }
}
