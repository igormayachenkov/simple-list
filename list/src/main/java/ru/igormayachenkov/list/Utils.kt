package ru.igormayachenkov.list

import android.util.Log
import android.widget.Toast

object Utils {
    fun areEqual(a: String?, b: String?): Boolean {
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

}