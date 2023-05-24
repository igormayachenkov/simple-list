package ru.igormayachenkov.list

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        withContext(Dispatchers.IO){
            _state.emit(Statistics.Loading)
            delay(2000)
            _state.emit(Statistics.Success(13,1013))
        }
    }
}