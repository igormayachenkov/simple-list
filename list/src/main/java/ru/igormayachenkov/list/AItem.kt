package ru.igormayachenkov.list

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.igormayachenkov.list.data.DList
import ru.igormayachenkov.list.data.Data

class AItem : AppCompatActivity() {
    companion object {
        const val TAG = "myapp.AItem"
    }
    // Data objects
    private var list: DList? = null
    private var itemIndex = 0

    // Controls
    var txtName: TextView? = null
    var txtDescr: TextView? = null
    var btnDelete: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_item)

        // Get data objects
        val id = intent.getLongExtra(Data.LIST_ID, 0)
        list = Data.listOfLists.getList(id)
        list!!.load()

        itemIndex = intent.getIntExtra(Data.ITEM_INDEX, -1)
        Log.d(TAG, "onCreate $id $itemIndex")

        // Load data objects

        // Controls
        txtName = findViewById<View>(R.id.txtName) as TextView
        txtDescr = findViewById<View>(R.id.txtDescr) as TextView
        btnDelete = findViewById<View>(R.id.btnDel) as ImageButton

        // LOAD DATA FIELDS
        if (itemIndex >= 0) {
            val item = list!!.items[itemIndex]

            // Load item fialds
            txtName!!.text = item.name
            txtDescr!!.text = item.description
        } else {
            btnDelete!!.visibility = View.GONE
        }
    }
    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // HANDLERS
    fun onButtonSave(v: View?) {
        // Validate data
        val name = txtName!!.text.toString()
        if (name.isEmpty()) {
            txtName!!.error = getString(R.string.item_error)
            return
        }

        // Save data
        if (itemIndex < 0) {
            // New item
            list!!.addItem(name, txtDescr!!.text.toString())
            setResult(Data.RESULT_INSERTED)
        } else {
            // Existed item
            list!!.updateItemName(itemIndex, name, txtDescr!!.text.toString())
            setResult(Data.RESULT_UPDATED)
        }

        // Return
        finish()
    }

    fun onButtonDelete(v: View?) {
        //Toast.makeText(this, "onButtonDelete", Toast.LENGTH_SHORT).show();
        // Delete item
        list!!.deleteItem(itemIndex)

        // Return
        setResult(Data.RESULT_DELETED)
        finish()
    }
}