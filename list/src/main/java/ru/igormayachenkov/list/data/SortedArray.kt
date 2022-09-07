package ru.igormayachenkov.list.data

//----------------------------------------------------------------
// LIST DATA (SortedArray)

abstract class SortedArray<TYPE> : ArrayList<TYPE>(){

    // SORTING
    abstract val comparator : Comparator<TYPE>

    // LOADING
    fun load(elements : Collection<TYPE> ) {
        clear()
        addAll(elements)
        updateSortOrder()
    }

    fun updateSortOrder(){
        sortWith(comparator)
    }

    // MODIFIERS
    fun insert(element:TYPE):Int{
        return doInsert(element)
    }
    private fun doInsert(element:TYPE):Int{
        var pos = binarySearch(element,comparator)
        if(pos<0)  pos = -(pos + 1)
        add(pos, element)
        return pos
    }

    fun update(element:TYPE, posOld:Int):Int{
        removeAt(posOld)
        return doInsert(element)
    }

}