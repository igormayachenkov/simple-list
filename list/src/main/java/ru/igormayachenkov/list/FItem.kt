package ru.igormayachenkov.list

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.f_item.*
import ru.igormayachenkov.list.data.ItemChanges
import ru.igormayachenkov.list.data.OpenItem

class FItem : Fragment()  {

    //----------------------------------------------------------------------------------------------
    // STATIC
    companion object {
        const val TAG: String = "myapp.FItem"

        fun show(){
            Log.d(TAG, "show")
            AMain.publicInterface?.showFragment(FItem(), TAG)
        }

        fun hide(){
            Log.d(TAG, "hide")
            AMain.publicInterface?.removeFragment(TAG)
        }

        // FACKED STUPID ANDROID: Sync DATA - UI
        fun onActivityCreated(fragmentManager:FragmentManager){
            val fragment = fragmentManager.findFragmentByTag(TAG)

            // CHECK WRONG CASES
            if(Logic.openItem!=null && fragment==null)
                show()
            if(Logic.openItem==null && fragment!=null)
                hide()
        }
    }

    //----------------------------------------------------------------------------------------------
    // FRAGMENT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        retainInstance = true// TO PREVENT DESTROY ON SCREEN ROTATION !!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.f_item, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        // Set handlers
        //fog.setOnClickListener     { onButtonCancel() } TODO add hide-on-fog-click setting
        btnSave.setOnClickListener { onButtonSave() }
        btnDel.setOnClickListener  { onButtonDelete() }

        // Load data
        Logic.openItem?.let {
            load(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState) - is not required!
        Log.d(TAG, "onSaveInstanceState")

        // Save current input
        Logic.saveOpenItemChanges(loadInput())
    }

    //----------------------------------------------------------------------------------------------
    // DATA + SHOW/HIDE
    fun load(openitem: OpenItem){
        Log.d(TAG, "load #${openitem}")

        //-------------------------------------
        // LOAD DATA
        openitem.changes?.let {
            with(it) {
                txtName.setText     (name )
                txtDescr.setText    (description)
                chkState.isChecked = isChecked
            }
        }?: kotlin.run {
            with(openitem.item) {
                txtName.setText     (name )
                txtDescr.setText    (description)
                chkState.isChecked = isChecked
            }
        }
    }

    // DATA INPUT
    fun loadInput():ItemChanges{
        return ItemChanges(
                txtName.text.toString().trim(),
                txtDescr.text.toString().trim(),
                chkState.isChecked
        )
    }

    //----------------------------------------------------------------------------------------------
    // HANDLERS
    fun onButtonCancel(){
        Logic.clearOpenItem()
    }

    fun onButtonSave() {
        Log.d(TAG,"onButtonSave")

        // Load input
        val input = loadInput()

        // Validate data
        if (input.name.isEmpty()) {
            txtName.error = getString(R.string.item_error)
            return
        }

        // Save data
        Logic.updateOpenItem(input)
    }

    fun onButtonDelete() {
        try {
            Logic.deleteOpenItem()
        }catch (e:Exception){
            Toast.makeText(this.activity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

}

