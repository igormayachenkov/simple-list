package ru.igormayachenkov.list

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.igormayachenkov.list.data.Item
import ru.igormayachenkov.list.data.List
import ru.igormayachenkov.list.data.Data
import kotlinx.android.synthetic.main.a_list.*
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.android.synthetic.main.item_list.view.txtName

class AList : AppCompatActivity(){
    companion object {
        const val TAG = "myapp.AList"

        object ItemUI {
            const val KEY = "item_ui"
            const val CLICK_CHECKBOX = "click_checkbox"
            const val ICON_CLICK     = "icon_click"
            const val CLICK_NULL     = "click_null"
            const val LONGCLICK_CLICK= "longclick_click"
            const val default = CLICK_CHECKBOX
        }

        //    static final int FILE_OPEN_REQUEST      = 222;
        const val FILE_CREATE_REQUEST = 333
        const val FILE_CREATE_XML_REQUEST = 444

        fun show(activity: Activity){
            val intent = Intent(activity, AList::class.java)
            activity.startActivity(intent)
        }

        var instance : AList.PublicInterface? = null
    }

    // Data objects
    private var dataList: List? = null
    private val uiList = ArrayList<Item>()
    // Colors
    var colorChecked: Int
    var colorUnchecked: Int
    init {
        colorChecked = ContextCompat.getColor(App.context()!!, R.color.textChecked)
        colorUnchecked = ContextCompat.getColor(App.context()!!, R.color.textUnchecked)
    }
    val columnCount = 1
    var itemUI:String = ItemUI.default

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_list)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        instance = PublicInterface()

        // Preferences
        itemUI = readItemUI()

        dataList = Logic.openList
        dataList?.let {
            it.load()

            // Load controls
            title = it.name

            // LIST
            val manager = when {
                columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(this)
                else -> androidx.recyclerview.widget.GridLayoutManager(this, columnCount)
           }
            recyclerView.layoutManager = manager

            // Divider
//            val dividerItemDecoration = DividerItemDecoration(this , manager.orientation)
//            recyclerView.addItemDecoration(dividerItemDecoration)

            // DRAG/SWIPE https://medium.com/@mca.himanshusharma/draggable-recyclerview-in-kotlin-6c8b76af142c
//            val callback = DragCallback(
//                    ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
//                    ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))
//            ItemTouchHelper(callback)
//                    .attachToRecyclerView(recyclerView)

            // Adapter
            recyclerView.adapter = adapter

            // LOAD DATA
            reloadData()

        }?: kotlin.run {
            Log.e(TAG, "list #id does not exist")
            finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_list, menu)
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
        // Check prefs changed
        val newItemUI = readItemUI()
        if(itemUI.compareTo(newItemUI)!=0){
            itemUI = newItemUI
            adapter.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC INTERFACE (for Logic)
    inner class PublicInterface {
        fun close(){
            finish()
        }
        fun onListRenamed(){
            // Update title
            title = dataList!!.name
        }

        fun onItemInserted() {
            Log.w(TAG, "onItemInserted")
            reloadData()
        }

        fun onItemUpdated(isNameChanged: Boolean, isDescrChanged: Boolean) {
            Log.w(TAG, "onItemUpdated $isNameChanged $isDescrChanged")
            if (isNameChanged)
                reloadData()
            else
                adapter.notifyDataSetChanged()
                // TODO replace by notifyItemUpdatecd
        }

        fun onItemDeleted(id: Long) {
            Log.w(TAG, "onItemDeleted")
            getItemPosition(id)?.let {
                uiList.removeAt(it)
                adapter.notifyItemRemoved(it)
            }
        }
    }

    fun getItemPosition(id:Long):Int?{
        for(i in 0..uiList.size-1){
            val item = uiList[i]
            if(item.id==id) return i
        }
        return null
    }

    private fun reloadData() {
        Log.w(TAG, "RELOAD DATA")

        // Reload UI LIST
        uiList.clear()
        dataList!!.items?.let {
            for(item in it.values){
                uiList.add(item)
            }
            uiList.sortBy { it.name }
        }?: kotlin.run {
            Log.e(TAG, "list is not loaded")
        }

        updateListVisibility()

        adapter.notifyDataSetChanged()
    }

    private fun updateListVisibility(){
        if (uiList.size == 0) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    //----------------------------------------------------------------------------------------------
    // SETTINGS (Preferences)
    fun readItemUI():String{
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getString(ItemUI.KEY, ItemUI.default)!!
    }

    //----------------------------------------------------------------------------------------------
    // LIST ITEM
    fun openItem(view: View){
        val position = view.tag
        if(position is Int) {
            Logic.openItem(uiList[position], this)
        }
    }
    fun checkItem(view: View){
        val position = view.tag
        if(position is Int){
            // Change item state
            val item = uiList[position]
            item.changeState()
            // Update row
            adapter.notifyItemChanged(position)
            // TODO move to public interface
        }
    }
    fun onItemClick(view: View) {
        when(itemUI){
            ItemUI.CLICK_CHECKBOX -> openItem(view)
            ItemUI.CLICK_NULL     -> openItem(view)
            ItemUI.ICON_CLICK     -> checkItem(view)
            ItemUI.LONGCLICK_CLICK-> checkItem(view)
        }
    }
    fun onItemLongClick(view: View): Boolean {
        when(itemUI){
            ItemUI.LONGCLICK_CLICK-> openItem(view)
        }
        return true
    }
    fun onItemCheckIcon(view: View) {
        Log.d(TAG, "onItemCheckIcon")
        checkItem(view)
    }
    fun onItemOpenIcon(view: View) {
        Log.d(TAG, "onItemOpenIcon")
        openItem(view)
    }

    //----------------------------------------------------------------------------------------------
    // VIEW HOLDER
    inner class MyViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtName      : TextView = view.txtName
        val txtDescr     : TextView = view.txtDescription
        val itemBody     : View = view.itemBody
        val iconOpen     : View = view.iconOpen
        val iconCheck    : View = view.iconCheck
        init {
            itemBody.setOnClickListener         { onItemClick(it)}
            itemBody.setOnLongClickListener     { onItemLongClick(it)}
            iconOpen.setOnClickListener         { onItemOpenIcon(it)}
            iconCheck.setOnClickListener        { onItemCheckIcon(it)}
        }

        fun bind(position: Int) {
            val item = uiList[position]

            itemBody.tag = position
            iconCheck.tag = position
            iconOpen.tag = position

            txtName.text  = item.name

            if (item.description.isNullOrEmpty()) {
                txtDescr.visibility = View.GONE
            } else {
                txtDescr.visibility = View.VISIBLE
                txtDescr.text = item.description
            }

            iconOpen.visibility  = if(itemUI==ItemUI.ICON_CLICK) VISIBLE else GONE
            iconCheck.visibility = if(itemUI==ItemUI.CLICK_CHECKBOX) VISIBLE else GONE

            // Checked
            updateChecked(item)
        }

        fun updateChecked(item:Item){
            // Checkbox
            val isChecked = item.state == Item.ITEM_STATE_CHECKED
            iconCheck.isActivated = isChecked

            // Text color
            val textColor =  when(itemUI) {
                ItemUI.CLICK_CHECKBOX -> colorUnchecked
                else -> if (isChecked) colorChecked else colorUnchecked
            }
            txtName.setTextColor (textColor)
            txtDescr.setTextColor(textColor)
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
                            .inflate(R.layout.item_list, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind(position)
        }

        fun swapItems(fromPosition: Int, toPosition: Int) {
            Log.d(TAG,"swapItems $fromPosition $toPosition")
            if (fromPosition < toPosition) {
                for (i in fromPosition..toPosition - 1) {
                    uiList.set(i, uiList.set(i+1, uiList.get(i)));
                }
            } else {
                for (i in fromPosition..toPosition + 1) {
                    uiList.set(i, uiList.set(i-1, uiList.get(i)));
                }
            }

            notifyItemMoved(fromPosition, toPosition)
        }

    }

    //----------------------------------------------------------------------------------------------
    // DRAG/SWIPE CALLBACK
    inner class DragCallback(dragDirs: Int, swipeDirs: Int)
        : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs)
    {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            adapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }
    }

    //----------------------------------------------------------------------------------------------
    // MENU HANDLERS
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add       -> { onMenuAdd(); true }
            R.id.menu_settings  -> { onMenuSettings(); true }
            R.id.menu_rename    -> { onMenuRename(); true }
            R.id.menu_save_json -> { onMenuSave();true }
            R.id.menu_save_xml  -> { onMenuSaveXML();true }
            R.id.menu_delete    -> { onMenuDelete();true }
            R.id.menu_help      -> { onMenuHelp();true }
            else ->
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }
    private fun onMenuAdd() {
        Logic.openItem(null,this)
    }
    private fun onMenuHelp() {
        // Go to help activity
        val intent = Intent(this, AHelp::class.java)
        intent.putExtra(Data.ACTIVITY, "AList")
        startActivity(intent)
    }
    fun onMenuSettings() {
        // Go to Settings activity
        val intent = Intent(this, ASettings::class.java)
        startActivity(intent)
    }
    private fun onMenuDelete() {
        // Show yes/no dialog
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.list_delete_alert_title))
                .setMessage(getString(R.string.list_delete_alert_text))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    try{
                        Logic.deleteOpenList()
                    }catch (e:Exception){Utils.showError(TAG,e)}
                }
                .show()
    }

    private fun onMenuRename() {
        val dlg = DlgName(
                this,
                R.string.dialog_title_rename,
                R.string.main_add_hint,
                dataList!!.name,
                { text ->
                    try {
                        Logic.renameOpenList(text)
                    }catch (e:Exception){ Utils.showError(TAG, e) }
                }
        )
        dlg.show()
    }

    private fun onMenuSave() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TITLE, dataList!!.name + ".json")
        startActivityForResult(intent, FILE_CREATE_REQUEST)
    }

    private fun onMenuSaveXML() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TITLE, dataList!!.name + ".xml")
        startActivityForResult(intent, FILE_CREATE_XML_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult requestCode= $requestCode, resultCode=$resultCode")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        if (data == null) return
        when (requestCode) {
            FILE_CREATE_REQUEST -> doSave(data.data)
            FILE_CREATE_XML_REQUEST -> doSaveXML(data.data)
        }
    }

    //----------------------------------------------------------------------------------------------
    // FILE ACTIONs
    fun doSaveXML(uri: Uri?) {
        try {
            val bytes = Logic.saveListToXML(dataList!!, uri)
            // Show result
            Toast.makeText(this, bytes.toString() + " " + getString(R.string.bytes_saved), Toast.LENGTH_LONG).show()
        }catch (e:Exception){ Utils.showErrorDialog(e)}
    }

    fun doSave(uri: Uri?) {
        dataList?.let {
            val lists = ArrayList<List>()
            lists.add(it)
            try {
                Logic.saveLists( uri, lists)
            }catch (e:Exception){ Utils.showErrorDialog(e)}
        }
    }

