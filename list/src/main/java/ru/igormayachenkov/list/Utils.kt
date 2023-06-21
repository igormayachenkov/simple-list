package ru.igormayachenkov.list

import kotlinx.coroutines.delay

class ActionTimer(){
    private val start = System.currentTimeMillis()

    suspend fun pauseIfNeed(minTime:Long=800){
        val spent = System.currentTimeMillis()-start
        if(spent < minTime)
            delay(minTime-spent)
    }
}

//    fun areEqual(a: String?, b: String?): Boolean {
//        if(a.isNullOrEmpty() and b.isNullOrEmpty()) return true
//        if (a != null && b != null)
//            return a.compareTo(b) == 0
//        if (a == null && b == null)
//            return true
//        return false
//    }
//
//    fun areNotEqual(a: String?, b: String?): Boolean {
//        return !areEqual(a, b)
//    }

    //----------------------------------------------------------------------------------------------
    // KEYBOARD
    // https://developer.android.com/training/keyboard-input/visibility
//    fun showSoftKeyboard(view: View) {
//        if (view.requestFocus()) {
//            context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let { imm ->
//                if (imm is InputMethodManager) {
//                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
//                }
//            }
//        }
//    }
//    fun hideSoftKeyboard(activity: Activity?) {
//        context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let { imm ->
//            if (imm is InputMethodManager) {
//                activity?.currentFocus?.windowToken?.let {
//                    imm.hideSoftInputFromWindow(it, 0)
//                }
//            }
//        }
//    }

