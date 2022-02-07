package ru.igormayachenkov.list

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ru.igormayachenkov.list.data.List
import ru.igormayachenkov.list.data.OpenList
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.f_list.*
import kotlinx.android.synthetic.main.item_list.view.*
import ru.igormayachenkov.list.data.Item

class FList : Fragment()  {

    //----------------------------------------------------------------------------------------------
    // STATIC
    companion object {
        val TAG: String = "myapp.FList"
        val colorChecked  : Int = ContextCompat.getColor(App.context, R.color.colorChecked)
        val colorUnchecked: Int = ContextCompat.getColor(App.context, R.color.colorUnchecked)

        var publicInterface : FList.PublicInterface? = null
    }

    // DATA
    var uiList = ArrayList<Item>()
    val columnCount = 1

    //----------------------------------------------------------------------------------------------
    // FRAGMENT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        retainInstance = true// TO PREVENT DESTROY ON SCREEN ROTATION !!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.f_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        publicInterface = PublicInterface()
        view.visibility = GONE // initial hidden state !!!

        // Set handlers
        toolbar.setOnMenuItemClickListener { onMenuClick(it); true}  //  https://developer.android.com/guide/fragments/appbar

        // Init List
        recyclerView.layoutManager = when {
            columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(activity)
            else -> androidx.recyclerview.widget.GridLayoutManager(activity, columnCount)
        }
        recyclerView.adapter = adapter

        // Observe open list
        Logic.openList.observe(viewLifecycleOwner, Observer<OpenList?> { load(it) })
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        publicInterface = null
        super.onDestroyView()
    }

    //----------------------------------------------------------------------------------------------
    // LOAD DATA
    fun load(list: OpenList?){
        Log.d(TAG, "load list #${list?.id}")

        view?.let { view ->
            if (list != null) {
                Log.d(TAG, "load list data name:${list.name}")

                // Name
                toolbar.title = list.name

                // Load item list
                uiList = list.items
                reloadData()

                // SHOW FRAGMENT
                view.visibility = VISIBLE
            } else {
                // HIDE FRAGMENT
                if (view.visibility == VISIBLE) {
                    Utils.hideSoftKeyboard(activity)
                    view.visibility = GONE
                }
            }
        }
    }

    private fun reloadData(){
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
    // PUBLIC INTERFACE
    inner class PublicInterface {

        fun onListRenamed(){
            // Update title
            toolbar.title = Logic.openList.value?.name
        }

        fun onItemInserted() {
            Log.w(TAG, "onItemInserted")
            reloadData()
            // TODO replace by adapter.notifyItemInserted
        }

        fun onItemUpdated(isNameChanged: Boolean, isDescrChanged: Boolean) {
            Log.w(TAG, "onItemUpdated $isNameChanged $isDescrChanged")
            if (isNameChanged)
                reloadData()
            else
                adapter.notifyDataSetChanged()
                // TODO replace by adapter.notifyItemUpdatecd
        }

        fun onItemDeleted(pos: Int) {
            Log.w(TAG, "onItemDeleted pos:$pos")
            adapter.notifyItemRemoved(pos)
        }
    }


    //----------------------------------------------------------------------------------------------
    // HANDLERS
    fun onItemClick(view: View) {
        val position = recyclerView.getChildAdapterPosition(view)
        // Change item state
        val item = uiList[position]
        item.changeState()
        // Update row
        adapter.notifyItemChanged(position)
    }

    fun onItemLongClick(view: View): Boolean {
        val position = recyclerView.getChildAdapterPosition(view)
        Logic.setOpenItem(uiList[position])
        return true
    }

    fun onMenuClick(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_add       -> onMenuAdd()
            R.id.menu_rename    -> onMenuRename()
            //R.id.menu_save_json -> onMenuSave()
            //R.id.menu_save_xml  -> onMenuSaveXML()
            R.id.menu_delete    -> onMenuDelete()
            R.id.menu_help      -> onMenuHelp()
        }
    }

    private fun onMenuAdd() {
        Logic.createItem()
    }

    private fun onMenuHelp() {
        // Go to help activity
        val intent = Intent(activity, AHelp::class.java)
        intent.putExtra(Logic.ACTIVITY, "AList")
        startActivity(intent)
    }

    private fun onMenuRename() {
        val dlg = DlgName(
            requireContext(),
            R.string.dialog_title_rename,
            R.string.main_add_hint,
            toolbar.title.toString(),
            { text ->
                try {
                    Logic.renameOpenList(text)
                }catch (e:Exception){ Utils.showError(TAG, e) }
            }
        )
        dlg.show()
    }

    private fun onMenuDelete() {
        // Show yes/no dialog
        AlertDialog.Builder(requireContext())
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


    //----------------------------------------------------------------------------------------------
    // VIEW HOLDER
    inner class MyViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtName      : TextView = view.txtName
        val txtDescr     : TextView = view.txtDescription
        init {
            view.setOnClickListener{v->onItemClick(v)}
            view.setOnLongClickListener { v->onItemLongClick(v)}
        }

        fun bind(position: Int) {
            val item = uiList[position]

            // Name
            txtName.text  = item.name

            // Description
            if (item.description.isNullOrEmpty()) {
                txtDescr.visibility = View.GONE
            } else {
                txtDescr.visibility = View.VISIBLE
                txtDescr.text = item.description
            }

            // Checked
            updateChecked(item)
        }

        fun updateChecked(item:Item){
            if (item.state == Item.ITEM_STATE_CHECKED) {
                txtName.setTextColor(colorChecked)
                txtDescr.setTextColor(colorChecked)
            } else {
                txtName.setTextColor(colorUnchecked)
                txtDescr.setTextColor(colorUnchecked)
            }
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

    }
}