package ru.igormayachenkov.list

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


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
            delay(2000)
            _state.emit(State.Success("Data saved successfully"))
            // Refresh other parts
            App.infoRepository.refresh()
        }
    }

}