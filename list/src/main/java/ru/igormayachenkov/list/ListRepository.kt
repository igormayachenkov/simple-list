package ru.igormayachenkov.list

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.OpenList
import java.util.*

private const val TAG = "myapp.ListRepository"

class ListRepository() {

    private val fakeRootList = DataItem(
        id=0, parent_id = 0,
        DataItem.Type(true,false),
        DataItem.State(true),
        name="Simple List", description = null
    )

    private val _openList = MutableStateFlow<OpenList>(
        OpenList(fakeRootList) )
    val openList = _openList.asStateFlow()

    val stack = Stack<OpenList>()



    //----------------------------------------------------------------------------------------------
    // OPEN / CLOSE
    fun setOpenList(list:DataItem){
        Log.d(TAG, "setOpenList  ${list.logString} ")
        stack.push(openList.value)
        _openList.value = OpenList(list)
    }
    fun goBack():Boolean{
        if(stack.isEmpty()) return false
        _openList.value = stack.pop()
        return true
    }
    //----------------------------------------------------------------------------------------------
    // MODIFIERS
    fun updateOpenList(list:DataItem){
        // Modify database
        Database.updateItem(list)
        // Emit new state
        _openList.value = openList.value.copy(list=list)
    }
    fun insertAndOpenList(list:DataItem){
        Log.d(TAG, "insertList  ${list.logString} ")
        // Modify database
        Database.insertItem(list)
        // Open the list (and emit new state)
        setOpenList(list)
    }
    fun deleteAndCloseList(list:DataItem){
        Log.d(TAG, "deleteList  ${list.logString}")
        // Cascade children deletion
        deleteChildren(list)
        // Delete the item record
        Database.deleteItem(list.id)
        // Go back (and emit new state)
        goBack()
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

}