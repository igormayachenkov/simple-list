package ru.igormayachenkov.list

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import android.net.Uri
import android.util.Log
import ru.igormayachenkov.list.data.Database
import ru.igormayachenkov.list.data.List
import ru.igormayachenkov.list.data.Item
import ru.igormayachenkov.list.data.OpenList
import java.io.BufferedReader
import java.util.HashMap
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
    val listOfLists = HashMap<Long, List>()

    //----------------------------------------------------------------------------------------------
    // OPEN LIST
    var openList    = MutableLiveData<OpenList?>()

    fun setOpenList(list:List?){
        Log.d(TAG, "setOpenList #${list?.id}")
        // Save id
        saveLong(list?.id, OPEN_LIST_ID)
        // Clear open item
        setOpenItem(null)
        // Update live data
        openList.value =
            if(list!=null)
                OpenList(list, Database.loadListItems(list.id))
            else
                null
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
        listOfLists.put(list.id, list)
        AMain.instance?.onListInserted()

        // Open the list
        setOpenList(list)
    }

    fun renameOpenList(name:String?){
        val list = openList.value
        if(list==null) throw Exception("open list is null")
        if (name.isNullOrEmpty()) throw Exception(App.instance()!!.getString(R.string.dialog_error))

        // Rename List
        Database.updateListName(list.id, name)
        list.name = name
        FList.publicInterface?.onListRenamed()
        AMain.instance?.onListRenamed(list.id)
    }

    fun deleteOpenList(){
        val list = openList.value
        if(list==null) throw Exception("open list is null")

        Database.deleteList(list.id)
        listOfLists.remove(list.id)
        setOpenList(null)
        AMain.instance?.onListDeleted(list.id)
    }



    //----------------------------------------------------------------------------------------------
    // OPEN ITEM
    var openItem = MutableLiveData<Item?>()

    fun setOpenItem(item:Item?) {
        Log.d(TAG, "setOpenItem #${item?.id}")
        // Save id
        saveLong(item?.id, OPEN_ITEM_ID)
        // Update live data
        openItem.value = item
    }

    private fun isOpenItemExisted(openItemId:Long):Boolean {
        return openList.value?.itemById(openItemId) != null
    }

    fun createItem(){
        Log.d(TAG, "createItem")
        openList.value?.id?.let { list_id->
            val item = Item.create(list_id,null,null)
            setOpenItem(item)
        }?:run{ throw Exception("createItem when openList is NULL") }
    }

    fun updateOpenItem(name:String?, descr:String?){
        val item = openItem.value
        if(item==null) throw Exception("deleteOpenItem: open item is null")

        // Check changes
        val isNameChanged:Boolean = Utils.areNotEqual(name, item.name)
        val isDescrChanged:Boolean = Utils.areNotEqual(descr, item.description)

        if(isNameChanged || isDescrChanged) {
            // Update item fields
            item.name = name
            item.description = descr

            if (isOpenItemExisted(item.id)) {
                // EXISTED ITEM
                Database.updateItemName(item.id, name, descr)
                // Update UI
                FList.publicInterface?.onItemUpdated(isNameChanged, isDescrChanged)
            } else {
                // NEW ITEM
                Database.insertItem(item)
                openList.value?.insertItem(item)
                // Update UI
                FList.publicInterface?.onItemInserted()
            }
        }

        // Clear open item
        setOpenItem(null)
    }

    fun deleteOpenItem(){
        val item = openItem.value
        if(item==null) throw Exception("deleteOpenItem: open item is null")

        if(isOpenItemExisted(item.id)) {
            // EXISTED
            val list = listOfLists.get(item.parent_id)
            if(list==null) throw Exception("list #${item.parent_id} not found")
            // Update storage
            Database.deleteItem(item.id)
            val pos = openList.value?.deleteItem(item)
            // Clear open item
            setOpenItem(null)// updates UI too (hides fItem)
            // Update UI
            pos?.let {
                FList.publicInterface?.onItemDeleted(it)
            }
        }else{
            // NEW
            // Just clear open item
            setOpenItem(null)// updates UI too (hides fItem)
        }
    }

    fun deleteALL(){
        Database.deleteALL()
        listOfLists.clear() // reload?
        setOpenList(null)
        AMain.instance?.onDataUpdated()
    }

    //----------------------------------------------------------------------------------------------
    // INIT (RESTORE)
    init{
        Log.d(TAG, "init")

        pref = App.context.getSharedPreferences("data",Context.MODE_PRIVATE)

        Database.open(App.context)
        //Data.load(App.context)
        Database.loadListOfLists(listOfLists)

        // Restore open list/item
        val openListId = if(pref.contains(OPEN_LIST_ID)) pref.getLong(OPEN_LIST_ID, 0) else null
        val openItemId = if(pref.contains(OPEN_ITEM_ID)) pref.getLong(OPEN_ITEM_ID, 0) else null
        Log.d(TAG,"Restore openListId:$openListId   openItemId:$openItemId")
        // Get list
        openListId?.let {
            setOpenList(listOfLists.get(it))
        }
        // Get item
        openItemId?.let {
            setOpenItem(openList.value?.itemById(it))
        }

    }

    //----------------------------------------------------------------------------------------------
    // FILE EXPORT/IMPORT





//    fun loadFromJSON(json: JSONObject){
//        Data.loadFromJSON(json)
//        AMain.instance?.onDataUpdated()
//    }



}