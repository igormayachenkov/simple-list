package ru.igormayachenkov.list

import android.R
import android.app.AlertDialog
import android.content.Context

////////////////////////////////////////////////////////////////////////////////////////////////////
// AlertDialog-BASED DIALOG
// FOR ERROR MESSAGE SHOW
class DlgError(context: Context, message: String?) {
    // DATA
    var alert: AlertDialog

    // SHOW
    fun show() {
        alert.show()
    }

    init {
        // Create builder
        val builder = AlertDialog.Builder(context)
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setTitle(R.string.dialog_alert_title)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok, null)

        // Create Alert dialog
        alert = builder.create()
    }
}