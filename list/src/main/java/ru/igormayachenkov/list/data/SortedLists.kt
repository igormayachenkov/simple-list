package ru.igormayachenkov.list.data

import java.util.*
import kotlin.Comparator

class SortedLists : SortedArray<List>(){

    val comparaByName = compareBy<List> { it.name }

    override val comparator: Comparator<List>
        get() = comparaByName

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