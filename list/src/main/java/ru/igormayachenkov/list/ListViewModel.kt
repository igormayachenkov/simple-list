package ru.igormayachenkov.list

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.DataList
import ru.igormayachenkov.list.data.Element

private const val TAG = "myapp.ListViewModel"

class ListViewModel : ViewModel() {
    var openList by mutableStateOf(DataList(0,"all lists", null))
        private set

    val openListItems = mutableStateListOf<Element>(
        DataItem(11,0,1,"First",null),
        DataItem(12,0,0,"Second",null)
    )

    init {
        Log.d(TAG,"init")
        val listOfLists = Database.loadListOfLists()
        Log.d(TAG,listOfLists.toString())
        listOfLists.forEach { list->
            openListItems.add(list)
        }

    }
}