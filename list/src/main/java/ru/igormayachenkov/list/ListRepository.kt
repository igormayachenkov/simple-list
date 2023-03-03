package ru.igormayachenkov.list

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.igormayachenkov.list.data.Element

private const val TAG = "myapp.ListRepository"

class ListRepository() {
    fun open(context:Context){
        Database.open(context)
    }

    suspend fun loadListItems(listId:Long):List<Element>{
        Log.d(TAG, "loadListItems started $listId")
        var items:List<Element>
        withContext(Dispatchers.IO) {
            items = if (listId.compareTo(0) == 0) {
                Database.loadListOfLists().toList()
            }else {
                Database.loadListItems(listId).toList()
            }
        }
        Log.d(TAG, "loadListItems finished ${items.toString()}")
        return items
    }
}