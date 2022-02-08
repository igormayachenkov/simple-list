package ru.igormayachenkov.list.data

import java.util.*
import kotlin.Comparator

class SortedLists : SortedArray<List>(){

    //private val comparatorName = Comparator<List> { a, b -> a.name.compareTo(b.name) }

    override fun updateSortOrder() {
        array.sortBy { it.name }
        //Collections.sort(array, comparatorName)
    }

    fun getElementById(id:Long):List?{
        array.forEach{ element->
            if(element.id==id) return element
        }
        return null
    }
    fun getPositionById(id:Long):Int?{
        array.forEachIndexed{ pos, element->
            if(element.id==id) return pos
        }
        return null
    }
}