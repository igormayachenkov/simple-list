package ru.igormayachenkov.list.data

data class DataInfo(
    val nLists:Int,
    val nItems:Int
){
    val isEmpty:Boolean get() = (nLists==0 && nItems==0)
}
