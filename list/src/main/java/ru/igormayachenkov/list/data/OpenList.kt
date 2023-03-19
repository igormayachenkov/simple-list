package ru.igormayachenkov.list.data

import androidx.compose.foundation.lazy.LazyListState

data class OpenList(
    val list          : DataItem,
    val lazyListState : LazyListState = LazyListState()
)
