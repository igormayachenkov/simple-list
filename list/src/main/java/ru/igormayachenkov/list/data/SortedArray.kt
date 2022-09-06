package ru.igormayachenkov.list.data

//----------------------------------------------------------------
// LIST DATA (SortedArray) + APPROPRIATE LIST VIEW (IListAdapter)

abstract class SortedArray<TYPE> {
    protected val array = ArrayList<TYPE>()

    // LINK to APPROPRIATE LIST VIEW
    abstract val adapter : IListAdapter?

    // SORTING
    abstract val comparator : Comparator<TYPE>

    // LOADING
    fun load(elements : Collection<TYPE> ) {
        array.clear()
        array.addAll(elements)
        //updateSortOrder()
        array.sortWith(comparator)
    }

    fun clear(){
        array.clear()
        // Update UI
        adapter?.notifyDataSetChanged()
    }

    val asList : kotlin.collections.List<TYPE>
        get() = array // sorted items

    // MODIFIERS
    fun updateSortOrder(){
        array.sortWith(comparator)
        // Update UI
        adapter?.notifyDataSetChanged()
    }

    fun insert(element:TYPE):Int{
        val pos = doInsert(element)
        // Update UI
        adapter?.notifyItemInserted(pos)
        return pos
    }
    private fun doInsert(element:TYPE):Int{
        var pos = array.binarySearch(element,comparator)
        if(pos<0)  pos = -(pos + 1)
        array.add(pos, element)
        return pos
    }
    fun update(element:TYPE, posOld:Int):Int{
        array.removeAt(posOld)
        val posNew = doInsert(element)
        // Update UI
        if (posNew==posOld) adapter?.notifyItemChanged(posNew)
        else                adapter?.notifyDataSetChanged()
        return posNew
    }

    fun removeAt(pos:Int){
        array.removeAt(pos)
        // Update UI
        adapter?.notifyItemRemoved(pos)
    }

}