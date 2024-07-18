package ru.igormayachenkov.list.media

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private const val TAG = "myapp.MediaItem"

class MediaFile(val ownerId:Long) {
    val state = mutableStateOf<MediaState>(MediaState.Empty)
    suspend fun load(){
        if(state.value is MediaState.Empty) {
            Log.w(TAG, "load media #${ownerId}")
            state.value = MediaState.Loading
            // Do load
            try {
                delay((Math.random() * 3000).roundToLong())
                if(Math.random()<0.2) throw Exception("load error")
                state.value = MediaState.Success((Math.random() * 100).roundToInt())
            }catch (e:Exception){
                state.value = MediaState.Error(e.message ?: e.toString())
            }
        }
    }
}