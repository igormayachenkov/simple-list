package ru.igormayachenkov.list

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.igormayachenkov.list.data.*

private const val TAG = "myapp.ListViewModel"

class ListViewModel : ViewModel() {

    private val listRepository:ListRepository = App.instance.listRepository

    val pageStack = ArrayList<PageStackData>()

    // LOADED PAGE
    var openList:DataItem by mutableStateOf(listRepository.loadListById(0))
        private set
    var lazyListState : LazyListState = LazyListState() // must have an initial value as openList
        private set

    val itemsState = listRepository.itemsState.map {
        if(it is ItemsState.Success) it.copy(items= it.items.sortedWith(comparator))
        else it
    }.stateIn(
        scope = viewModelScope,
        SharingStarted.WhileSubscribed(),
        initialValue = ItemsState.Loading
    )
    var loadItemsJob:Job?=null


    init {
        Log.d(TAG,"init")
        // TODO restore pageStack from memory here
        loadPage(PageStackData(0, LazyListState()))
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
            // save existed page
            pageStack.add(PageStackData(openList.id, lazyListState))
            // open new page
            loadPage(PageStackData(item.id, LazyListState()), item)
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
            listRepository.updateItem(newItem, justItemState = true)
//            // Update UI (It is the list item)
//            onItemUpdated(newItem)
        }catch (e: Exception){
            Log.e(TAG, e.stackTraceToString())
        }
    }

    // Do it all sync or all async
    // because of the list scroll position must be restored on filled list
    private fun loadPage(page:PageStackData, newList:DataItem?=null){
        Log.d(TAG,"loadPage #${page.id} scroll:${page.lazyListState.firstVisibleItemIndex}")
        // Load the list object
        val list = newList ?: listRepository.loadListById(page.id)
        // Change the open list
        openList = list
        // RELOAD ITEMS
        loadItemsJob?.cancel()
        loadItemsJob = viewModelScope.launch {
            listRepository.loadItems(listId = list.id)
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
        if(pageStack.isNotEmpty()) {
            // Pop the page stack
            val page = pageStack.removeAt(pageStack.lastIndex)
            // Load page
            loadPage(page)
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

    // TODO split open list and item modifications

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
                        // TODO if list inserted - open it
                    }else{
                        // Wrong case: delete new item
                    }
                } else {
                    if (newItem != null) {
                        // UPDATE
                        listRepository.updateItem(newItem, justItemState = false)
                        // Update UI
                        //if (newItem.id.compareTo(openList.id) == 0) {
                        if (oldItem===openList) {
                            // It is the open list
                            onOpenListUpdated(newItem)
                        } else {
                            // It is the list item
                            //onItemUpdated(newItem)
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
                            //onItemDeleted(oldItem)
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
    // OPEN LIST
    private fun onOpenListUpdated(newItem: DataItem){
        openList = newItem
        // TODO do something if list => item change
    }
    private fun onOpenListDeleted(){
        onBackButtonClick()
    }

}