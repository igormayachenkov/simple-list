package ru.igormayachenkov.list

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import ru.igormayachenkov.list.data.Statistics

private const val TAG = "myapp.StatisticRepository"

class StatisticRepository {
    private val _state = MutableStateFlow<Statistics>(Statistics.Loading)
    val state = _state.asStateFlow()

    suspend fun calculate(){
        Log.d(TAG,"calculate")
        _state.emit(Statistics.Loading)
        withContext(Dispatchers.IO){
            try {
                val stat = Database.statistics()
                _state.emit(stat)
            }catch (e:Exception){
                _state.emit(Statistics.Error(e.toString()))
            }
        }
    }
}