//    private fun save(filename: String, format: String) {
//        var filename = filename
//        filename = "$filename.$format"
//        var file: File? = null
//        var error: String? = null
//        try {
//            //File dir = TheApp.context().getExternalFilesDir(null); //Android/data/<package.name>/files
//            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), Data.APP_FOLDER)
//            if (!dir.exists()) if (!dir.mkdirs()) throw Exception("Directory not created")
//            file = File(dir, filename)
//            val bw = BufferedWriter(FileWriter(file))
//            when (format) {
//                "xml" -> dataList!!.saveXML(bw)
//                "json" -> bw.write(dataList!!.toJSON().toString())
//                else -> throw Exception("unknown format '$format'")
//            }
//            bw.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            error = e.message
//        }
//
//        // SHOW RESULT
//        if (error == null) {
//            Log.d(TAG, "save size:" + (file?.usableSpace ?: "0"))
//            AlertDialog.Builder(this) //.setTitle(getString(R.string.dialog_title_save))
//                    .setMessage("""
//    ${getString(R.string.list_save_ok)}
//    Android/Documents/
//    ${Data.APP_FOLDER}/
//    $filename
//    """.trimIndent())
//                    .setPositiveButton("OK", null)
//                    .setIcon(android.R.drawable.ic_dialog_info)
//                    .show()
//        } else {
//            Log.d(TAG, "Save error:$error")
//            AlertDialog.Builder(this) //.setTitle(getString(R.string.dialog_title_save))
//                    .setMessage("""
//    ${getString(R.string.list_save_error)}
//    $error
//    """.trimIndent())
//                    .setPositiveButton("OK", null)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show()
//        }
//    }


}