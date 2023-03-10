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
    // SORTING
    private val comparator : Comparator<DataItem> = compareBy<DataItem>{ it.name }
    private fun sortOpenListItems(){
        Log.d(TAG,"sortItems")
        //openListItems.sortBy { it.name }
        openListItems.sortWith(comparator)
    }
    private fun itemIndexToInsert(item:DataItem):Int{
        val index = openListItems.binarySearch (element=item, comparator=comparator)
        return if(index<0) -(index + 1)
        else index
    }

    //----------------------------------------------------------------------------------------------
    // EVENTS
    fun onListRowClick(item:DataItem){
        Log.d(TAG,"onListRowClick #${item.id}")
        if(item.type.hasChildren){
            // List
            backStack.add(openList.id)
            changeOpenList(item)
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
            listRepository.updateItemState(newItem)
            // Update UI (It is the list item)
            onItemUpdated(newItem)
        }catch (e: Exception){
            Log.e(TAG, e.stackTraceToString())
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
            sortOpenListItems()
        }
    }

    fun onBackButtonClick():Boolean{
        Log.d(TAG,"onBackButtonClick")
        // CLOSE EDITOR
        if(editorData!=null){
            onEditorCancel()
            return true
        }
        // BACK ON BACKSTACK
        if(backStack.isNotEmpty()) {
            // Pop backStack
            val id = backStack.removeAt(backStack.lastIndex)
            // Load the list object
            val list = listRepository.loadListById(id)
            // Change the open list
            changeOpenList(list)
            return true
        }
        return false
    }

    //----------------------------------------------------------------------------------------------
    // USE EDITOR
    var editorData:EditorData? by mutableStateOf(null)
        private set

    fun editListHeader(){
        editorData = EditorData(false, openList)
    }
    fun createItem(){
        editorData = EditorData(
            isNew = true,
            item = DataItem(
                id = listRepository.generateItemId(),
                parent_id = openList.id,
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
                        listRepository.insertItem(newItem)
                        // Update UI
                        onItemInserted(newItem)
                    }else{
                        // Wrong case: delete new item
                    }
                } else {
                    if (newItem != null) {
                        // UPDATE
                        listRepository.updateItem(newItem)
                        // Update UI
                        //if (newItem.id.compareTo(openList.id) == 0) {
                        if (oldItem===openList) {
                            // It is the open list
                            onOpenListUpdated(newItem)
                        } else {
                            // It is the list item
                            onItemUpdated(newItem)
                        }
                    }else{
                        // DELETE
                        val item = initialData.item
                        listRepository.deleteItem(item)
                        // Update UI
                        if (oldItem===openList) {
                            // It is the open list
                            onOpenListDeleted()
                        } else {
                            // It is the list item
                            onItemDeleted(oldItem)
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
    // UI UPDATERS
    // LIST ITEM
    private fun onItemInserted(newItem: DataItem){
        openListItems.add(itemIndexToInsert(newItem), newItem)
    }
    private fun onItemUpdated(newItem: DataItem){
        // Find the source item index
        val index = openListItems.indexOfFirst { it.id.compareTo(newItem.id)==0 }
        if(index<0) throw Exception("item not found by id=${newItem.id}")
        // Update UI
        openListItems.removeAt(index)
        val newIndex = itemIndexToInsert(newItem)
        Log.d(TAG,"onItemUpdated index: $index => $newIndex")
        openListItems.add(newIndex,newItem)
        //openListItems[index] = newItem
        //sortOpenListItems()
    }
    private fun onItemDeleted(item:DataItem){
        // Find the source item index
        val index = openListItems.indexOfFirst { it.id.compareTo(item.id)==0 }
        if(index<0) throw Exception("item not found by id=${item.id}")
        // Update UI
        openListItems.removeAt(index)
    }
    // OPEN LIST
    private fun onOpenListUpdated(newItem: DataItem){
        openList = newItem
        // TODO do something if list => item change
    }
    private fun onOpenListDeleted(){
        onBackButtonClick()
    }

}