package ru.igormayachenkov.list

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import org.json.JSONObject
import ru.igormayachenkov.list.data.List
import ru.igormayachenkov.list.data.Data
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlinx.android.synthetic.main.a_main.*
import kotlinx.android.synthetic.main.item_main.view.*
import ru.igormayachenkov.list.settings.ASettingsOld
import ru.igormayachenkov.list.settings.BodyAction
import ru.igormayachenkov.list.settings.ColumnsNumnber
import androidx.lifecycle.Observer
import ru.igormayachenkov.list.settings.Settings


class AMain : AppCompatActivity() {
    companion object {
        private const val TAG = "myapp.AMain"
        const val FILE_OPEN_REQUEST = 222
        const val FILE_CREATE_REQUEST = 333

        var instance : PublicInterface? = null

    }

    // Data objects
    private val uiList = ArrayList<List>()
    private val comparatorName = Comparator<List> { a, b -> a.name.compareTo(b.name) }

    val columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.main_title)

        instance = PublicInterface()

        // LIST
        val colnumObserver = Observer<Int> { colnum ->
            val manager = getListLayoutManager(colnum)
            recyclerView.layoutManager = manager
        }
        Settings.mainColNumber.observe(this, colnumObserver)
        recyclerView.adapter = adapter

        reloadData()
    }

    fun getListLayoutManager(colnum:Int): RecyclerView.LayoutManager {
        //val colnum = ColumnsNumnber.getNumber(settings.colNumber)
        return when {
            colnum <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(this)
            else -> androidx.recyclerview.widget.GridLayoutManager(this, colnum)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        instance = null
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }
    //----------------------------------------------------------------------------------------------
    // PUBLIC INTERFACE
    inner class PublicInterface {
        fun onDataUpdated(){
            Log.w(TAG, "onDataUpdated")
            reloadData()
        }
        fun onListInserted() {
            Log.w(TAG, "onListInserted")
            reloadData()
        }
        fun onListRenamed(id:Long) {
            Log.w(TAG, "onListRenamed #$id")
            reloadData()
        }
        fun onListDeleted(id: Long) {
            Log.w(TAG, "onListDeleted")
            getItemPosition(id)?.let {
                uiList.removeAt(it)
                adapter.notifyItemRemoved(it)
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // UPDATERs
    private fun reloadData() {
        Log.w(TAG, "RELOAD DATA")

        // Reload sorted list
        uiList.clear()
        uiList.addAll(Data.listOfLists.aLL)
        Collections.sort(uiList, comparatorName)

        // Update controls
        if (uiList.size == 0) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
        }
    }
    fun getItemPosition(id:Long):Int?{
        for(i in 0..uiList.size-1){
            val item = uiList[i]
            if(item.id==id) return i
        }
        return null
    }

    //----------------------------------------------------------------------------------------------
    // LIST ITEM
    fun onListItemClick(view: View) {
        Log.d(TAG,"onListItemClick")
        val list = view.tag
        if(list is List)
            Logic.openList(list,this)
    }

//    //----------------------------------------------------------------------------------------------
//    // SETTINGS (Preferences)
//    class Settings(context: Context?) {
//        var colNumber   : String = ColumnsNumnber.default
//
//        init{
//            context?.let {
//                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
//                colNumber   = prefs.getString ("main_columns_number", BodyAction.default)!!
//            }
//        }
//    }
//    var settings = Settings(null)

    fun onMenuAdd() {
        // ALERT DIALOG
        val dlg = DlgName(
                this,
                R.string.main_add_menu,
                R.string.main_add_hint,
                null,  // name value
                { text ->
                    try {
                        val list = Logic.createList(text)
                        Logic.openList(list,this)
                    }catch (e:Exception){ Utils.showError(TAG, e) }
                }
        )
        dlg.show()
    }

    //----------------------------------------------------------------------------------------------
    // MENU
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add       -> { onMenuAdd(); true }
            R.id.menu_settings  -> { onMenuSettings(); true }
            R.id.menu_save      -> { onMenuSave(); true }
            R.id.menu_clear     -> { onMenuClear(); true }
            R.id.menu_load      -> { onMenuLoad(); true }
            R.id.menu_help      -> { onMenuHelp(); true }
            else ->
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }
    fun onMenuHelp() {
        // Go to help activity
        val intent = Intent(this, AHelp::class.java)
        intent.putExtra(Data.ACTIVITY, "AMain")
        startActivity(intent)
    }
    fun onMenuSettings() {
        // Go to Settings activity
        ASettingsOld.open(this, R.xml.main_preferences)
    }

    fun onMenuClear() {
        // SHOW DIALOG
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.main_clear_message)
        // Set up the buttons
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.setPositiveButton(android.R.string.ok) { _,_ ->
            try {
                Logic.deleteALL()
            }catch (e:Exception){ Utils.showError(TAG, e) }
        }
        builder.show()
    }

    fun onMenuLoad() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers, it would be "*/*".
        intent.type = "*/*"

        startActivityForResult(intent, FILE_OPEN_REQUEST)
    }

    fun onMenuSave() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Create a file with the requested MIME type.
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TITLE, "Simple List.json")
        startActivityForResult(intent, FILE_CREATE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult requestCode= $requestCode, resultCode=$resultCode")
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return
        if (data == null) return
        when (requestCode) {
            FILE_OPEN_REQUEST   -> doLoad(data.data)
            FILE_CREATE_REQUEST -> doSave(data.data, Data.listOfLists.aLL ) // all lists
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DO ACTIONs
    fun doSave(uri: Uri?, lists: Collection<List>) {
        try {
            val bytes = Logic.saveLists(uri,lists)
            // Show result
            Toast.makeText(this, bytes.toString() + " " + this.getString(R.string.bytes_saved), Toast.LENGTH_LONG).show()
        } catch (e: Exception) { Utils.showErrorDialog(e) }
    }

    fun doLoad(uri: Uri?) {
        try {
            // Read file
            val json = Logic.readJSON(uri)
            // Load
            doLoad(json)
        } catch (e: Exception) { Utils.showErrorDialog(e) }
    }

    fun doLoad(json: JSONObject?) {
        // Estimate loading
        val estimation = Data.estimateLoadingFromJSON(json!!)
        val message =   "${getString(R.string.load_to_insert)} ${estimation.toInsert}\n"+
                        "${getString(R.string.load_to_update)} ${estimation.toUpdate}"
        // Ask user for request
        val dlg = DlgCommon(this, R.string.load_title, 0, {
            try {
                Logic.loadFromJSON(json)
                // Show result
                Toast.makeText(this@AMain, R.string.load_success, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                DlgError(this@AMain, e.message).show()
            }
        })
        dlg.setMessage(message)
        dlg.show()
    }

    //----------------------------------------------------------------------------------------------
    // VIEW HOLDER
    inner class MyViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtName      : TextView = view.txtName
        init {
            view.setOnClickListener{v->onListItemClick(v)}
        }

        fun bind(position: Int) {
            val list = uiList[position]
            txtName.text = list.name
            itemView.tag = list
        }
    }

    //----------------------------------------------------------------------------------------------
    // LIST ADAPTER
    val adapter = MyListAdapter()

    inner class MyListAdapter():RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int {
            return uiList.size
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_main, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind(position)
        }

    }
}