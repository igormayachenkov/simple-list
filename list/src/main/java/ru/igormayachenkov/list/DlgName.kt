package ru.igormayachenkov.list

import android.R
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

////////////////////////////////////////////////////////////////////////////////////////////////////
// AlertDialog-BASED DIALOG
// FOR NAME INPUT
class DlgName(
        context: Context,
        idTitle: Int,
        idNameHint: Int,
        name: String?,
        val callback:((String)->Unit)?
) {

    val alert: AlertDialog

    // SHOW
    fun show() {
        alert.show()
    }

    // CONSTRUCTOR
    init {

        // Create builder
        val builder = AlertDialog.Builder((context))
        if (idTitle != 0) builder.setTitle(idTitle)

        // Set up the input
        val input = EditText(context)
        if (idNameHint != 0) input.setHint(idNameHint)
        if (name != null) input.setText(name)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        input.textAlignment = View.TEXT_ALIGNMENT_CENTER
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, which -> // Raise event
            callback?.invoke(input.text.toString().trim { it <= ' ' })
        })
        builder.setNegativeButton(R.string.cancel, null)

        // Create alert dialog
        alert = builder.create()

        // Seet keyboard auto show
        alert.getWindow()?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}