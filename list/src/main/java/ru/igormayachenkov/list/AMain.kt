package ru.igormayachenkov.list

import android.app.AlertDialog
import android.content.Context
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
import kotlinx.android.synthetic.main.a_main.*
import kotlinx.android.synthetic.main.item_main.view.*
import ru.igormayachenkov.list.data.IListAdapter
import ru.igormayachenkov.list.dialogs.DlgError
import ru.igormayachenkov.list.dialogs.DlgName

class AMain : AppCompatActivity() {
    companion object {
        private const val TAG = "myapp.AMain"
        var publicInterface : PublicInterface? = null
        var context : Context? = null
    }

    // Data objects
    private lateinit var uiList : kotlin.collections.List<List>

    val columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)

        // Init Toolbar
        //  https://developer.android.com/guide/fragments/appbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setOnMenuItemClickListener { onMenuClick(it); true }

        context = this
        publicInterface = PublicInterface()

        // List
        recyclerView.layoutManager = when {
            columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(this)
            else -> androidx.recyclerview.widget.GridLayoutManager(this, columnCount)
        }
        recyclerView.adapter = adapter

        // Set link to sorted list
        uiList = Logic.listOfLists.asList

        // Update list
        publicInterface?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        publicInterface = null
        context = null
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
    inner class PublicInterface : IListAdapter{
        fun startExternalActivity(intent:Intent, requestCode:Int){
            startActivityForResult(intent,requestCode)
        }

        fun showDialog(fragment: Fragment, tag:String){
            with(supportFragmentManager.beginTransaction()){
                //setCustomAnimations(R.anim.fade_in,0,0, R.anim.fade_out)
                setCustomAnimations(R.anim.enter_from_right,0,0, R.anim.exit_to_right)
                add(dialogContainer.id, fragment,tag)
                addToBackStack(null)
                commit()
            }
        }
        fun hideDialog(){
            supportFragmentManager.popBackStack()
        }

        override fun notifyDataSetChanged() {
            Log.d(TAG, "notifyDataSetChanged")
            adapter.notifyDataSetChanged()
            updateNoDataLabel()
        }

        override fun notifyItemInserted(pos: Int) {
            Log.d(TAG, "notifyItemInserted pos:$pos")
            adapter.notifyItemInserted(pos)
            updateNoDataLabel()
        }

        override fun notifyItemChanged(pos: Int) {
            Log.d(TAG, "notifyItemChanged pos:$pos")
            adapter.notifyItemChanged(pos)
        }

        override fun notifyItemRemoved(pos: Int) {
            Log.d(TAG, "notifyItemRemoved pos:$pos")
            adapter.notifyItemRemoved(pos)
            updateNoDataLabel()
        }
    }

    //----------------------------------------------------------------------------------------------
    // HANDLERS
    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        if(supportFragmentManager.backStackEntryCount>0){
            supportFragmentManager.popBackStack()
        }else if(Logic.openItem.value!=null) {
            Logic.clearOpenItem()
        }else if(Logic.openList.value!=null) {
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