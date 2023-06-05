package ru.igormayachenkov.list

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import ru.igormayachenkov.list.data.DataItem
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.FileReader


private const val TAG = "myapp.SaverRepository"

class SaverRepository {
    sealed interface State{
        object Ready : State
        data class Busy   (val message:String) : State
        data class Error  (val message:String) : State
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
            _state.emit(State.Busy("Restoring data..."))
            try {
                // Read file
                val text = readFile(uri)
                Log.d(TAG,"${text.length} bytes read")
                // Parse to JSON
                val json = JSONObject(text)
                Log.d(TAG,"parsed to JSON")

                // Process the data: JSON => items
                val root = json.getJSONObject("root")
                val itemList = ArrayList<DataItem>()
                jsonToItem(root, parentId = 0, result = itemList)

                // Write to the database
                //...

                delay(1000)
                // Success state
                _state.emit(State.Success("Data restored successfully.\n${text.length} bytes read\n${itemList.size} elements added"))
            } catch (e: Exception) {
                _state.emit(State.Error(e.toString()))
            }

            // Refresh other parts
            app.infoRepository.refresh()
        }
    }

    //----------------------------------------------------------------------------------------------
    // JSON UTILS
    private fun itemToJson(item:DataItem, result:SaveResult):JSONObject {
        val json = item.toJSON()
        result.nItems++
        if(item.type.hasChildren){
            // Children to json
            val array = JSONArray()
            Database.loadListItems(item.id).forEach { child->
                array.put(itemToJson(child,result)) // recurrent call
            }
            json.put("items", array)
        }
        return json
    }
    private fun jsonToItem(json:JSONObject, parentId: Long, result:ArrayList<DataItem>):DataItem{
        // Parse the item body
        val item = DataItem(json,parentId)
        //Log.d(TAG,"- jsonToItem: ${item.name}")
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
