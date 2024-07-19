package ru.igormayachenkov.list

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

private const val TAG = "myapp.ResultAPI"

//----------------------------------------------------------------------------------------------
// ACTIVITY RESULT API
class ResultAPI(activity: ComponentActivity) {

    // SAVE ALL DATA
    val saveAll = activity.registerForActivityResult(ActivityResultContracts.CreateDocument(mimeType =  "application/json"))
    { uri->
        Log.d(TAG,"saveAll success $uri")
        if(uri!=null){
            app.saverRepository.saveAll(uri)
        }
    }

    // LOAD ALL DATA
    val loadAll = activity.registerForActivityResult(ActivityResultContracts.OpenDocument())
    { uri->
        Log.d(TAG,"loadAll success $uri")
        if(uri!=null){
            app.saverRepository.loadAll(uri)
        }
    }

    // LOAD MEDIA FILE
    fun showLoadMediaDialog(itemId:Long){
        mediaItemId = itemId
        loadMedia.launch(arrayOf("*/*")) // start open dialog
    }
    companion object{  private var mediaItemId:Long?=null    }
    private val loadMedia = activity.registerForActivityResult(ActivityResultContracts.OpenDocument())
    { uri->
        Log.d(TAG,"loadMedia success $uri")
        if(uri!=null){
            mediaItemId?.let {itemId->
                app.mediaRepository.addMedia(itemId, uri)
            }
        }
    }

}