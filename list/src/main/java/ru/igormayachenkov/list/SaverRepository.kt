package ru.igormayachenkov.list

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.DataFile
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.FileReader


private const val TAG = "myapp.SaverRepository"
private const val VERSION_2_0 = 20000

// SAVE/LOAD DATA TO/FROM JSON FILE

class SaverRepository {
    sealed interface State{
        object Ready : State
        data class Busy   (val message:String) : State
        data class Error  (val message:String) : State
        data class ConfirmLoad(val dataFile: DataFile) : State
        data class Success(val message:String) : State
    }

    private val _state = MutableStateFlow<State>(State.Ready)
    val state = _state.asStateFlow()

    //----------------------------------------------------------------------------------------------
    // ACTIONS
    fun reset(){
        if(state.value is State.Busy) return // do not interrupt!
        _state.value = State.Ready
    }

    fun deleteAll(){
        Log.d(TAG,"deleteAll")
        CoroutineScope(Dispatchers.IO).launch {
            _state.emit(State.Busy("Erasing data..."))
            try {
                Database.deleteALL()

                delay(1000)

                // Success state
                _state.emit(State.Success("Data erased successfully."))
            } catch (e: Exception) {
                _state.emit(State.Error(e.toString()))
            }

            // Refresh other parts
            app.infoRepository.refresh()
            app.listRepository.goRootAndRefresh()
        }
    }

    class SaveResult(var nItems:Int=0)
    fun saveAll(uri: Uri){
        Log.d(TAG,"saveAll $uri")
        CoroutineScope(Dispatchers.IO).launch {
            _state.emit(State.Busy("Saving data..."))
            try {
                // Fill data
                val json = JSONObject()
                app.version?.let{
                    json.put("versionCode", it.code)
                    json.put("versionName", it.name)
                }
                val saveResult=SaveResult()
                json.put("root", itemToJson(app.listRepository.fakeRootList, result = saveResult))
                val text = json.toString()
                Log.d(TAG,"JSON length: ${text.length}")

                // Write to the file
                val nBytes = writeFile(uri, text)

                delay(1000)

                // Success state
                _state.emit(State.Success("Data saved successfully.\n${saveResult.nItems} elements saved\n$nBytes bytes written"))
            }catch (e:Exception){
                _state.emit(State.Error(e.toString()))
            }

            // Refresh other parts
            app.infoRepository.refresh()
        }
    }
    fun loadAll(uri: Uri) {
        Log.d(TAG, "loadAll $uri")
        CoroutineScope(Dispatchers.IO).launch {
            _state.emit(State.Busy("Reading file data..."))
            try {
                // Read file
                val text = readFile(uri)
                Log.d(TAG,"${text.length} bytes read")
                // Parse to JSON
                val json = JSONObject(text)
                Log.d(TAG,"parsed to JSON")

                // PROCESS JSON
                val versionCode = json.getInt("versionCode")
                val versionName = json.getString("versionName")
                // Data depending on the version
                val items = ArrayList<DataItem>()
                if(versionCode < VERSION_2_0) {
                    // Version 1 format
                    val lists = json.getJSONArray("lists")
                    for(i in 0 until lists.length()){
                        jsonToListV1(lists.getJSONObject(i), result = items)
                    }
                }else{
                    // Version 2 format
                    val root = json.getJSONObject("root")
                    jsonToItem(root, parentId = 0, result = items)
                }

                delay(1000)

                // Confirm state
                _state.emit(State.ConfirmLoad(DataFile(versionName, text.length, items)))
            } catch (e: Exception) {
                _state.emit(State.Error(e.toString()))
            }
        }
    }
    fun loadAllFinish(dataFile: DataFile){
        Log.d(TAG, "loadAllFinish")
        CoroutineScope(Dispatchers.IO).launch {
            _state.emit(State.Busy("Restoring data..."))
            try {
                // Empty database
                Database.deleteALL()
                // Write to the database
                for(item in dataFile.items){
                    Database.insertItem(item, log=false)
                }

                delay(1000)
                // Success state
                _state.emit(State.Success("Data restored successfully.\n${dataFile.items.size} elements added"))
            } catch (e: Exception) {
                _state.emit(State.Error(e.toString()))
            }

            // Refresh other parts
            app.infoRepository.refresh()
            app.listRepository.goRootAndRefresh()
        }
    }

    //----------------------------------------------------------------------------------------------
    // JSON UTILS
    private fun itemToJson(item:DataItem, result:SaveResult):JSONObject {
        val json = item.toJSON()
        if(!item.isRoot) // skip root
            result.nItems++
        if(item.type.hasChildren){
            // Children to json
            val array = JSONArray()
            val items = Database.loadListItems(item.id)
            for(child in items)
                array.put( itemToJson(child,result) ) // recurrent call
            json.put("items", array)
        }
        return json
    }
    private fun jsonToItem(json:JSONObject, parentId: Long, result:ArrayList<DataItem>):DataItem{
        // Parse the item body
        val item = DataItem(json,parentId)
        //Log.d(TAG,"- jsonToItem: ${item.name}")
        if(!item.isRoot) // skip root
            result.add(item)
        // Parse children
        if(item.type.hasChildren){
            val array = json.getJSONArray("items")
            for(i in 0 until array.length()){
                jsonToItem(json=array.getJSONObject(i), parentId=item.id, result) // recurrent call
            }
        }
        return item
    }
    private fun jsonToListV1(json:JSONObject, result:ArrayList<DataItem>) {
        // Parse the list body
        val list = DataItem(
            id = json.getLong("id"),
            parent_id = 0L,
            type = DataItem.Type(hasChildren = true, isCheckable = false),
            state = DataItem.State(isChecked = false),
            name = json.getString("name"),
            description = null
        )
        result.add(list)
        // Parse children as list items
        val array = json.getJSONArray("items")
        for(i in 0 until array.length()) {
            @Suppress("NAME_SHADOWING") val json = array.getJSONObject(i)
            result.add(DataItem(
                id = json.getLong("id"),
                parent_id = list.id,
                type = DataItem.Type(hasChildren = false, isCheckable = true),
                state = DataItem.State(isChecked = json.getInt("state")!=0),
                name = json.getString("name"),
                description = UtilsJSON.getStringOrNull(json,"description")
            ))
        }
    }

    //----------------------------------------------------------------------------------------------
    // FILE IO FUNCTIONS
    private fun writeFile(uri:Uri, text:String):Int{
        val pfd = app.contentResolver.openFileDescriptor(uri,"w")
        val bytes = text.toByteArray()
        with( FileOutputStream(pfd!!.fileDescriptor) ) {
            write(bytes)
            close()
        }
        pfd.close()
        return bytes.size
    }
    private fun readFile(uri:Uri):String{
        val pfd = app.contentResolver.openFileDescriptor(uri, "r")
        val reader = BufferedReader(FileReader(pfd!!.fileDescriptor))
        // Read
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        reader.close()
        pfd.close()
        return sb.toString()
    }
}
