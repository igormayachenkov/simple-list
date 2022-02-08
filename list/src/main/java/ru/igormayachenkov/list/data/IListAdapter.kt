package ru.igormayachenkov.list.data

interface IListAdapter {
    fun notifyDataSetChanged()
    fun notifyItemInserted(pos:Int)
    fun notifyItemChanged(pos:Int)
    fun notifyItemRemoved(pos: Int)
}