package ru.igormayachenkov.list

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import ru.igormayachenkov.list.data.DataItem
import java.io.FileOutputStream


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

    fun saveAll(uri: Uri){
        Log.d(TAG,"saveAll $uri")
        CoroutineScope(Dispatchers.IO).launch {
            _state.emit(State.Busy("Saving all data..."))
            try {
                // Fill data
                val json = JSONObject()
                    .put("root", itemToJson(app.listRepository.fakeRootList))
                val bytes = json.toString().toByteArray()
                Log.d(TAG,"JSON length: ${bytes.size}")

                // Write to the file
                val pfd = app.contentResolver.openFileDescriptor(uri,"w")
                with( FileOutputStream(pfd!!.fileDescriptor) ) {
                    write(bytes)
                    close()
                }
                pfd.close()

                delay(1000)

                // Success state
                _state.emit(State.Success("Data saved successfully. ${bytes.size} bytes written"))
            }catch (e:Exception){
                _state.emit(State.Error(e.toString()))
            }

            // Refresh other parts
            app.infoRepository.refresh()
        }
    }
    private fun itemToJson(item:DataItem):JSONObject {
        val json = item.toJSON()
        if(item.type.hasChildren){
            json.put("items", childrenToJSON(item.id))
        }
        return json
    }
    private fun childrenToJSON(parentId:Long):JSONArray{
        val json = JSONArray()
        Database.loadListItems(parentId).forEach { item->
            json.put(itemToJson(item))
        }
        return json
    }
}
