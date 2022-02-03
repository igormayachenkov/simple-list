package ru.igormayachenkov.list

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import ru.igormayachenkov.list.data.List
import kotlinx.android.synthetic.main.f_item.*
import ru.igormayachenkov.list.data.Item
import ru.igormayachenkov.list.settings.ASettingsOld


class FItem : Fragment() {
    // STATIC
    companion object {
        val TAG: String = "myapp.FItem"
    }

    // Data objects
    private var list: List? = null
    private var item: Item? = null


    //----------------------------------------------------------------------------------------------
    // FRAGMENT
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true// TO PREVENT DESTROY ON SCREEN ROTATION
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.f_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        // Settings (Preferences)
        settings = Settings(App.context)

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
        settings = Settings(App.context)
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    fun finish(){
        activity?.supportFragmentManager?.popBackStack()
    }

    //----------------------------------------------------------------------------------------------
    // HANDLERS
    fun onButtonSettings() {
        // Go to Settings activity
        ASettingsOld.open(AList.instance, R.xml.item_preferences)
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
                AList.publicInterface?.onItemUpdated(isNameChanged, isDescrChanged)
            }
        }?: run{
            // NEW ITEM
            list!!.addItem(name, descr)
            AList.publicInterface?.onItemInserted()
        }

        // Return
        finish()
    }

    fun onButtonDelete() {
        if(settings.confirmDelete) {
            AlertDialog.Builder(App.context)
                    .setMessage(getString(R.string.item_confirm_delete))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes) { _, _ -> doDelete() }
                    .show()
        }else{
            doDelete()
        }
    }

    private fun doDelete() {
        try {
            Logic.deleteItem(item)
            finish()
        }catch (e:Exception){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
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