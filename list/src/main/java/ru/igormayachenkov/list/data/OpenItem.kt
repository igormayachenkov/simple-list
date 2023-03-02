package ru.igormayachenkov.list.data

//--------------------------------
//  LIST ITEM + POSITION

data class OpenItem (
    val item    : DataItem,
    val pos     : Int?  // Null for new items. We can have it here : pos changing only after OpenItem closed
){
    var changes : ItemChanges?=null
}