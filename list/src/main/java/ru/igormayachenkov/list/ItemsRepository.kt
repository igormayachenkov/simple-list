package ru.igormayachenkov.list

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.ItemsState

private const val TAG = "myapp.ItemsRepository"

// ITEMS FOR THE CURRENT OPEN LIST

class ItemsRepository() {

    private val _itemsState = MutableStateFlow<ItemsState>(ItemsState.Loading)
    val itemsState = _itemsState.asStateFlow()

    //----------------------------------------------------------------------------------------------
    // LOADERS
    suspend fun loadItems(listId:Long){
        withContext(Dispatchers.IO){
            try {
                // Start
                Log.d(TAG, "openList started $listId")
                _itemsState.emit(ItemsState.Loading)
                // Progress
                //delay(1000)
                val items = Database.loadListItems(listId)
                // Success
                _itemsState.emit(ItemsState.Success(items))
            }catch(ex:Exception){
                // Error
                _itemsState.emit(ItemsState.Error(ex.stackTraceToString()))
            }
            Log.d(TAG, "openList finished")
        }
    }

    //----------------------------------------------------------------------------------------------
    // MODIFIERS
    fun insertItem(item:DataItem){
        Log.d(TAG, "insertItem  ${item.logString} ")
        val items = (itemsState.value as ItemsState.Success).items // exception in wrong state
        // Modify database
        Database.insertItem(item)
        // Emit new state
        _itemsState.value = ItemsState.Success(items+item )
    }
    fun updateItem(item:DataItem, justItemState:Boolean){
        Log.d(TAG, "updateItem  ${item.logString}")
        val items = (itemsState.value as ItemsState.Success).items // exception in wrong state
        // Modify database
        if(justItemState)
            Database.updateItemState(item)
        else
            Database.updateItem(item)
        // Emit new state
        _itemsState.value = ItemsState.Success(items.map { if(it.id==item.id) item else it })
    }
    fun deleteItem(item:DataItem){
        Log.d(TAG, "deleteItem  ${item.logString}")
        val items = (itemsState.value as ItemsState.Success).items // exception in wrong state
        // Cascade children deletion
        deleteChildren(item)
        // Delete the item record
        Database.deleteItem(item.id)
        // Emit new state
        _itemsState.value = ItemsState.Success(items.filter { it.id!=item.id })
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