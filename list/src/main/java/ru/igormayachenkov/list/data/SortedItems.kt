package ru.igormayachenkov.list.data

import ru.igormayachenkov.list.FList


class SortedItems : SortedArray<Item>(){

    // SORTING
    val comparaByName = compareBy<Item> { it.name }

    override val comparator: Comparator<Item>
        get() = comparaByName

    // LIST ADAPTER
    override val adapter: IListAdapter?
        get() = FList.instance?.listChangeInterface


}