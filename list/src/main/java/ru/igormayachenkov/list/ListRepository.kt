package ru.igormayachenkov.list

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.TYPE_LIST

private const val TAG = "myapp.ListRepository"

class ListRepository() {
    fun open(context:Context){
        Database.open(context)
    }

    fun loadListById(id: Long): DataItem {
        if(id.compareTo(0)==0) return DataItem(0, 0, TYPE_LIST,0,"all lists", null)
        return Database.loadItem(id) ?: throw Exception("list not found by id=$id")
    }

    suspend fun loadListItems(listId:Long):List<DataItem>{
        Log.d(TAG, "loadListItems started $listId")
        var items:List<DataItem>
        withContext(Dispatchers.IO) {
            items = Database.loadListItems(listId)
        }
        Log.d(TAG, "loadListItems finished ${items.toString()}")
        return items
    }

    fun updateItem(item:DataItem){
        Log.d(TAG, "updateItem  $item")
        Database.updateItem(item)
    }

}