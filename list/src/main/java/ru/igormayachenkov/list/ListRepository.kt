package ru.igormayachenkov.list

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.igormayachenkov.list.data.DataList
import ru.igormayachenkov.list.data.Element

private const val TAG = "myapp.ListRepository"

class ListRepository() {
    fun open(context:Context){
        Database.open(context)
    }

    fun loadListById(listId: Long): DataList {
        if(listId.compareTo(0)==0) return DataList(0,"all lists", null)
        return Database.loadList(listId) ?: throw Exception("list not found by id=$listId")
    }

    suspend fun loadListItems(listId:Long):List<Element>{
        Log.d(TAG, "loadListItems started $listId")
        var items:List<Element>
        withContext(Dispatchers.IO) {
            items = Database.loadListItems(listId).toList()
        }
        Log.d(TAG, "loadListItems finished ${items.toString()}")
        return items
    }

}