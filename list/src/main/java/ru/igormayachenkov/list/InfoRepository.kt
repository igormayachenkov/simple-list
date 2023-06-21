package ru.igormayachenkov.list

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.igormayachenkov.list.data.DataInfo

private const val TAG = "myapp.InfoRepository"

class InfoRepository {
    sealed interface State {
        object Ready   : State
        object Busy    : State
        data class Success(val info:DataInfo) : State
        data class Error(val message:String) : State
    }
    private val _state = MutableStateFlow<State>(State.Ready)
    val state = _state.asStateFlow()

    //----------------------------------------------------------------------------------------------
    // ACTIONS
    private var job: Job?=null
    fun reset(){
        job?.cancel()
        job = null
        _state.value = State.Ready
    }
    fun refresh(){
        if(state.value === State.Ready) return
        if(state.value === State.Busy) return
        calculate()
    }

    fun calculate(){
        job = CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "calculate")
            _state.emit(State.Busy)
            val timer = ActionTimer()
            try {
                val info = Database.queryInfo()
                timer.pauseIfNeed()
                _state.emit(State.Success(info))
            } catch (e: Exception) {
                _state.emit(State.Error(e.toString()))
            }
        }
    }
}