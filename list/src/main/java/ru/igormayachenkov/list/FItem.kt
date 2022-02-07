package ru.igormayachenkov.list

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import ru.igormayachenkov.list.data.Item
import kotlinx.android.synthetic.main.f_item.*

class FItem : Fragment()  {

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
        Logic.openItem.observe(viewLifecycleOwner, Observer<Item?> { load(it) })
    }

    //----------------------------------------------------------------------------------------------
    // LOAD DATA
    fun load(item: Item?){
        Log.d(TAG, "load item #${item?.id}")

        view?.let { view ->
            if (item != null) {
                // Load data
                txtName.setText(item.name)
                txtDescr.setText(item.description)
                // Show
                view.visibility = VISIBLE
            } else {
                // Hide
                if (view.visibility == VISIBLE) {
                    Utils.hideSoftKeyboard(activity)
                    view.visibility = GONE
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // HANDLERS
    fun onButtonCancel(){
        Logic.setOpenItem(null)
    }

    fun onButtonSave() {
        // Validate data
        val name = txtName.text.toString().trim()
        if (name.isEmpty()) {
            txtName!!.error = getString(R.string.item_error)
            return
        }
        var descr : String? = txtDescr.text.toString().trim()
        if(descr.isNullOrEmpty()) descr = null

        Log.d(TAG,"onButtonSave '$name' '$descr'")

        // Save data
        Logic.updateOpenItem(name, descr)
    }

    fun onButtonDelete() {
        try {
            Logic.deleteOpenItem()
        }catch (e:Exception){
            Toast.makeText(this.activity, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}

