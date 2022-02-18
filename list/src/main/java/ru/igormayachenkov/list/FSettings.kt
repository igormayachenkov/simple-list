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
        fun onBackPressed(){
            Log.d(TAG, "onBackPressed")
            instance?.popupView?.let {
                if(it.isVisible)
                    it.hide()
                else
                    hide()
            }
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
        instance = this
        popupView = PopupView(popupContainer)

        // Update state
        if(isActive) {
            load()
            view.visibility = VISIBLE
        }else{
            view.visibility = GONE // initial hidden state !!!
        }

        // Set handlers
        btnBack.setOnClickListener { hide() }
        btnRowLeft.setOnClickListener  { onBtnItemSide(it) }
        btnRowRight.setOnClickListener { onBtnItemSide(it) }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        instance = null
        popupView = null
        super.onDestroyView()
    }



    //----------------------------------------------------------------------------------------------
    // DATA + SHOW/HIDE
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