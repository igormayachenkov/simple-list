package ru.igormayachenkov.list.media

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private const val TAG = "myapp.MediaItem"

class MediaFile(val file:File) {
    val state = mutableStateOf<MediaState>(MediaState.Empty)

    suspend fun load(context: Context){
        if(state.value is MediaState.Empty) {
            Log.w(TAG, "load media file=${file.name}")
            state.value = MediaState.Loading
            // Do load
            try {
                doLoad(context)
                delay((Math.random() * 3000).roundToLong())
                if(Math.random()<0.2) throw Exception("load error")
                state.value = MediaState.Success((Math.random() * 100).roundToInt())
            }catch (e:Exception){
                state.value = MediaState.Error(e.message ?: e.toString())
            }
        }
    }

    private fun doLoad(context: Context){

    }
}