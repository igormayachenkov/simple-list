package ru.igormayachenkov.list.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.igormayachenkov.list.App
import ru.igormayachenkov.list.ItemsRepository
import ru.igormayachenkov.list.ListRepository
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.ListViewModel"

class ListViewModel(
    private val listRepository: ListRepository,
    private val itemsRepository: ItemsRepository
) : ViewModel(){

    // LOADED PAGE
    val openList = listRepository.openList
    val isRoot:Boolean
        get() = listRepository.isRoot

    val itemsState = itemsRepository.itemsState

    init {
        Log.d(TAG, "init")
    }

    //----------------------------------------------------------------------------------------------
    // EVENTS
    fun openItem(item: DataItem){
        Log.d(TAG, "openItem #${item.id}")
        if(item.type.hasChildren){
            // List
            listRepository.goForward(item)
        }else{
            // Item
            editListItem(item)
        }
    }

    fun checkItem(item: DataItem) {
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

    val isBackHandlerEnabled:Boolean get() {
        // need to CLOSE EDITOR
        if(editorData!=null) return true
        // need to BACK ON LIST BACKSTACK
        if(!isRoot) return true
        return false
    }
    fun onBackButtonClick(){
        Log.d(TAG, "onBackButtonClick")
        // CLOSE EDITOR
        if(editorData!=null){
            onEditorCancel()
            return
        }
        // BACK ON LIST BACKSTACK
        listRepository.goBack()
    }

    //----------------------------------------------------------------------------------------------
    // EDITOR
    var editorData: EditorData? by mutableStateOf(null)
        private set

    fun editOpenList(){
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

    private fun editListItem(item: DataItem){
        editorData = EditorData(false, item)
    }

    fun onEditorCancel() {
        editorData=null
    }

    fun onEditorSave(newItem: DataItem?):String?{
        Log.d(TAG, "onEditorSave $newItem")
        editorData?.let { initialData ->
            val isNew   = initialData.isNew
            val oldItem = initialData.item
            try {
                newItem?.name?.ifEmpty { throw Exception("Name can't be empty") }
                if (isNew) {
                    if(newItem!=null) {
                        // INSERT
                        if (newItem.type.hasChildren) {
                            // It is a list
                            listRepository.insertAndOpenList(newItem)
                        } else {
                            // It is an item
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
    // SHARING TODO move sorting items here
    fun shareOpenList(context: Context){
        viewModelScope.launch {
            val items = if(itemsState.value is ItemsState.Success)
                (itemsState.value as ItemsState.Success).items else null

            StringBuilder().apply {
                // list title
                append(openList.value.list.toSharedText(items?.size))
                append("\n-------------------------------")
                // list items
                items?.forEach{
                    append("\n")
                    append(it.toSharedText())
                    append("\n")
                }
                // SHARE
                context.shareText(toString())
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    // FACTORY
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                Log.d(TAG,"Factory")
                //val savedStateHandle = createSavedStateHandle()
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                ListViewModel(
                    listRepository = app.listRepository,
                    itemsRepository = app.itemsRepository,
                  //  savedStateHandle = savedStateHandle
                )
            }
        }
    }
}