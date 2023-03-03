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
            TYPE_ITEM -> {}
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

}