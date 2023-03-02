package ru.igormayachenkov.list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import ru.igormayachenkov.list.data.List
import androidx.recyclerview.widget.RecyclerView
import ru.igormayachenkov.list.dialogs.DlgError
import ru.igormayachenkov.list.dialogs.DlgName
import ru.igormayachenkov.list.settings.FSettings

class AMain : AppCompatActivity() {
    companion object {
        private const val TAG = "myapp.AMain"
        var instance : AMain? = null
    }
    // Controls
    private val recyclerView:RecyclerView by lazy { findViewById(R.id.recyclerView) }
    private val lblEmptyList:View by lazy{ findViewById(R.id.lblEmptyList)}

    // Data objects
    private val uiList : kotlin.collections.List<List>
       get() = Logic.listOfLists

    val columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)

        // Init Toolbar
        //  https://developer.android.com/guide/fragments/appbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setOnMenuItemClickListener { onMenuClick(it); true }

        instance = this

        // List
        recyclerView.layoutManager = when {
            columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(this)
            else -> androidx.recyclerview.widget.GridLayoutManager(this, columnCount)
        }
        recyclerView.adapter = adapter

        // Load
        notifyDataSetChanged()

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
    // LOAD
    fun updateNoDataLabel(){
        lblEmptyList.visibility =
                if (uiList.size == 0) VISIBLE else GONE
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC INTERFACE
    fun startExternalActivity(intent:Intent, requestCode:Int){
        startActivityForResult(intent,requestCode)
    }

    // DIALOGS - BACKSTACK FRAGMENTS
    fun showDialog(fragment: Fragment, tag:String){
        with(supportFragmentManager.beginTransaction()){
            //setCustomAnimations(R.anim.fade_in,0,0, R.anim.fade_out)
            setCustomAnimations(R.anim.fade_in,0,0, R.anim.fade_out)
            add(R.id.dialogContainer, fragment,tag)
            addToBackStack(null)
            commit()
        }
    }
    fun hideDialog(){
        supportFragmentManager.popBackStack()
    }

    fun notifyDataSetChanged() {
        Log.d(TAG, "notifyDataSetChanged")
        adapter.notifyDataSetChanged()
        updateNoDataLabel()
    }

    fun notifyItemInserted(pos: Int) {
        Log.d(TAG, "notifyItemInserted pos:$pos")
        adapter.notifyItemInserted(pos)
        updateNoDataLabel()
    }

    fun notifyItemChanged(pos: Int) {
        Log.d(TAG, "notifyItemChanged pos:$pos")
        adapter.notifyItemChanged(pos)
    }

    fun notifyItemRemoved(pos: Int) {
        Log.d(TAG, "notifyItemRemoved pos:$pos")
        adapter.notifyItemRemoved(pos)
        updateNoDataLabel()
    }

    //----------------------------------------------------------------------------------------------
    // HANDLERS
    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        if(supportFragmentManager.backStackEntryCount>0){
            supportFragmentManager.popBackStack()
        }else if(Logic.openItem!=null) {
            Logic.clearOpenItem()
        }else if(Logic.openList!=null) {
            Logic.clearOpenList()
        }else{
            super.onBackPressed()
        }
    }

    fun onMenuClick(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_add       -> onMenuAdd()
            R.id.menu_save      -> Converter.saveAll()
            R.id.menu_clear     -> onMenuClear()
            R.id.menu_load      -> Converter.loadAll()
            R.id.menu_help      -> onMenuHelp()
            R.id.menu_settings  -> FSettings.show()
        }
    }

    fun onListItemClick(view: View) {
        Log.d(TAG,"onListItemClick")
        val position = recyclerView.getChildAdapterPosition(view)
        Logic.setOpenList(uiList[position])
    }

    fun onMenuAdd() {
        // Name DIALOG
        DlgName.show(
            R.string.main_add_menu,
            R.string.main_add_hint,
            null  // name value
        )
        // the last parameter because of kotlin style: https://stackoverflow.com/questions/53375316/lambda-argument-should-be-moved-out-of-parentheses
        { text ->
            try {
                Logic.createList(text)
            } catch (e: Exception) {
                DlgError.showErrorToast(TAG, e)
            }
        }

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
            }catch (e:Exception){ DlgError.showErrorToast(TAG, e) }
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
        val txtName : TextView = view.findViewById(R.id.txtName)
        init {
            view.setOnClickListener{v->onListItemClick(v)}
        }

        fun bind(position: Int) {
            //Log.d(TAG,"--- bind --- $position")
            val list = uiList[position]
            // Name
            txtName.text = list.name
        }
    }

    //----------------------------------------------------------------------------------------------
    // LIST ADAPTER
    val adapter = MyListAdapter()

    inner class MyListAdapter :RecyclerView.Adapter<MyViewHolder>() {
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