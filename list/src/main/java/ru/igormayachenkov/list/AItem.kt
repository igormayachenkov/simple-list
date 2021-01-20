package ru.igormayachenkov.list

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.igormayachenkov.list.data.List
import ru.igormayachenkov.list.data.Data
import kotlinx.android.synthetic.main.a_item.*
import ru.igormayachenkov.list.data.Item

class AItem : AppCompatActivity() {
    companion object {
        const val TAG = "myapp.AItem"
    }
    // Data objects
    private var list: List? = null
    private var item: Item? = null

    // Controls
//    var txtName: TextView? = null
//    var txtDescr: TextView? = null
//    var btnDelete: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_item)

        // Get data objects
        val listId = intent.getLongExtra(Data.LIST_ID, 0)
        list = Data.listOfLists.getList(listId)
        list?.let { list->
            list.load()

            val itemId:Long = intent.getLongExtra(Data.ITEM_ID, 0L)
            Log.d(TAG, "onCreate list#$listId item#$itemId")

            // Load data objects
            if (itemId != 0L) {
                item = list.items!!.get(itemId)
                item?.let {
                    // Load item fialds
                    txtName.setText(it.name)
                    txtDescr.setText(it.description)
                }?: kotlin.run {
                    Log.e(AList.TAG, "item #$itemId not found in the list")
                    finish()
                }
            } else {
                btnDel.visibility = View.GONE
            }
        }?: kotlin.run {
            Log.e(AList.TAG, "list #$listId does not exist")
            finish()
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
        val name = txtName.text.toString().trim()
        if (name.isEmpty()) {
            txtName!!.error = getString(R.string.item_error)
            return
        }
        var descr : String? = txtDescr.text.toString().trim()
        if(descr.isNullOrEmpty()) descr = null

        Log.d(TAG,"onButtonSave '$name' '$descr'")

        // Save data
        item?.let { item ->
            // EXISTED ITEM
            // Check changes
            val isNameChanged  = Utils.areNotEqual(name, item.name)
            val isDescrChanged = Utils.areNotEqual(descr, item.description)
            if(isNameChanged || isDescrChanged) {
                // Update
                list!!.updateItemName(item.id, name, descr)
                AList.instance?.onItemUpdated(isNameChanged, isDescrChanged)
            }
        }?: run{
            // NEW ITEM
            list!!.addItem(name, descr)
            AList.instance?.onItemInserted()
        }

        // Return
        finish()
    }

    fun onButtonDelete(v: View?) {
        //Toast.makeText(this, "onButtonDelete", Toast.LENGTH_SHORT).show();
        // Delete item
        item?.id?.let {
            list!!.deleteItem(it)
            AList.instance?.onItemDeleted()
        }
        finish()
    }
}