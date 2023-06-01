package ru.igormayachenkov.list

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

private const val TAG = "myapp.ResultAPI"

//----------------------------------------------------------------------------------------------
// ACTIVITY RESULT API
class ResultAPI(activity: ComponentActivity) {
    val saveAll = activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/json"))
    { uri->
        Log.d(TAG,"saveAll success $uri")
        if(uri!=null){
            App.saverRepository.saveAll(uri)
        }
    }


}