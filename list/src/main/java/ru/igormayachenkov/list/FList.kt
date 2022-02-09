package ru.igormayachenkov.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ru.igormayachenkov.list.data.OpenList
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.f_list.*
import kotlinx.android.synthetic.main.item_list.view.*
import ru.igormayachenkov.list.data.IListAdapter
import ru.igormayachenkov.list.data.Item
import ru.igormayachenkov.list.data.OpenItem
import ru.igormayachenkov.list.dialogs.DlgError
import ru.igormayachenkov.list.dialogs.DlgName

class FList : BaseFragment()  {

    //----------------------------------------------------------------------------------------------
    // STATIC
    companion object {
        const val TAG: String = "myapp.FList"
        val colorChecked  : Int = ContextCompat.getColor(App.context, R.color.colorChecked)
        val colorUnchecked: Int = ContextCompat.getColor(App.context, R.color.colorUnchecked)

        var publicInterface : FList.PublicInterface? = null
    }

    // DATA
    lateinit var uiList : List<Item>
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
        Logic.openList.observe(viewLifecycleOwner, Observer<OpenList?> { list->
            if(list!=null) show(list) else hide()
        })
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        publicInterface = null
        super.onDestroyView()
    }

    //----------------------------------------------------------------------------------------------
    // SHOW (and load) / HIDE
    fun show(list: OpenList){
        Log.d(TAG, "show #${list.id}")

        publicInterface = PublicInterface()

        // Name
        toolbar.title = list.name

        // Set list data
        uiList = list.items.asList

        // Update items
        publicInterface?.notifyDataSetChanged()

        // SHOW FRAGMENT
        showFragment()
    }

    fun hide(){
        Log.d(TAG, "hide")
        publicInterface = null
        // HIDE FRAGMENT
        hideFragment()
    }

    fun updateNoDataLabel(){
        lblEmptyList.visibility =
                if (uiList.size == 0) VISIBLE else GONE
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC INTERFACE
    inner class PublicInterface : IListAdapter {

        fun onListRenamed(){
            Log.d(TAG, "onListRenamed")
            // Update title
            toolbar.title = Logic.openList.value?.name
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
    fun onItemClick(view: View) {
        val position = recyclerView.getChildAdapterPosition(view)
        val item = uiList[position]
        Logic.toggleItemState(item, position)


        // Update row
        adapter.notifyItemChanged(position)
    }

    fun onItemLongClick(view: View): Boolean {
        val position = recyclerView.getChildAdapterPosition(view)
        Logic.setOpenItem( OpenItem(uiList[position], position) )
        return true
    }

    fun onMenuClick(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_add       -> onMenuAdd()
            R.id.menu_rename    -> onMenuRename()
            R.id.menu_delete    -> onMenuDelete()
            R.id.menu_help      -> onMenuHelp()
            R.id.menu_save_json -> Converter.saveOpenList()
            R.id.menu_save_xml  -> Converter.saveOpenListXML()
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
        DlgName.show(
            R.string.dialog_title_rename,
            R.string.main_add_hint,
            toolbar.title.toString()
        ) { text ->
            try {
                Logic.renameOpenList(text)
            } catch (e: Exception) {
                DlgError.showErrorToast(TAG, e)
            }
        }
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
                }catch (e:Exception){DlgError.showErrorToast(TAG,e)}
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
                txtDescr.visibility = GONE
            } else {
                txtDescr.visibility = VISIBLE
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

    inner class MyListAdapter :RecyclerView.Adapter<MyViewHolder>() {
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