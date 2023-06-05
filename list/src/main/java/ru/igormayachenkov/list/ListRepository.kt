package ru.igormayachenkov.list

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.flow.*
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.OpenList
import ru.igormayachenkov.list.data.SavedOpenList
import java.util.*

private const val TAG = "myapp.ListRepository"

// CURRENT OPEN LIST

class ListRepository(
    private val stackDataSource: StackDataSource
) {
    //----------------------------------------------------------------------------------------------
    // OPEN LIST STACK
    val fakeRootList = DataItem(
        id=0, parent_id = 0,
        DataItem.Type(true,false),
        DataItem.State(false),
        name="Simple List", description = null
    )

    private val stack:Stack<OpenList> = restoreStack()

    val isRoot:Boolean
        get() = stack.size==1

    suspend fun saveStack(){
        stackDataSource.saveStack(
            stack.toList().map { SavedOpenList(it.list.id, it.lazyListState.firstVisibleItemIndex) }
        )
    }
    private fun restoreStack():Stack<OpenList> = Stack<OpenList>().apply {
        stackDataSource.restoreStack().forEach {
            Log.d(TAG,"restored stack item $it")
            push(OpenList(
                if(it.id.compareTo(0)==0) fakeRootList else Database.loadItem(it.id)!!,
                LazyListState(it.firstVisibleItemIndex)
            ))
        }
        // Push fake root if not restored
        if(isEmpty())
            push(OpenList(fakeRootList))
    }

    //----------------------------------------------------------------------------------------------
    // OPEN LIST as FLOW
    private val _openList : MutableStateFlow<OpenList> = MutableStateFlow<OpenList>(
        stack.peek()
    )
    val openList : StateFlow<OpenList> = _openList.asStateFlow()

    //----------------------------------------------------------------------------------------------
    // OPEN / CLOSE
    fun goForward(list:DataItem){
        Log.d(TAG, "goForward  ${list.logString} ")
        stack.push(OpenList(list))
        //saveStack()
        _openList.value = stack.peek()
    }
    fun goBack():Boolean{
        if(isRoot) return false
        stack.pop()
        //saveStack()
        _openList.value = stack.peek()
        return true
    }
    fun goRootAndRefresh(){
        Log.d(TAG, "goRootAndRefresh")
        stack.empty()
        stack.push(OpenList(fakeRootList)) // create new object to refresh
        _openList.value = stack.peek()
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
        goForward(list)
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