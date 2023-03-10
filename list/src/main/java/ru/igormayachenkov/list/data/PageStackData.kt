package ru.igormayachenkov.list.data

import androidx.compose.foundation.lazy.LazyListState

data class PageStackData(
    val id:Long,
    val lazyListState:LazyListState
)
