package ru.igormayachenkov.list

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import android.util.Log
import ru.igormayachenkov.list.data.*
import ru.igormayachenkov.list.data.List
import kotlin.Exception

object Logic {
    const val TAG = "myapp.Logic"

    const val ACTIVITY = "ACTIVITY"


    // PREFERENCES
    lateinit var pref : SharedPreferences
    const val OPEN_LIST_ID = "open_list_id"
    const val OPEN_ITEM_ID = "open_item_id"
    fun saveLong(value:Long?, key:String){
        with (pref.edit()) {
            value?.let {
                putLong(key, it)
            }?: kotlin.run {
                remove(key)
            }
            apply()
        }
    }

    //----------------------------------------------------------------------------------------------
    // LIST OF LIST
    val listOfLists = SortedLists()

    //----------------------------------------------------------------------------------------------
    // OPEN LIST
    var openList    = MutableLiveData<OpenList?>()

    fun setOpenList(list:List){
        Log.d(TAG, "setOpenList #${list.id}")
        // Save id
        saveLong(list.id, OPEN_LIST_ID)
        // Clear open item
        clearOpenItem()
        // Update live data
        openList.value = OpenList(list, Database.loadListItems(list.id))
    }

    fun clearOpenList(){
        Log.d(TAG, "clearOpenList")
        // Save id
        saveLong(null, OPEN_LIST_ID)
        // Clear open item
        clearOpenItem()
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
        Log.d(TAG, "setOpenItem #${openitem.pos} pos${openitem.item.id}")
        // Save id
        saveLong(openitem.item.id, OPEN_ITEM_ID)
        // Update live data
        openItem.value = openitem
    }
    fun clearOpenItem() {
        Log.d(TAG, "clearOpenItem")
        // Clear id
        saveLong(null, OPEN_ITEM_ID)
        // Update live data
        openItem.value = null
    }

    fun createItem(){
        Log.d(TAG, "createItem")
        openList.value?.id?.let { list_id->
            val item = Item.create(list_id,null,null)
            setOpenItem(OpenItem(item,null))
        }?:run{ throw Exception("createItem when openList is NULL") }
    }

    fun toggleItemState(item:Item, pos: Int){
        item.toggleState()
        Database.updateItemState(item)
        openList.value?.items?.update(item, pos)
    }

    fun updateOpenItem(name:String?, descr:String?, isChecked:Boolean){
        val openitem = openItem.value
        val openlist = openList.value
        if(openitem==null) throw Exception("updateOpenItem: open item is null")
        if(openlist==null) throw Exception("updateOpenItem: open list is null")
        val item = openitem.item
        val pos  = openitem.pos

        // Check changes
        val changes = HashSet<String>()
        if( Utils.areNotEqual(name, item.name))         changes.add("name")
        if( Utils.areNotEqual(descr, item.description)) changes.add("descr")
        if(isChecked!=item.isChecked)                   changes.add("status")

        if(changes.isNotEmpty()) {
            // Update item fields
            item.name = name
            item.description = descr
            item.isChecked = isChecked

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
        }else{
            // NEW
            // Do nothing. Just clear open item
        }
        // Clear open item
        clearOpenItem()// updates UI too (hides fItem)
    }

    fun deleteALL(){
        Database.deleteALL()
        listOfLists.clear() // reload?
        clearOpenList()
        AMain.publicInterface?.notifyDataSetChanged()
    }

    //----------------------------------------------------------------------------------------------
    // INIT (RESTORE)
    init{
        Log.d(TAG, "init")

        pref = App.context.getSharedPreferences("data",Context.MODE_PRIVATE)

        Database.open(App.context)
        //Data.load(App.context)
        listOfLists.load(Database.loadListOfLists())

        // Restore open list/item
        val openListId = if(pref.contains(OPEN_LIST_ID)) pref.getLong(OPEN_LIST_ID, 0) else null
        val openItemId = if(pref.contains(OPEN_ITEM_ID)) pref.getLong(OPEN_ITEM_ID, 0) else null
        Log.d(TAG,"Restore openListId:$openListId   openItemId:$openItemId")

        // Set open list
        openListId?.let { id->
            listOfLists.getElementById(id)?.let {
                setOpenList(it)
            }
        }
        // Set open item
        openItemId?.let { id->
            openList.value?.openItemById(id)?.let {
                setOpenItem(it)
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