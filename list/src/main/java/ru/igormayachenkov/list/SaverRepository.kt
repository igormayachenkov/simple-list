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
import ru.igormayachenkov.list.data.Version
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.FileReader


private const val TAG = "myapp.SaverRepository"
private val VERSION_2_0 = Version(2,0,"0")

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
            _state.emit(State.Busy(app.getString(R.string.saver_erasing)))
            val timer = ActionTimer()
            try {
                Database.deleteALL()
                timer.pauseIfNeed(1000)
                // Success state
                _state.emit(State.Success(app.getString(R.string.saver_erased)))
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
            _state.emit(State.Busy(app.getString(R.string.saver_writing_file)))
            val timer = ActionTimer()
            try {
                // Fill data
                val json = JSONObject()
                json.put("version", app.version.toString())
                val saveResult=SaveResult()
                json.put("root", itemToJson(app.listRepository.fakeRootList, result = saveResult))
                val text = json.toString()
                Log.d(TAG,"JSON length: ${text.length}")

                // Write to the file
                val nBytes = writeFile(uri, text)

                timer.pauseIfNeed(1000)

                // Success state
                _state.emit(State.Success(app.getString(R.string.saver_written)+"\n"+
                        saveResult.nItems+" "+app.getString(R.string.saver_written_total)+"\n"+
                        nBytes+" "+app.getString(R.string.saver_written_bytes)))
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
            _state.emit(State.Busy(app.getString(R.string.saver_reading_file)))
            val timer = ActionTimer()
            try {
                // Read file
                val text = readFile(uri)
                Log.d(TAG,"${text.length} bytes read")
                // Parse to JSON
                val json = JSONObject(text)
                Log.d(TAG,"parsed to JSON")

                // PROCESS JSON
                val version =
                    if(json.has("version"))
                        Version.fromString(json.getString("version"))
                else if(json.has("versionName"))
                        Version.fromString(json.getString("versionName"))
                else throw Exception("version not found")
                // Data depending on the version
                val items = ArrayList<DataItem>()
                if(version isBelow VERSION_2_0) {
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

                timer.pauseIfNeed(1000)

                // Confirm state
                _state.emit(State.ConfirmLoad(DataFile(version.toString(), text.length, items)))
            } catch (e: Exception) {
                _state.emit(State.Error(e.message.toString()))
            }
        }
    }
    fun loadAllFinish(dataFile: DataFile){
        Log.d(TAG, "loadAllFinish")
        CoroutineScope(Dispatchers.IO).launch {
            _state.emit(State.Busy(app.getString(R.string.saver_writing_database)))
            val timer = ActionTimer()
            try {
                // Empty database
                Database.deleteALL()
                // Write to the database
                for(item in dataFile.items){
                    Database.insertItem(item, log=false)
                }

                timer.pauseIfNeed(1000)
                // Success state
                _state.emit(State.Success(
                    app.getString(R.string.saver_restored)+"\n"+
                    dataFile.items.size+" "+
                    app.getString(R.string.saver_restored_total)))
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
