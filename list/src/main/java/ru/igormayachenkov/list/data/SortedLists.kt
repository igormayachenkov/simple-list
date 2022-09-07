package ru.igormayachenkov.list.data

import ru.igormayachenkov.list.AMain
import kotlin.Comparator

class SortedLists : SortedArray<List>(){

    // SORTING
    val comparaByName = compareBy<List> { it.name }

    override val comparator: Comparator<List>
        get() = comparaByName

    fun getElementById(id:Long):List?{
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