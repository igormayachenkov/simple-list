package ru.igormayachenkov.list.data

import android.util.Log

//------------------------------------------
// SORTED ARRAY

abstract class SortedArray<TYPE> {
    protected val array = ArrayList<TYPE>()

    // SORTING
    abstract val comparator : Comparator<TYPE>


    // LOADING
    fun load(elements : Collection<TYPE> ) {
        array.clear()
        array.addAll(elements)
        updateSortOrder()
    }

    fun clear(){
        array.clear()
    }

    val asList : kotlin.collections.List<TYPE>
        get() = array // sorted items

    // MODIFIERS
    fun updateSortOrder(){
        array.sortWith(comparator)
    }

    fun insert(element:TYPE):Int{
        var pos = array.binarySearch(element,comparator)
        if(pos<0)  pos = -(pos + 1)
        array.add(pos, element)
        return pos
    }

    fun update(element:TYPE, posOld:Int):Int{
        array.removeAt(posOld)
        return insert(element)
    }

    fun removeAt(pos:Int){
        array.removeAt(pos)
    }

}