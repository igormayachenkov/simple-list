package ru.igormayachenkov.list.settings

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.igormayachenkov.list.AMain
import ru.igormayachenkov.list.R

class FRowSide : Fragment()  {
    //----------------------------------------------------------------------------------------------
    // STATIC
    companion object {
        const val TAG: String = "myapp.FRowSide"

        fun show() {
            Log.d(TAG, "show")
            AMain.instance?.showDialog(FRowSide(), TAG)
        }

        fun hide() {
            Log.d(TAG, "hide")
            AMain.instance?.hideDialog()
        }
    }
    // Controls
    private val viewFog:View by lazy { requireView().findViewById(R.id.viewFog) }

    //----------------------------------------------------------------------------------------------
    // FRAGMENT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        //retainInstance = true// TO PREVENT DESTROY ON SCREEN ROTATION !!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.f_row_side, null)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        // Update state
        load()

        // Set handlers
        viewFog.setOnClickListener { hide() }
    }
    //----------------------------------------------------------------------------------------------
    // DATA
    private fun load(){
        Log.d(TAG, "load")

    }
    //----------------------------------------------------------------------------------------------
    // HANDLERS
}