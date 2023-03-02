package ru.igormayachenkov.list.data

import kotlin.Comparator

class SortedLists : SortedArray<DataList>(){

    // SORTING
    val comparaByName = compareBy<DataList> { it.name }

    override val comparator: Comparator<DataList>
        get() = comparaByName

    fun getElementById(id:Long):DataList?{
        forEach{ element->
            if(element.id==id) return element
        }
        return null
    }
    fun getPositionById(id:Long):Int?{
        forEachIndexed{ pos, element->
            if(element.id==id) return pos
        }
        return null
    }
}