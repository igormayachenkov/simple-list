package ru.igormayachenkov.list.data

// SORTED ARRAY

abstract class SortedArray<TYPE> {
    protected val array = ArrayList<TYPE>()

    fun load(elements : Collection<TYPE> ) {
        array.addAll(elements)
        updateSortOrder()
    }

    val asList : kotlin.collections.List<TYPE>
        get() = array // sorted items

    // MODIFIERS
    abstract fun updateSortOrder()

    fun insert(element:TYPE){
        array.add(element)
        updateSortOrder()
    }

    fun removeAt(pos:Int){
        array.removeAt(pos)
    }

}