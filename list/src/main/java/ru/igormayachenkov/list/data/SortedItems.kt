package ru.igormayachenkov.list.data

class SortedItems : SortedArray<Item>(){

    override fun updateSortOrder() {
        array.sortBy { it.name }
    }
}