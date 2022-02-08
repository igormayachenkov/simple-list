package ru.igormayachenkov.list.data

//--------------------------------
//  LIST ITEM + POSITION

data class OpenItem (
    val item : Item,
    val pos  : Int?  // null for new items
)