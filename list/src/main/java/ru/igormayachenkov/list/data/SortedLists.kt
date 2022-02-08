package ru.igormayachenkov.list.data

class SortedLists : SortedArray<List>(){

    override fun updateSortOrder() {
        array.sortBy { it.name }
    }

    fun getPositionById(id:Long):Int?{
        array.forEachIndexed{ pos, element->
            if(element.id==id) return pos
        }
        return null
    }
}