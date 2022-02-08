package ru.igormayachenkov.list.data

import ru.igormayachenkov.list.AMain
import java.util.*
import kotlin.Comparator

class SortedLists : SortedArray<List>(){

    // SORTING
    val comparaByName = compareBy<List> { it.name }

    override val comparator: Comparator<List>
        get() = comparaByName

    // LIST ADAPTER
    override val adapter: IListAdapter?
        get() = null//AMain.instance

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