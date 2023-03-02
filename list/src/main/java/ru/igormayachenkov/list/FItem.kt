package ru.igormayachenkov.list

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import ru.igormayachenkov.list.data.ItemChanges

class FItem : BaseFragment()  {

    //----------------------------------------------------------------------------------------------
    // STATIC
    companion object {
        const val TAG: String = "myapp.FItem"
        var instance:FItem? = null

    }
    // Controls
    private val btnSave:LinearLayout by lazy { requireView().findViewById(R.id.btnSave) }
    private val btnDel:LinearLayout  by lazy { requireView().findViewById(R.id.btnDel) }
    private val txtName:EditText     by lazy { requireView().findViewById(R.id.txtName) }
    private val txtDescr:EditText    by lazy { requireView().findViewById(R.id.txtDescr) }
    private val chkState:CheckBox    by lazy { requireView().findViewById(R.id.chkState) }

    //----------------------------------------------------------------------------------------------
    // FRAGMENT
    init {
        Log.d(TAG, "init")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        //retainInstance = true// TO PREVENT DESTROY ON SCREEN ROTATION !!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.f_item, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        instance = this

        // Restore status according to the data
        update(false)

        // Set handlers
        //fog.setOnClickListener     { onButtonCancel() } TODO add hide-on-fog-click setting
        btnSave.setOnClickListener { onButtonSave() }
        btnDel.setOnClickListener  { onButtonDelete() }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState) - is not required!
        Log.d(TAG, "onSaveInstanceState")

        // Save current input
        Logic.saveOpenItemChanges(loadInput())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        instance = null
    }

    //----------------------------------------------------------------------------------------------
    // DATA => CONTROLS
    override fun load():Boolean {
        Logic.openItem?.let { openItem ->
            Log.d(TAG, "load #${openItem}")

            //-------------------------------------
            // LOAD DATA
            openItem.changes?.let {
                with(it) {
                    txtName.setText(name)
                    txtDescr.setText(description)
                    chkState.isChecked = isChecked
                }
            } ?: kotlin.run {
                with(openItem.item) {
                    txtName.setText(name)
                    txtDescr.setText(description)
                    chkState.isChecked = isChecked
                }
            }
            return true
        }
        return false
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

