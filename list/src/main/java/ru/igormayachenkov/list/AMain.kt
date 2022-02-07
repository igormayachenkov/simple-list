package ru.igormayachenkov.list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.igormayachenkov.list.data.List
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlinx.android.synthetic.main.a_main.*
import kotlinx.android.synthetic.main.a_main.emptyView
import kotlinx.android.synthetic.main.a_main.recyclerView
import kotlinx.android.synthetic.main.item_main.view.*

class AMain : AppCompatActivity() {
    companion object {
        private const val TAG = "myapp.AMain"
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

        // Init Toolbar
        //  https://developer.android.com/guide/fragments/appbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setOnMenuItemClickListener { onMenuClick(it); true }

        instance = PublicInterface()

        // List
        recyclerView.layoutManager = when {
            columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(this)
            else -> androidx.recyclerview.widget.GridLayoutManager(this, columnCount)
        }
        recyclerView.adapter = adapter

        reloadData()
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
        fun startExternalActivity(intent:Intent, requestCode:Int){
            startActivityForResult(intent,requestCode)
        }

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
                adapter.notifyItemRemoved(it)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATERs
    private fun reloadData() {
        Log.w(TAG, "RELOAD DATA")

        // Reload sorted list
        uiList.clear()
        uiList.addAll(Logic.listOfLists.values)
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
    // HANDLERS
    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        if(Logic.openItem.value!=null) {
            Logic.setOpenItem(null)
        }else if(Logic.openList.value!=null) {
            Logic.setOpenList(null)
        }else{
            super.onBackPressed()
        }
    }

    fun onMenuClick(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_add   -> onMenuAdd()
            R.id.menu_save  -> Converter.saveAll()
            R.id.menu_clear -> onMenuClear()
            R.id.menu_load  -> Converter.loadAll()
            R.id.menu_help  -> onMenuHelp()
        }
    }

    fun onListItemClick(view: View) {
        Log.d(TAG,"onListItemClick")
        val position = recyclerView.getChildAdapterPosition(view)
        val list = uiList[position]
        Logic.setOpenList(list)
        //AList.show(this)
    }

    fun onMenuAdd() {
        // ALERT DIALOG
        val dlg = DlgName(
                this,
                R.string.main_add_menu,
                R.string.main_add_hint,
                null,  // name value
                { text ->
                    try {
                        Logic.createList(text)
                    }catch (e:Exception){ Utils.showError(TAG, e) }
                }
        )
        dlg.show()
    }

    fun onMenuHelp() {
        // Go to help activity
        val intent = Intent(this, AHelp::class.java)
        intent.putExtra(Logic.ACTIVITY, "AMain")
        startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Converter.onActivityResult(requestCode,resultCode, data)
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
            // Name
            txtName.text = list.name
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