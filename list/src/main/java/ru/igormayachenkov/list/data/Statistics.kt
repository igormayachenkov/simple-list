package ru.igormayachenkov.list.data

sealed interface Statistics{
    object Loading : Statistics
    data class Error(val message:String) : Statistics
    data class Success(val nLists:Int, val nItems:Int) : Statistics
}