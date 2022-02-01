package ru.igormayachenkov.list

import android.app.Activity
import android.content.Context
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

    init{
        Log.d(TAG, "init")

        Database.open(App.context)
        Data.load(App.context)

        // TODO restore open list/item

    }

    //----------------------------------------------------------------------------------------------
    // OPEN LIST/ITEM
    var openList : List? = null
    var openItem : Item? = null

    fun createList(name:String?):List{
        if (name.isNullOrEmpty()) throw Exception(App.context.getString(R.string.dialog_error))

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
        if (name.isNullOrEmpty()) throw Exception(App.context.getString(R.string.dialog_error))

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
        openItem=null
        AMain.instance?.onListDeleted(list.id)
        AList.instance?.close()
    }

    fun openList(list:List, activity:Activity){
        openList = list
        // TODO save open list id
        AList.show(activity)
    }

    fun openItem(item:Item?, activity:Activity){
        openItem = item // null means new
        // TODO save open item id
        AItem.show(activity)
    }

    fun deleteItem(item: Item?){
        if(item==null) throw Exception("deleteItem: item is null")

        val list = Data.listOfLists.getList(item.parent_id)
        if(list==null) throw Exception("list #${item.parent_id} not found")

        Database.deleteItem(item.id)
        list.deleteItem(item.id)
        AList.instance?.onItemDeleted(item.id)
    }

    fun deleteALL(){
        Database.deleteALL()
        Data.listOfLists.reload()
        openList = null
        openItem = null
        AMain.instance?.onDataUpdated()
    }

    //----------------------------------------------------------------------------------------------
    // FILE EXPORT/IMPORT
    fun saveLists(uri: Uri?, lists: Collection<List>):Int{
        // Open
        val pfd = App.context.contentResolver.openFileDescriptor(uri!!, "w")
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
        val pfd = App.context.contentResolver.openFileDescriptor(uri!!, "w")
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