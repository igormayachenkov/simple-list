package ru.igormayachenkov.list.media

import android.content.Context
import android.util.Log
import java.io.File

private const val TAG = "myapp.MediaRepository"

class MediaRepository {
    private val store = HashMap<Long,MediaFile>()

    companion object{
        fun filenameForItem(itemId:Long):String { return itemId.toString(16) }
        fun getDir(context: Context):File {return context.getExternalFilesDir(null)!!}
    }

    // 190c244e6be
    fun getMediaForItem(itemId:Long, context: Context):MediaFile? {
        return store.get(itemId) ?: run{
            // Check the file existance
            val filename = filenameForItem(itemId)
            val file = File(getDir(context), filename)

            val fileContents = "Hello world!"
            //writeMedia(context, itemId, fileContents.toByteArray())

            if(file.exists()){
                // Create new item
                MediaFile(file).apply {
                    store.put(itemId, this)
                }
            } else null
        }
    }

    private fun writeMedia(context: Context, itemId:Long, bytes : ByteArray){
        val filename = filenameForItem(itemId)
        Log.w(TAG, "writeMedia $filename")
        try {
            val file = File(getDir(context), filename)
            file.writeBytes(bytes)
        }catch (e:Exception){
            Log.e(TAG, "write file", e)
        }
    }




}