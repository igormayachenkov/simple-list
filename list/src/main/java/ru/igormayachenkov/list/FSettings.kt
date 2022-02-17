package ru.igormayachenkov.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.f_settings.*

class FSettings : BaseFragment()   {
    //----------------------------------------------------------------------------------------------
    // STATIC
    companion object {
        const val TAG: String = "myapp.FSettings"
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

        view.visibility = GONE // initial hidden state !!!

        load()

        // Set handlers
        btnBack.setOnClickListener { Settings.isVisible.value = false }

        // Observe Settings visibility
        Settings.isVisible.observe(viewLifecycleOwner, Observer<Boolean> { isVisible->
            if(isVisible==true) showFragment() else hideFragment()
        })
    }

    //----------------------------------------------------------------------------------------------
    // DATA
    fun load(){
        Log.d(TAG, "load")

    }

}