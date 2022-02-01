package ru.igormayachenkov.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import ru.igormayachenkov.list.data.List
import kotlinx.android.synthetic.main.a_item.*
import ru.igormayachenkov.list.data.Item
import ru.igormayachenkov.list.settings.ASettingsOld

class AItem : AppCompatActivity() {
    companion object {
        const val TAG = "myapp.AItem"

        fun show(activity: Activity){
            val intent = Intent(activity, AItem::class.java)
            activity.startActivity(intent)
        }
    }
    // Data objects
    private var list: List? = null
    private var item: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_item)

        // Settings (Preferences)
        settings = Settings(this)

        // Get data objects
        //val listId = intent.getLongExtra(Data.LIST_ID, 0)
        //list = Data.listOfLists.getList(listId)
        list = Logic.openList
        item = Logic.openItem
        list?.let { list->
            list.load()

            Log.d(TAG, "onCreate list#$list.id item#$item?.id")

            // Load data objects
            item?.let {
                // Load item fialds
                txtName.setText(it.name)
                txtDescr.setText(it.description)
            }?: kotlin.run {
                btnDel.visibility = View.GONE
            }

        }?: kotlin.run {
            Log.e(AList.TAG, "open list does is null")
            finish()
        }
        // Set handlers
        btnSave.setOnClickListener { onButtonSave() }
        btnDel.setOnClickListener  { onButtonDelete() }
        btnSettings.setOnClickListener  { onButtonSettings() }
    }
    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        settings = Settings(this)
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // HANDLERS
    fun onButtonSettings() {
        // Go to Settings activity
        ASettingsOld.open(this, R.xml.item_preferences)
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

    fun onButtonDelete() {
        if(settings.confirmDelete) {
            AlertDialog.Builder(this)
                    .setMessage(getString(R.string.item_confirm_delete))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes) { _, _ -> doDelete() }
                    .show()
        }else{
            doDelete()
        }
    }

    fun doDelete() {
        try {
            Logic.deleteItem(item)
            finish()
        }catch (e:Exception){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

        }
    }

    //----------------------------------------------------------------------------------------------
    // SETTINGS (Preferences)
    class Settings(context: Context?) {
        var confirmDelete   : Boolean = true

        init{
            context?.let {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                confirmDelete = prefs.getBoolean("confirm_delete", true)
            }
        }
    }
    var settings = Settings(null)

}