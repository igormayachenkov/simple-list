package ru.igormayachenkov.list.data


class SortedItems : SortedArray<Item>(){

    val comparaByName = compareBy<Item> { it.name }

    override val comparator: Comparator<Item>
        get() = comparaByName

}