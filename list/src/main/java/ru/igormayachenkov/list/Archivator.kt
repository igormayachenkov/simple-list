package ru.igormayachenkov.list

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

private const val TAG = "myapp.Converter"

//----------------------------------------------------------------------------------------------
// ACTIVITY RESULT API
class Archivator(activity: ComponentActivity) {
    val createDoc = activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")){ uri->
        Log.d(TAG,"createDoc $uri")
    }


}