package ru.igormayachenkov.list

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.ListViewModel"

class ListViewModel : ViewModel() {

    private val listRepository:ListRepository = App.instance.listRepository

    var openList:DataItem by mutableStateOf(listRepository.loadListById(0))
        private set

    val openListItems = mutableStateListOf<DataItem>()

    val backStack = ArrayList<Long>()

    init {
        Log.d(TAG,"init")
        reloadOpenListItems()
    }

    //----------------------------------------------------------------------------------------------
    // EVENTS
    fun onListRowClick(item:DataItem){
        Log.d(TAG,"onListRowClick #${item.id}")
        when(item.type){
            TYPE_LIST -> {
                backStack.add(openList.id)
                changeOpenList(item)
            }
            TYPE_ITEM -> {editListItem(item)}
        }
    }
    private fun changeOpenList(list:DataItem){
        Log.d(TAG,"changeOpenList #${list.id}")
        openList = list
        reloadOpenListItems()
    }
    private fun reloadOpenListItems(){
        val id = openList.id
        Log.d(TAG,"reloadOpenListItems #$id")
        // Clear existed
        openListItems.clear()
        // Start loading process
        viewModelScope.launch {
            val items = listRepository.loadListItems(id)
            openListItems.addAll(items)
        }
    }

    fun onBackButtonClick(){
        Log.d(TAG,"onBackButtonClick")
        if(backStack.isEmpty()) return
        // Pop backStack
        val id = backStack.removeAt(backStack.lastIndex)
        // Load the list object
        val list = listRepository.loadListById(id)
        // Change the open list
        changeOpenList(list)
    }

    //----------------------------------------------------------------------------------------------
    // USE EDITOR
    var editingData:EditorData? by mutableStateOf(null)
        private set

    fun editListHeader(){
        editingData = EditorData(false, openList)
    }
    fun createItem(){
        editingData = EditorData(
            isNew = true,
            item = DataItem(
                id = listRepository.generateItemId(),
                parent_id = openList.id,
                type = TYPE_ITEM,
                state = 0,
                name = "",
                description = null
            )
        )
    }

    private fun editListItem(item:DataItem){
        editingData = EditorData(false,item)
    }

    fun onEditorCancel() {
        editingData=null
    }

    fun onEditorSave(updatedData:EditorData):String?{
        Log.d(TAG,"onSave $updatedData")
        try {
            if (updatedData.isNew) {
                // INSERT
                insertItem(updatedData)
            } else {
                // UPDATE
                if (updatedData.item.id.compareTo(openList.id) == 0)
                    updateOpenList(updatedData)
                else
                    updateItem(updatedData)
            }
        }catch(e:Exception){
            // return error
            return e.message
        }
        // Close editor
        onEditorCancel()
        // Return no-error
        return null
    }

    private fun insertItem(editorData: EditorData){
        // New data object
        val newItem = editorData.item
        // Save in the storage
        listRepository.insertItem(newItem)
        // Update UI
        openListItems.add(newItem)
    }

    private fun updateOpenList(editorData: EditorData){
        Log.d(TAG,"updateOpenList $editorData")
        // Update the data object
        val newList = editorData.item
        // Save in the storage
        listRepository.updateItem(newList)
        // Update UI
        openList = newList
    }
    private fun updateItem(editorData: EditorData){
        Log.d(TAG,"updateItem $editorData")
        // Find the source item
        val item = openListItems.find { it.id.compareTo(editorData.item.id)==0 }
        if(item==null) throw Exception("item not found by id=${editorData.item.id}")
        // New data object
        val newItem = editorData.item
        // Save in the storage
        listRepository.updateItem(newItem)
        // Update UI
        openListItems[openListItems.indexOf(item)] = newItem
    }
}