package ru.igormayachenkov.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ru.igormayachenkov.list.AMain.Companion.doSave
import ru.igormayachenkov.list.data.Item
import ru.igormayachenkov.list.data.List
import ru.igormayachenkov.list.data.Data
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import kotlinx.android.synthetic.main.a_list.*
//import kotlinx.android.synthetic.main.item_list.*

class AList : AppCompatActivity(), OnItemClickListener, OnItemLongClickListener {
    // Data objects
    private var list: List? = null

    // Controls
//    var viewList: ListView? = null
//    var viewEmpty: View? = null

    // Adapter
    private var adapter: ListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_list)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        // Controls
//        viewList = findViewById<View>(R.id.listView) as ListView
//        viewEmpty = findViewById(R.id.emptyView)

        // Get data objects
        val id = intent.getLongExtra(Data.LIST_ID, 0)
        list = Data.listOfLists.getList(id)
        list?.let {
            // Load data objects
            it.load()

            // Load controls
            title = it.name

            // List
            adapter = ListAdapter(this)
            listView.adapter = adapter
            listView.onItemClickListener = this
            listView.onItemLongClickListener = this
            //update()

            it.liveItems.observe(this, Observer{update()})
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

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATER
    private fun update() {
        Log.w(TAG, "UPDATE")
        if (adapter!!.count == 0) {
            listView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
            listView.visibility = View.VISIBLE
            adapter!!.notifyDataSetChanged()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // HANDLERS
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                onMenuAdd()
                true
            }
            R.id.menu_rename -> {
                onMenuRename()
                true
            }
            R.id.menu_save_json -> {
                onMenuSave()
                true
            }
            R.id.menu_save_xml -> {
                onMenuSaveXML()
                true
            }
            R.id.menu_delete -> {
                onMenuDelete()
                true
            }
            R.id.menu_help -> {
                onMenuHelp()
                true
            }
            else ->                 // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val items = list?.liveItems?.value
        if(items==null) return
        // Change item state
        val item = items[position]
        item.changeState()

        // Update row
        adapter?.updateChecked(item, view, null, null)
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        // Go to item activity
        startAItem(position)
        return true
    }

    private fun onMenuAdd() {
        // Go to item activity
        startAItem(-1)
    }

    private fun startAItem(index: Int) {
        val intent = Intent(this, AItem::class.java)
        intent.putExtra(Data.LIST_ID, list!!.id)
        intent.putExtra(Data.ITEM_INDEX, index)
        startActivityForResult(intent, ITEM_OPEN_REQUEST)
    }

    private fun onMenuHelp() {
        // Go to help activity
        val intent = Intent(this, AHelp::class.java)
        intent.putExtra(Data.ACTIVITY, "AList")
        startActivity(intent)
    }

    private fun onMenuDelete() {
        // Show yes/no dialog
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.list_delete_alert_title))
                .setMessage(getString(R.string.list_delete_alert_text))
                .setPositiveButton(android.R.string.yes) { dialog, which -> // continue with delete
                    Data.listOfLists.deleteList(list!!.id)
                    // Go out
                    setResult(Data.RESULT_DELETED)
                    finish()
                }
                .setNegativeButton(android.R.string.no) { dialog, which ->
                    // do nothing
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    private fun onMenuRename() {
        val dlg = DlgName(
                this,
                R.string.dialog_title_rename,
                R.string.main_add_hint,
                list!!.name,
                { text ->
                    if (text.isEmpty()) {
                        Toast.makeText(this@AList, R.string.dialog_error, Toast.LENGTH_SHORT).show()
                    }else {
                        // Rename List
                        list!!.rename(text)
                        // Update title
                        title = list!!.name
                        // Set flag
                        setResult(Data.RESULT_UPDATED)
                    }
                }
        )
        dlg.show()
    }

    private fun onMenuSave() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TITLE, list!!.name + ".json")
        startActivityForResult(intent, FILE_CREATE_REQUEST)
    }

    private fun onMenuSaveXML() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TITLE, list!!.name + ".xml")
        startActivityForResult(intent, FILE_CREATE_XML_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult requestCode= $requestCode, resultCode=$resultCode")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ITEM_OPEN_REQUEST) {
            //update()
        } else {
            if (resultCode != RESULT_OK) return
            if (data == null) return
            when (requestCode) {
                FILE_CREATE_REQUEST -> doSave(data.data)
                FILE_CREATE_XML_REQUEST -> doSaveXML(data.data)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DO ACTIONs
    fun doSaveXML(uri: Uri?) {
        try {
            // Open
            val pfd = contentResolver.openFileDescriptor(uri!!, "w")
            val fileOutputStream = FileOutputStream(pfd!!.fileDescriptor)

            // Write
            val bytes = list!!.toXML().toByteArray()
            fileOutputStream.write(bytes)

            // Close. Let the document provider know you're done by closing the stream.
            fileOutputStream.close()
            pfd.close()

            // Show result
            Toast.makeText(this, bytes.size.toString() + " " + getString(R.string.bytes_saved), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            DlgError(this, e.message).show()
        }
    }

    fun doSave(uri: Uri?) {
        list?.let {
            val lists = ArrayList<List>()
            lists.add(it)
            doSave(this, uri, lists)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // List ADAPTER
    private inner class ListAdapter(var ctx: Context) : BaseAdapter() {
        var lInflater: LayoutInflater
        var colorChecked: Int
        var colorUnchecked: Int

        init {
            lInflater = ctx
                    .getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            colorChecked = ContextCompat.getColor(ctx, R.color.colorChecked)
            colorUnchecked = ContextCompat.getColor(ctx, R.color.colorUnchecked)
        }

        override fun getCount(): Int {
            val items = list?.liveItems?.value
            if(items==null) return 0
            return items.size
        }

        override fun getItem(position: Int): Any {
            return list!!.liveItems.value!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // используем созданные, но не используемые view
            val view:View = if(convertView!=null)
                convertView
            else
                lInflater.inflate(R.layout.item_list, parent, false)
            // Fill View
            val item = list!!.liveItems.value!!.get(position)
            updateView(item, view)
            return view
        }

        fun updateView(item: Item, view: View) {
            //Log.d(TAG, "updateItem "+item.name);

            // Name
            val txtName = view.findViewById<View>(R.id.txtName) as TextView
            txtName.text = item.name
            // Description
            val txtDescr = view.findViewById<View>(R.id.txtDescription) as TextView
            if (item.description == null || item.description!!.isEmpty()) {
                txtDescr.visibility = View.GONE
            } else {
                txtDescr.visibility = View.VISIBLE
                txtDescr.text = item.description
            }
            // Checked / Category
            updateChecked(item, view, txtName, txtDescr)
        }

        fun updateChecked(item: Item, view: View, txtName: TextView?, txtDescr: TextView?) {
            var txtName = txtName
            var txtDescr = txtDescr
            if (txtName == null) txtName = view.findViewById<View>(R.id.txtName) as TextView
            if (txtDescr == null) txtDescr = view.findViewById<View>(R.id.txtDescription) as TextView
            if (item.state == Item.ITEM_STATE_CHECKED) {
                txtName.setTextColor(colorChecked)
                txtDescr.setTextColor(colorChecked)
            } else {
                txtName.setTextColor(colorUnchecked)
                txtDescr.setTextColor(colorUnchecked)
            }
        }
    }

    private fun save(filename: String, format: String) {
        var filename = filename
        filename = "$filename.$format"
        var file: File? = null
        var error: String? = null
        try {
            //File dir = TheApp.context().getExternalFilesDir(null); //Android/data/<package.name>/files
            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), Data.APP_FOLDER)
            if (!dir.exists()) if (!dir.mkdirs()) throw Exception("Directory not created")
            file = File(dir, filename)
            val bw = BufferedWriter(FileWriter(file))
            when (format) {
                "xml" -> list!!.saveXML(bw)
                "json" -> bw.write(list!!.toJSON().toString())
                else -> throw Exception("unknown format '$format'")
            }
            bw.close()
        } catch (e: Exception) {
            e.printStackTrace()
            error = e.message
        }

        // SHOW RESULT
        if (error == null) {
            Log.d(TAG, "save size:" + (file?.usableSpace ?: "0"))
            AlertDialog.Builder(this) //.setTitle(getString(R.string.dialog_title_save))
                    .setMessage("""
    ${getString(R.string.list_save_ok)}
    Android/Documents/
    ${Data.APP_FOLDER}/
    $filename
    """.trimIndent())
                    .setPositiveButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show()
        } else {
            Log.d(TAG, "Save error:$error")
            AlertDialog.Builder(this) //.setTitle(getString(R.string.dialog_title_save))
                    .setMessage("""
    ${getString(R.string.list_save_error)}
    $error
    """.trimIndent())
                    .setPositiveButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }
    }

    companion object {
        const val TAG = "myapp.AList"
        const val ITEM_OPEN_REQUEST = 111

        //    static final int FILE_OPEN_REQUEST      = 222;
        const val FILE_CREATE_REQUEST = 333
        const val FILE_CREATE_XML_REQUEST = 444
    }
}