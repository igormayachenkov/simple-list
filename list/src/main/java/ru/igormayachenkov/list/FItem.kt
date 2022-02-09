package ru.igormayachenkov.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import ru.igormayachenkov.list.data.Item
import kotlinx.android.synthetic.main.f_item.*
import org.json.JSONObject
import ru.igormayachenkov.list.Prefs.Companion.OPEN_ITEM_CHANGES
import ru.igormayachenkov.list.data.OpenItem

class FItem : BaseFragment()  {

    //----------------------------------------------------------------------------------------------
    // STATIC
    companion object {
        val TAG: String = "myapp.FItem"
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

        view.visibility = GONE // initial hidden state !!!

        // Set handlers
        //fog.setOnClickListener     { onButtonCancel() } TODO add hide-on-fog-click setting
        btnSave.setOnClickListener { onButtonSave() }
        btnDel.setOnClickListener  { onButtonDelete() }
        // Observe open item
        Logic.openItem.observe(viewLifecycleOwner, Observer<OpenItem?> { openitem->
            if(openitem!=null) show(openitem.item) else hide()
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState) - is not required!
        Log.d(TAG, "onSaveInstanceState")

        // Save current input
        val json = loadInput().toJSON()
        Log.d(TAG, "*** save $json")
        Logic.pref.saveJSON(OPEN_ITEM_CHANGES, json)

    }


    //----------------------------------------------------------------------------------------------
    // SHOW/HIDE
    fun show(item: Item){
        Log.d(TAG, "show #${item.id}")

        //-------------------------------------
        // LOAD DATA
        // Get saved input
        val saved = DataInput.fromJSON(
                Logic.pref.loadJSON(OPEN_ITEM_CHANGES))
        Log.d(TAG, "*** saved $saved")

        // Load controls
        if(saved!=null) {
            with(saved) {
                txtName.setText     (name )
                txtDescr.setText    (description)
                chkState.isChecked = isChecked
            }
        }else {
            with(item) {
                txtName.setText     (name )
                txtDescr.setText    (description)
                chkState.isChecked = isChecked
            }
        }
        //-------------------------------------

        // Show fragment
        showFragment()
    }

    fun hide(){
        Log.d(TAG, "hide")
        //-------------------------------------
        // UNLOAD DATA
        // Clear saved input
        Logic.pref.remove(OPEN_ITEM_CHANGES)
        //-------------------------------------

        // Hide fragment
        hideFragment()
        chkState.isChecked = false // to prevent blinking
    }

    //----------------------------------------------------------------------------------------------
    // DATA INPUT
    data class DataInput(
        val name        : String,
        val description : String,
        val isChecked   : Boolean
    ){
        fun toJSON():JSONObject{
            val json = JSONObject()
            json.put("name",        name)
            json.put("description", description)
            json.put("isChecked",   isChecked)
            return json
        }
        // From JSON
        private constructor(json:JSONObject):this(
                json.getString("name"),
                json.getString("description"),
                json.getBoolean("isChecked"),
        )
        companion object{
            fun fromJSON(json:JSONObject?):DataInput?{
                json?.let {
                    try {
                        return DataInput(it)
                    } catch (e: Exception) {
                        Log.e(TAG, e.message.toString())
                    }
                }
                return null
            }
        }
    }

    fun loadInput():DataInput{
        return DataInput(
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

