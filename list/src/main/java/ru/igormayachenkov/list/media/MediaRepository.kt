package ru.igormayachenkov.list.media

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File

private const val TAG = "myapp.MediaRepository"

class MediaRepository {
    private val store = HashMap<Long,List<MediaFile>>()

    companion object{
        private fun getItemMediaDir(itemId:Long):String    { return itemId.toString(16) }
        private fun getRootMediaDir(context: Context):File { return context.getExternalFilesDir(null)!! }
    }

    // 190c244e6be
    fun getItemMedia(itemId:Long, context: Context):List<MediaFile>? {
        store.get(itemId)?.let {
            return it
        }?:run{
            // Check the file existance
            val dirname = getItemMediaDir(itemId)
            Log.w(TAG,"   --- itemId=$itemId dirname=$dirname")
            val dir = File(getRootMediaDir(context), dirname)

            val fileContents = "Hello world!"
            //writeMedia(context, itemId, fileContents.toByteArray())

            if(dir.exists()){
                dir.list()?.let { filelist->
                    Log.w(TAG, "      filelist=${filelist.size}")
                    if (filelist.isNotEmpty()) {
                        // Create list of medial files
                        return ArrayList<MediaFile>().apply {
                            filelist.forEach{
                                add( MediaFile(File(dir,it)) )
                            }
                            // Remember in the store
                            store.put(itemId, this)
                        }
                    }
                }
            }
            return null
        }
    }

    fun addMedia(itemId: Long, uri:Uri){
        Log.w(TAG, "addMedia $uri")
    }

    private fun writeMedia(context: Context, itemId:Long, bytes : ByteArray){
        val filename = getItemMediaDir(itemId)
        Log.w(TAG, "writeMedia $filename")
        try {
            val file = File(getRootMediaDir(context), filename)
            file.writeBytes(bytes)
        }catch (e:Exception){
            Log.e(TAG, "write file", e)
        }
    }




}