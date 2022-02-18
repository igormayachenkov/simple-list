package ru.igormayachenkov.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.f_settings.*

class FSettings : BaseFragment()   {
    //----------------------------------------------------------------------------------------------
    // STATIC
    companion object {
        const val TAG: String = "myapp.FSettings"
        private var instance : FSettings? = null
        private var isActive :Boolean = false
        val isItActive:Boolean
            get() = isActive

        fun show(){
            Log.d(TAG, "show")
            isActive = true
            instance?.let {
                it.load()
                it.showFragment()
            }
        }
        fun hide(){
            Log.d(TAG, "hide")
            isActive = false
            instance?.hideFragment()
        }

    }

    //----------------------------------------------------------------------------------------------
    // FRAGMENT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        //retainInstance = true// TO PREVENT DESTROY ON SCREEN ROTATION !!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.f_settings, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        instance = this

        // Update state
        if(isActive) {
            load()
            view.visibility = VISIBLE
        }else{
            view.visibility = GONE // initial hidden state !!!
        }

        // Set handlers
        btnBack.setOnClickListener { hide() }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        instance = null
        super.onDestroyView()
    }



    //----------------------------------------------------------------------------------------------
    // DATA + SHOW/HIDE
    private fun load(){
        Log.d(TAG, "load")

    }


}