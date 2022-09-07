package ru.igormayachenkov.list.dialogs

import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import ru.igormayachenkov.list.AMain
import ru.igormayachenkov.list.App

////////////////////////////////////////////////////////////////////////////////////////////////////
// AlertDialog-BASED DIALOG
// FOR ERROR MESSAGE SHOW
object DlgError {
    const val TAG = "myapp.DlgError"

    // SHOW DIALOG
    fun show(ex: Exception) {
        Log.e(TAG, ex.toString())

        AMain.instance?.let { context ->
            // Create builder
            val builder = AlertDialog.Builder(context)
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setTitle(android.R.string.dialog_alert_title)
            builder.setMessage(ex.message)
            builder.setPositiveButton(android.R.string.ok, null)

            // Create Alert dialog
            builder.create().show()
        }
    }

    fun showErrorToast(TAG:String, e:Exception){
        Log.e(TAG, e.stackTraceToString())
        showErrorToast(TAG, e.message.toString())
    }

    fun showErrorToast(TAG:String, message:String){
        Log.e(TAG, message)
        Toast.makeText(App.context, message, Toast.LENGTH_LONG).show()
    }
}