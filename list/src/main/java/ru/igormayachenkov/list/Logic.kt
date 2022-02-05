package ru.igormayachenkov.list

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import android.net.Uri
import android.util.Log
import android.widget.Toast
import org.json.JSONObject
import ru.igormayachenkov.list.data.Data
import ru.igormayachenkov.list.data.Database
import ru.igormayachenkov.list.data.List
import ru.igormayachenkov.list.data.Item
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.FileReader
import kotlin.Exception

object Logic {
    const val TAG = "myapp.Logic"

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
    // OPEN LIST
    var openList    : List? = null
        get() = field
        set(value) {
            field = value
            saveLong(value?.id, OPEN_LIST_ID)
        }

    fun openList(list:List, activity:Activity){
        // Update data
        openList   = list

        // Update UI
        AList.show(activity)
    }

    fun createList(name:String?):List{
        if (name.isNullOrEmpty()) throw Exception(App.instance()!!.getString(R.string.dialog_error))

        // Create a new list object
        val list = List(
                System.currentTimeMillis(),
                name,
                null
        )
        // Save
        Database.insertList(list)
        Data.listOfLists.addList(list)
        AMain.instance?.onListInserted()

        return list
    }

    fun renameOpenList(name:String?){
        val list = openList
        if(list==null) throw Exception("open list is null")
        if (name.isNullOrEmpty()) throw Exception(App.instance()!!.getString(R.string.dialog_error))

        // Rename List
        Database.updateListName(list.id, name)
        list.name = name
        AList.instance?.onListRenamed()
        AMain.instance?.onListRenamed(list.id)
    }

    fun deleteOpenList(){
        val list = openList
        if(list==null) throw Exception("open list is null")

        Database.deleteList(list.id)
        Data.listOfLists.deleteList(list.id)
        openList=null
        setOpenItem(null)
        AMain.instance?.onListDeleted(list.id)
        AList.instance?.close()
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
        return openList?.items?.get(openItemId) != null
    }

    fun createItem(){
        Log.d(TAG, "createItem")
        openList?.id?.let { list_id->
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
                AList.instance?.onItemUpdated(isNameChanged, isDescrChanged)
            } else {
                // NEW ITEM
                Database.insertItem(item)
                openList?.addItem(item)
                // Update UI
                AList.instance?.onItemInserted()
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
            val list = Data.listOfLists.getList(item.parent_id)
            if(list==null) throw Exception("list #${item.parent_id} not found")
            // Update storage
            Database.deleteItem(item.id)
            list.deleteItem(item.id)
            // Clear open item
            setOpenItem(null)// updates UI too (hides fItem)
            // Update UI
            AList.instance?.onItemDeleted(item.id)
        }else{
            // NEW
            // Just clear open item
            setOpenItem(null)// updates UI too (hides fItem)
        }
    }

    fun deleteALL(){
        Database.deleteALL()
        Data.listOfLists.reload()
        openList = null
        setOpenItem(null)
        AMain.instance?.onDataUpdated()
    }

    //----------------------------------------------------------------------------------------------
    // INIT
    init{
        Log.d(TAG, "init")

        pref = App.context.getSharedPreferences("data",Context.MODE_PRIVATE)

        Database.open(App.context)
        Data.load(App.context)

        // Restore open list/item
        val openListId = if(pref.contains(OPEN_LIST_ID)) pref.getLong(OPEN_LIST_ID, 0) else null
        val openItemId = if(pref.contains(OPEN_ITEM_ID)) pref.getLong(OPEN_ITEM_ID, 0) else null
        Log.d(TAG,"Restore openListId:$openListId   openItemId:$openItemId")
        // Get list
        openListId?.let {
            openList = Data.listOfLists.getList(it)
        }
        // Get item
        openItemId?.let {
            setOpenItem(openList?.items?.get(it))
        }

    }

    //----------------------------------------------------------------------------------------------
    // FILE EXPORT/IMPORT
    fun saveLists(uri: Uri?, lists: Collection<List>):Int{
        // Open
        val pfd = App.instance()!!.contentResolver.openFileDescriptor(uri!!, "w")
        val fileOutputStream = FileOutputStream(pfd!!.fileDescriptor)

        // Write
        val bytes = Data.toJSON(lists).toString().toByteArray()
        fileOutputStream.write(bytes)

        // Close. Let the document provider know you're done by closing the stream.
        fileOutputStream.close()
        pfd.close()

        return bytes.size
    }

    fun saveListToXML(list:List, uri: Uri?):Int{
        // Open
        val pfd = App.instance()!!.contentResolver.openFileDescriptor(uri!!, "w")
        val fileOutputStream = FileOutputStream(pfd!!.fileDescriptor)

        // Write
        val bytes = list.toXML().toByteArray()
        fileOutputStream.write(bytes)

        // Close. Let the document provider know you're done by closing the stream.
        fileOutputStream.close()
        pfd.close()

        return bytes.size
    }

    fun readJSON(uri: Uri?):JSONObject{
        // Open
        val pfd = App.context.contentResolver.openFileDescriptor(uri!!, "r")
        val reader = BufferedReader(FileReader(pfd!!.fileDescriptor))

        // Read
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        val text = sb.toString()

        // Close. Let the document provider know you're done by closing the stream.
        reader.close()
        pfd.close()

        // Parce data
        return JSONObject(text)
    }

    fun loadFromJSON(json: JSONObject){
        Data.loadFromJSON(json)
        AMain.instance?.onDataUpdated()
    }

}