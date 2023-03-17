package ru.igormayachenkov.list.data

//--------------------------------------------------------------------------------------------------
// OPEN LIST ITEMS

sealed interface ItemsState {
    object     Loading : ItemsState
    data class Error(val message:String) : ItemsState
    data class Success(val items:List<DataItem>) : ItemsState
}
