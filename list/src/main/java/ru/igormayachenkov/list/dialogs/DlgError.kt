package ru.igormayachenkov.list.dialogs

import android.R
import android.app.AlertDialog
import android.util.Log
import ru.igormayachenkov.list.AMain

////////////////////////////////////////////////////////////////////////////////////////////////////
// AlertDialog-BASED DIALOG
// FOR ERROR MESSAGE SHOW
object DlgError {
    const val TAG = "myapp.DlgError"

    // SHOW
    fun show(ex: Exception) {
        Log.e(TAG, ex.toString())

        AMain.context?.let { context ->
            // Create builder
            val builder = AlertDialog.Builder(context)
            builder.setIcon(R.drawable.ic_dialog_alert)
            builder.setTitle(R.string.dialog_alert_title)
            builder.setMessage(ex.message)
            builder.setPositiveButton(R.string.ok, null)

            // Create Alert dialog
            builder.create().show()
        }
    }
}