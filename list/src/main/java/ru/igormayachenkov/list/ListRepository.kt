package ru.igormayachenkov.list

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.igormayachenkov.list.data.DataItem

private const val TAG = "myapp.ListRepository"

class ListRepository() {

    fun open(context:Context){
        Database.open(context)
    }

    //----------------------------------------------------------------------------------------------
    // LOADERS
    fun loadListById(id: Long): DataItem {
        if(id.compareTo(0)==0)
            return DataItem(0, 0, DataItem.Type(true,false), DataItem.State(true),"all lists", null)

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

    //----------------------------------------------------------------------------------------------
    // MODIFIERS
    fun insertItem(item:DataItem){
        Log.d(TAG, "insertItem  $item")
        Database.insertItem(item)
    }
    fun updateItem(item:DataItem){
        Log.d(TAG, "updateItem  $item")
        Database.updateItem(item)
    }
    fun deleteItem(item:DataItem){
        Log.d(TAG, "deleteItem  $item")
        // Cascade children deletion
        deleteChildren(item)
        // Delete the item record
        Database.deleteItem(item.id)
    }
    private fun deleteChildren(item:DataItem){
        if(item.type.hasChildren){
            val children = Database.loadListItems(item.id)
            // Delete sub-children
            children.forEach { deleteChildren(it) }
            // Delete the children
            Database.deleteChildren(item.id)
        }
    }

    //----------------------------------------------------------------------------------------------
    // UTILS
    fun generateItemId():Long{
        return System.currentTimeMillis()
        // TODO make data-based id generation
    }

}