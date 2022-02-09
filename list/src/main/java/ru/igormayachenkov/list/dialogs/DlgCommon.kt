package ru.igormayachenkov.list.dialogs

import android.R
import android.content.Context
import androidx.appcompat.app.AlertDialog
import ru.igormayachenkov.list.AMain

////////////////////////////////////////////////////////////////////////////////////////////////////
// AlertDialog-BASED DIALOG
// FOR INFO SHOWING OR CONFIRMATION REQUEST
object DlgCommon{

    // SHOW
    fun show(
            idTitle: Int,
            message: String?,
            callback: (()->Unit)?
    ){
        AMain.instance?.let { context->
            // Create builder
            val builder = AlertDialog.Builder(context)
            if (idTitle != 0) builder.setTitle(idTitle)
            message?.let {  builder.setMessage(it) }

            // Set up the buttons
            builder.setPositiveButton(R.string.ok) { dialog, which -> // Raise event
                callback?.invoke()
            }
            if (callback != null) builder.setNegativeButton(R.string.cancel, null)

            // Create alert dialog
            builder.create().show()
        }
    }
}