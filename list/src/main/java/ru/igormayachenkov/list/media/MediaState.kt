package ru.igormayachenkov.list.media

sealed interface MediaState {
    object Empty   : MediaState
    object Loading : MediaState
    data class Success(val content:Int) : MediaState
    data class Error(val error:String) : MediaState
}