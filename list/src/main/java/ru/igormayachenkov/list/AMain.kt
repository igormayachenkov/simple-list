package ru.igormayachenkov.list

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.json.JSONException
import org.json.JSONObject
import ru.igormayachenkov.list.data.DList
import ru.igormayachenkov.list.data.Data
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*
import kotlinx.android.synthetic.main.a_main.*

class AMain : AppCompatActivity(), OnItemClickListener {
    // Data objects
    private val sortedList = ArrayList<DList>()
    private val comparatorName = Comparator<DList> { a, b -> a.name.compareTo(b.name) }

    // Adapter
    private var adapter: ListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.main_title)

        // List
        adapter = ListAdapter(this.layoutInflater)
        listView.adapter = adapter
        listView.onItemClickListener = this

        //updateScheduled=false;
        update()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()

//        if(updateScheduled) {
//            listAdapter.notifyDataSetChanged();
//            updateScheduled = false;
//        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATER
    private fun update() {
        // Reload sorted list
        sortedList.clear()
        sortedList.addAll(Data.listOfLists.aLL)
        Collections.sort(sortedList, comparatorName)

        // Update controls
        if (adapter!!.count == 0) {
            listView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
            listView.visibility = View.VISIBLE
            adapter?.notifyDataSetChanged()
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
            R.id.menu_save -> {
                onMenuSave()
                true
            }
            R.id.menu_clear -> {
                onMenuClear()
                true
            }
            R.id.menu_load -> {
                onMenuLoad()
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

    private fun startAList(list: DList) {
        val intent = Intent(this@AMain, AList::class.java)
        intent.putExtra(Data.LIST_ID, list.id)
        startActivityForResult(intent, LIST_OPEN_REQUEST)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        //Toast.makeText(this, "onItemClick", Toast.LENGTH_SHORT).show();
        // Go to list activity
        startAList(adapter!!.getItem(position) as DList)
    }

    fun onMenuAdd() {
        // ALERT DIALOG
        val dlg = DlgName(
                this,
                R.string.main_add_menu,
                R.string.main_add_hint,
                null,  // name value
                { text ->
                    if (text.isEmpty()) {
                        Toast.makeText(this@AMain, R.string.dialog_error, Toast.LENGTH_SHORT).show()
                    }else {
                        // Create a new list object
                        val list = DList(System.currentTimeMillis(), text)
                        // Add new list
                        Data.listOfLists.addList(list)
                        // Go to list activity
                        startAList(list)
                    }
                }
        )
        dlg.show()
    }

    fun onMenuHelp() {
        // Go to help activity
        val intent = Intent(this, AHelp::class.java)
        intent.putExtra(Data.ACTIVITY, "AMain")
        startActivity(intent)
    }

    fun onMenuClear() {
        // SHOW DIALOG
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.main_clear_message)
        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok) { dialog, which -> doClear() }
        builder.setNegativeButton(android.R.string.cancel, null)
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

//        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
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
        if (requestCode == LIST_OPEN_REQUEST) {
            update()
        } else {
            if (resultCode != RESULT_OK) return
            if (data == null) return
            when (requestCode) {
                FILE_OPEN_REQUEST -> doLoad(data.data)
                FILE_CREATE_REQUEST -> doSave(this,
                        data.data,
                        Data.listOfLists.aLL // all lists
                )
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DO ACTIONs
    fun doClear() {
        Data.clearALL()
        update()
    }

    fun doLoad(uri: Uri?) {
        try {
            // Open
            val pfd = contentResolver.openFileDescriptor(uri!!, "r")
            val reader = BufferedReader(FileReader(pfd!!.fileDescriptor))

            // Read
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            val text = sb.toString()

            // Close. Let the document provider know you're done by closing the stream.
            reader.close()
            pfd.close()

            // Parce & update data
            val json = JSONObject(text)
            doLoad(json)
        } catch (e: Exception) {
            e.printStackTrace()
            DlgError(this, e.message).show()
        }
    }

    @Throws(JSONException::class)
    fun doLoad(json: JSONObject?) {
        // Estimate loading
        val estimation = Data.estimateLoadingFromJSON(json!!)
        val message = """${getString(R.string.load_to_insert)} ${estimation.toInsert}
${getString(R.string.load_to_update)} ${estimation.toUpdate}"""
        // Ask user for request
        val dlg = DlgCommon(this, R.string.load_title, 0, {
            try {
                // Update data
                Data.loadFromJSON(json)
                // Update UI
                update()
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // List ADAPTER
    inner class ListAdapter(val inflater:LayoutInflater) : BaseAdapter() {
        override fun getCount(): Int {
            return sortedList.size
        }

        override fun getItem(position: Int): Any {
            return sortedList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // используем созданные, но не используемые view
            val view:View = if(convertView!=null)
                convertView
            else
                inflater.inflate(R.layout.item_main, parent, false)

            // UPDATE View
            val list = sortedList[position]
            // Name
            (view.findViewById<View>(R.id.txtName) as TextView).text = list.name
            return view
        }

    }

    companion object {
        const val TAG = "myapp.AMain"
        const val LIST_OPEN_REQUEST = 111
        const val FILE_OPEN_REQUEST = 222
        const val FILE_CREATE_REQUEST = 333
        @JvmStatic
        fun doSave(context: Context, uri: Uri?, lists: Collection<DList>) {
            try {
                // Open
                val pfd = context.contentResolver.openFileDescriptor(uri!!, "w")
                val fileOutputStream = FileOutputStream(pfd!!.fileDescriptor)

                // Write
                val bytes = Data.toJSON(lists).toString().toByteArray()
                fileOutputStream.write(bytes)

                // Close. Let the document provider know you're done by closing the stream.
                fileOutputStream.close()
                pfd.close()

                // Show result
                Toast.makeText(context, bytes.size.toString() + " " + context.getString(R.string.bytes_saved), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                DlgError(context, e.message).show()
            }
        }
    }
}