package ru.igormayachenkov.list

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.DataList
import ru.igormayachenkov.list.data.Element

private const val TAG = "myapp.ListViewModel"

class ListViewModel : ViewModel() {

    private val listRepository:ListRepository = App.instance.listRepository

    var openList:DataList by mutableStateOf(listRepository.loadListById(0))
        private set

    val openListItems = mutableStateListOf<Element>(
        DataItem(11,0,1,"First",null),
        DataItem(12,0,0,"Second",null)
    )

    val backStack = ArrayList<Long>()

    init {
        Log.d(TAG,"init")
        reloadOpenListItems()
    }

    //----------------------------------------------------------------------------------------------
    // EVENTS
    fun onListRowClick(element:Element){
        Log.d(TAG,"onListRowClick #${element.id}")
        when(element){
            is DataList -> {
                backStack.add(openList.id)
                changeOpenList(element)
            }
            is DataItem -> {}
        }
    }
    private fun changeOpenList(list:DataList){
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