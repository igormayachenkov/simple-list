package ru.igormayachenkov.list

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.ListViewModel"

class ListViewModel(
    private val settingsRepository: SettingsRepository,
    private val listRepository:ListRepository,
    private val itemsRepository:ItemsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // LOADED PAGE
    val openList = listRepository.openList
    val isRoot:Boolean
        get() = listRepository.isRoot

    val itemsState = itemsRepository.itemsState.map {
        if(it is ItemsState.Success) it.copy(items= it.items.sortedWith(comparator))
        else it
    }.stateIn(
        scope = viewModelScope,
        SharingStarted.WhileSubscribed(),
        initialValue = ItemsState.Loading
    )

    var loadItemsJob:Job?=null

    private fun onListChanged(openList:OpenList){
        Log.d(TAG,"onListChanged ${openList.list.logString}")
        // RELOAD ITEMS
        loadItemsJob?.cancel()
        loadItemsJob = viewModelScope.launch {
            itemsRepository.loadItems(listId = openList.list.id)
        }
    }

    init {
        Log.d(TAG,"init")
        //loadPage(PageStackData(0, LazyListState()))
        viewModelScope.launch {
            listRepository.openList.collect{
                onListChanged(it)
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // SORTING
    private val comparator : Comparator<DataItem> = compareBy<DataItem>{ it.name }

    //----------------------------------------------------------------------------------------------
    // EVENTS
    fun onListRowClick(item:DataItem){
        Log.d(TAG,"onListRowClick #${item.id}")
        if(item.type.hasChildren){
            // List
            listRepository.goForward(item)
        }else{
            // Item
            editListItem(item)
        }
    }

    fun checkItem(item:DataItem) {
        Log.d(TAG, "checkItem #${item.id} ${item.name}")
        try {
            // UPDATE
            val newItem = item.copy(
                state = item.state.copy(
                    isChecked = !item.state.isChecked))
            itemsRepository.updateItem(newItem, justItemState = true)
//            // Update UI (It is the list item)
//            onItemUpdated(newItem)
        }catch (e: Exception){
            Log.e(TAG, e.stackTraceToString())
        }
    }

    fun onBackButtonClick():Boolean{
        Log.d(TAG,"onBackButtonClick")
        // CLOSE Settings
        if(showSettingsEditor){
            onSettingsEditorCancel()
            return true
        }
        // CLOSE EDITOR
        if(editorData!=null){
            onEditorCancel()
            return true
        }
        // BACK ON LIST BACKSTACK
        if(listRepository.goBack()) {
            return true
        }
        return false
    }

    //----------------------------------------------------------------------------------------------
    // EDITOR
    var editorData:EditorData? by mutableStateOf(null)
        private set

    fun editListHeader(){
        editorData = EditorData(false, openList.value.list)
    }
    fun createItem(){
        editorData = EditorData(
            isNew = true,
            item = DataItem(
                id = itemsRepository.generateItemId(),
                parent_id = openList.value.list.id,
                type = DataItem.Type(hasChildren = false, isCheckable = true),
                state = DataItem.State(isChecked = false),
                name = "",
                description = null
            )
        )
    }

    private fun editListItem(item:DataItem){
        editorData = EditorData(false,item)
    }

    fun onEditorCancel() {
        editorData=null
    }

    fun onEditorSave(newItem:DataItem?):String?{
        Log.d(TAG,"onEditorSave $newItem")
        editorData?.let { initialData ->
            val isNew   = initialData.isNew
            val oldItem = initialData.item
            try {
                if (isNew) {
                    if(newItem!=null) {
                        // INSERT
                        if (oldItem===openList.value.list) {
                            // It is the open list
                            listRepository.insertAndOpenList(newItem)
                        } else {
                            // It is the list item
                            itemsRepository.insertItem(newItem)
                        }
                    }else{
                        // Wrong case: delete new item
                    }
                } else {
                    if (newItem != null) {
                        // UPDATE
                        //if (newItem.id.compareTo(openList.id) == 0) {
                        if (oldItem===openList.value.list) {
                            // It is the open list
                            //onOpenListUpdated(newItem)
                            listRepository.updateOpenList(newItem)
                        } else {
                            // It is the list item
                            itemsRepository.updateItem(newItem, justItemState = false)
                        }
                    }else{
                        // DELETE
                        val item = initialData.item
                        if (oldItem===openList.value.list) {
                            // It is the open list
                            //onOpenListDeleted()
                            listRepository.deleteAndCloseList(item)
                        } else {
                            // It is the list item
                            itemsRepository.deleteItem(item)
                        }
                    }
                }
            } catch (e: Exception) {
                // return error
                return e.message
            }
            // Close editor
            onEditorCancel()
        }
        // Return no-error
        return null
    }

    //----------------------------------------------------------------------------------------------
    // SETTINGS
    val settings = settingsRepository.settings
    var showSettingsEditor by mutableStateOf(false)
        private set

    fun onSettingsEditorShow(){
        showSettingsEditor = true
    }

    fun onSettingsEditorCancel() {
        showSettingsEditor=false
    }

    fun onSettingsEditorSave(newSettings:Settings):String?{
        viewModelScope.launch {
            settingsRepository.setSettings(newSettings)
        }
        showSettingsEditor=false
        return null
    }

    //----------------------------------------------------------------------------------------------
    // Define ViewModel factory in a companion object
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val app = this[APPLICATION_KEY] as App
                ListViewModel(
                    settingsRepository = app.settingsRepository,
                    listRepository     = app.listRepository,
                    itemsRepository    = app.itemsRepository,
                    savedStateHandle   = savedStateHandle
                )
            }
        }
    }

}