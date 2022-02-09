package ru.igormayachenkov.list

import android.content.Context
import androidx.lifecycle.MutableLiveData
import android.util.Log
import ru.igormayachenkov.list.Prefs.Companion.OPEN_ITEM_ID
import ru.igormayachenkov.list.Prefs.Companion.OPEN_LIST_ID
import ru.igormayachenkov.list.data.*
import ru.igormayachenkov.list.data.List
import kotlin.Exception

object Logic {
    const val TAG = "myapp.Logic"

    const val ACTIVITY = "ACTIVITY"

    val pref : Prefs = Prefs("data")

    //----------------------------------------------------------------------------------------------
    // LIST OF LIST
    val listOfLists = SortedLists()

    //----------------------------------------------------------------------------------------------
    // OPEN LIST
    var openList    = MutableLiveData<OpenList?>()

    fun setOpenList(list:List){
        // !!! DO NOT Clear open item
        Log.d(TAG, "setOpenList #${list.id}")
        // Save id
        pref.saveLong(OPEN_LIST_ID, list.id)
        // Update live data
        openList.value = OpenList(list, Database.loadListItems(list.id))
    }

    fun clearOpenList(){
        Log.d(TAG, "clearOpenList")
        // Save id
        pref.remove(OPEN_LIST_ID)
        // Update live data
        openList.value = null
    }

    fun createList(name:String?){
        if (name.isNullOrEmpty()) throw Exception(App.instance()!!.getString(R.string.dialog_error))

        // Create a new list object
        val list = List(
                System.currentTimeMillis(),
                name,
                null
        )
        // Save
        Database.insertList(list)
        listOfLists.insert(list)

        // Open the list immediately
        setOpenList(list)
    }

    fun renameOpenList(name:String?){
        val list = openList.value
        if(list==null) throw Exception("open list is null")
        if (name.isNullOrEmpty()) throw Exception(App.instance()!!.getString(R.string.dialog_error))

        // Rename List
        Database.updateListName(list.id, name)
        list.name = name
        listOfLists.updateSortOrder() //TODO listOfLists.update
        FList.publicInterface?.onListRenamed()
    }

    fun deleteOpenList(){
        val openlist = openList.value
        if(openlist==null) throw Exception("open list is null")

        Database.deleteList(openlist.id)
        clearOpenList()
        listOfLists.getPositionById(openlist.id)?.let { pos->
            listOfLists.removeAt(pos)
        }
    }



    //----------------------------------------------------------------------------------------------
    // OPEN ITEM
    var openItem = MutableLiveData<OpenItem?>()

    fun setOpenItem(openitem:OpenItem) {
        Log.d(TAG, "setOpenItem #${openitem.item.id} pos${openitem.pos}")
        // Save id
        pref.saveLong(OPEN_ITEM_ID, openitem.item.id)
        // Update live data
        openItem.value = openitem
    }
    fun saveOpenItemChanges(changes:ItemChanges){
        openItem.value?.let {
            it.changes = changes
            ItemChanges.save(changes)
            Log.d(ItemChanges.TAG, "*** save $changes")
        }
    }
    fun clearOpenItem() {
        Log.d(TAG, "clearOpenItem")
        // Clear id
        pref.remove(OPEN_ITEM_ID)
        ItemChanges.clear()
        // Update live data
        openItem.value = null
    }

    fun createItem(){
        Log.d(TAG, "createItem")
        openList.value?.id?.let { list_id->
            val item = Item.create(list_id)
            setOpenItem(OpenItem(item,null))
        }?:run{ throw Exception("createItem when openList is NULL") }
    }

    fun toggleItemState(item:Item, pos: Int){
        item.toggleState()
        Database.updateItemState(item)
        openList.value?.items?.update(item, pos)
    }

    fun updateOpenItem(input:ItemChanges){
        val openitem = openItem.value
        val openlist = openList.value
        if(openitem==null) throw Exception("updateOpenItem: open item is null")
        if(openlist==null) throw Exception("updateOpenItem: open list is null")
        val item = openitem.item
        val pos  = openitem.pos

        // Check changes
        val changes = HashSet<String>()
        if( Utils.areNotEqual(input.name,        item.name))        changes.add("name")
        if( Utils.areNotEqual(input.description, item.description)) changes.add("descr")
        if(input.isChecked!=item.isChecked)                         changes.add("status")

        if(changes.isNotEmpty()) {
            // Update item fields
            item.name        = input.name
            item.description = input.description
            item.isChecked   = input.isChecked
            if(item.description.isNullOrEmpty()) item.description = null

            if (pos!=null) {
                // EXISTED ITEM
                Database.updateItem(item)
                openlist.items.update(item, pos)
            } else {
                // NEW ITEM
                Database.insertItem(item)
                openlist.items.insert(item)
            }
        }

        // Clear open item
        clearOpenItem()
    }

    fun deleteOpenItem(){
        val openitem = openItem.value
        val openlist = openList.value
        if(openitem==null) throw Exception("deleteOpenItem: open item is null")
        if(openlist==null) throw Exception("deleteOpenItem: open list is null")

        if(openitem.pos!=null) {
            // EXISTED
            // Update storage
            Database.deleteItem(openitem.item.id)
            openlist.items.removeAt(openitem.pos)
        }//else{
            // NEW : Do nothing. Just clear open item
        //}
        // Clear open item
        clearOpenItem()// updates UI too (hides fItem)
    }

    fun deleteALL(){
        Database.deleteALL()
        listOfLists.clear() // reload?
        clearOpenList()
    }

    //----------------------------------------------------------------------------------------------
    // INIT (RESTORE)
    init{
        Log.d(TAG, "init")

        Database.open(App.context)
        //Data.load(App.context)
        listOfLists.load(Database.loadListOfLists())

        // Restore open list/item
        val openListId = pref.loadLong(OPEN_LIST_ID)
        val openItemId = pref.loadLong(OPEN_ITEM_ID)
        Log.d(TAG,"Restore openListId:$openListId   openItemId:$openItemId")

        // Set open list
        openListId?.let { id->
            listOfLists.getElementById(id)?.let {
                setOpenList(it)
            }
        }
        // Set open item
        openItemId?.let { id->
            openList.value?.let { openlist ->
                val pos = openlist.findPositionById(id)
                val openitem =
                        if (pos != null)
                            OpenItem(openlist.items.asList.get(pos), pos) // Existed
                        else
                            OpenItem(Item.create(openlist.id), null) // New

                // Load saved changes - ONLY HERE ON APP START!!!
                openitem.changes = ItemChanges.load()
                Log.d(TAG, "*** restored open item changes ${openitem.changes}")

                // Set and open window
                setOpenItem(openitem)
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // FILE EXPORT/IMPORT





//    fun loadFromJSON(json: JSONObject){
//        Data.loadFromJSON(json)
//        AMain.instance?.onDataUpdated()
//    }



}