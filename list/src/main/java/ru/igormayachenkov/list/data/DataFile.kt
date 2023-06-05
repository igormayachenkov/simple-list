package ru.igormayachenkov.list.data

data class DataFile(
    val version : String,
    val nBytes  : Int,
    val itemList: List<DataItem>
)
