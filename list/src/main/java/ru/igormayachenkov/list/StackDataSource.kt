package ru.igormayachenkov.list

import ru.igormayachenkov.list.data.SavedOpenList

interface StackDataSource{
    suspend fun saveStack(stack: List<SavedOpenList>)
    fun restoreStack():List<SavedOpenList>
}