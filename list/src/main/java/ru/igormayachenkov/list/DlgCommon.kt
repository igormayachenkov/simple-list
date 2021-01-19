package ru.igormayachenkov.list

import android.R
import android.content.Context
import androidx.appcompat.app.AlertDialog

////////////////////////////////////////////////////////////////////////////////////////////////////
// AlertDialog-BASED DIALOG
// FOR INFO SHOWING OR CONFIRMATION REQUEST
class DlgCommon(
        context: Context,
        idTitle: Int,
        idMessage: Int,
        val callback: (()->Unit)?
        ) {
    var alert: AlertDialog

    // SETTERS
    fun setMessage(message: String) {
        alert.setMessage(message)
    }

    // SHOW
    fun show() {
        alert.show()
    }

    // CONSTRUCTOR
    init {

        // Create builder
        val builder = AlertDialog.Builder(context!!)
        if (idTitle != 0) builder.setTitle(idTitle)
        if (idMessage != 0) builder.setMessage(idMessage)

        // Set up the buttons
        builder.setPositiveButton(R.string.ok) { dialog, which -> // Raise event
            callback?.invoke()
        }
        if (callback != null) builder.setNegativeButton(R.string.cancel, null)

        // Create alert dialog
        alert = builder.create()
    }
}