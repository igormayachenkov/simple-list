package ru.igormayachenkov.list.data

import ru.igormayachenkov.list.app
//--------------------------------------------------------------------------------------------------
// OPEN LIST ITEMS

sealed interface ItemsState {
    object     Loading : ItemsState
    data class Error(val message:String) : ItemsState
         class Success(items:List<DataItem>) : ItemsState{
             val items:List<DataItem> = items.sortedWith(getDataItemComparator(app.settingsRepository.settings.value))
         }
}
