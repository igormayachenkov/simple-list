package ru.igormayachenkov.list

import android.content.Context
import android.util.Log
import android.app.Activity
import android.view.View
import android.widget.Toast
import android.view.inputmethod.InputMethodManager
import ru.igormayachenkov.list.App.Companion.context


object Utils {
    fun areEqual(a: String?, b: String?): Boolean {
        if(a.isNullOrEmpty() and b.isNullOrEmpty()) return true
        if (a != null && b != null)
            return a.compareTo(b) == 0
        if (a == null && b == null)
            return true
        return false
    }

    fun areNotEqual(a: String?, b: String?): Boolean {
        return !areEqual(a, b)
    }

    fun showError(TAG:String, e:Exception){
        Log.e(TAG, e.stackTraceToString())
        Log.e(TAG, e.message.toString())
        Toast.makeText(App.instance(), e.message, Toast.LENGTH_LONG).show()
    }
    fun showErrorDialog(e:Exception){
        e.printStackTrace()
        DlgError(App.instance()!!, e.message).show()
    }

    //----------------------------------------------------------------------------------------------
    // KEYBOARD
    // https://developer.android.com/training/keyboard-input/visibility
    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let { imm ->
                if (imm is InputMethodManager) {
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
    }
    fun hideSoftKeyboard(activity: Activity?) {
        context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let { imm ->
            if (imm is InputMethodManager) {
                activity?.currentFocus?.windowToken?.let {
                    imm.hideSoftInputFromWindow(it, 0)
                }
            }
        }
    }
}