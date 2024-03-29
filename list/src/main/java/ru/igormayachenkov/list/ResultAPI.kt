package ru.igormayachenkov.list

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

private const val TAG = "myapp.ResultAPI"

//----------------------------------------------------------------------------------------------
// ACTIVITY RESULT API
class ResultAPI(activity: ComponentActivity) {
    val saveAll = activity.registerForActivityResult(ActivityResultContracts.CreateDocument(mimeType =  "application/json"))
    { uri->
        Log.d(TAG,"saveAll success $uri")
        if(uri!=null){
            app.saverRepository.saveAll(uri)
        }
    }

    val loadAll = activity.registerForActivityResult(ActivityResultContracts.OpenDocument())
    { uri->
        Log.d(TAG,"loadAll success $uri")
        if(uri!=null){
            app.saverRepository.loadAll(uri)
        }
    }

}