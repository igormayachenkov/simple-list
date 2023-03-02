package ru.igormayachenkov.list.data


class SortedItems : SortedArray<DataItem>(){

    // SORTING
    val comparaByName = compareBy<DataItem> { it.name }

    override val comparator: Comparator<DataItem>
        get() = comparaByName
}