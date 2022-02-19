package ru.igormayachenkov.list

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.f_settings.*

//--------------------------------------------------------------------------------------------------
// DIALOG: Settings
// "Dialog" means that the show/hide status is controlled by crazy android UI (not by my data)

class FSettings : Fragment()   {
    //----------------------------------------------------------------------------------------------
    // STATIC
    companion object {
        const val TAG: String = "myapp.FSettings"

        fun show(){
            Log.d(TAG, "show")
            AMain.publicInterface?.showDialog(FSettings(), TAG)
        }
        fun hide(){
            Log.d(TAG, "hide")
            AMain.publicInterface?.hideDialog()
        }
        fun onBackPressed(){
            Log.d(TAG, "onBackPressed")
//            instance?.popupView?.let {
//                if(it.isVisible)
//                    it.hide()
//                else
//                    hide()
//            }
        }
    }

    // CONTROLS
    var popupView:PopupView?=null

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
        popupView = PopupView(popupContainer)

        // Update state
        load()

        // Set handlers
        btnBack.setOnClickListener { hide() }
        btnRowLeft.setOnClickListener  { onBtnItemSide(it) }
        btnRowRight.setOnClickListener { onBtnItemSide(it) }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        popupView = null
        super.onDestroyView()
    }



    //----------------------------------------------------------------------------------------------
    // DATA
    private fun load(){
        Log.d(TAG, "load")

    }

    //----------------------------------------------------------------------------------------------
    // HANDLERS
    private fun onBtnItemSide(btn: View){
        val popup:ViewGroup = layoutInflater.inflate(R.layout.sel_row_side, null) as ViewGroup
//        menu.children.forEach { view->
//            view.setOnClickListener(menuClickListener)
//        }
        popupView?.show(popup, btn,
                PopupView.VERT_ALIGNMENT.TOP,
                if(btn.id==btnRowLeft.id)
                    PopupView.HORIZ_ALIGNMENT.LEFT
                else
                    PopupView.HORIZ_ALIGNMENT.RIGHT
        )
    }

}