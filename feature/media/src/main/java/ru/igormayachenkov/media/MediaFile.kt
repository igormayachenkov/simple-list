package ru.igormayachenkov.media

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import kotlin.math.roundToLong

private const val TAG = "myapp.MediaFile"

class MediaFile(
    val file:File
) : IFile {
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Unloaded)
    override val loadingState = _loadingState.asStateFlow()

    override suspend fun load(context: Context){
        if(_loadingState.value is LoadingState.Unloaded) {
            Log.w(TAG, "load media file=${file.name}")
            _loadingState.emit( LoadingState.Loading )
            // Do load
            try {
                delay((Math.random() * 3000).roundToLong())
                //if(Math.random()<0.2) throw Exception("load error")
                val content = doLoad(context)
                //state.value = LoadingState.Success((Math.random() * 100).roundToInt())
                //state.value = LoadingState.Success(file.length().toInt())
                _loadingState.emit( LoadingState.Success(content) )
            }catch (e:Exception){
                _loadingState.emit( LoadingState.Error(e.message ?: e.toString()) )
            }
        }
    }

    private fun doLoad(context: Context):FileContent{
        val bytes = file.readBytes()
        //return  FileContent.Binary(bytes.size)
        return  FileContent.Image(BitmapFactory.decodeFile(file.path))
    }
}