package ru.igormayachenkov.list.media

/**
 * A STATE OF A SINGLE MEDIA FILE
 */
sealed interface MediaState {
    object Empty   : MediaState
    object Loading : MediaState
    data class Success(val content:Int) : MediaState
    data class Error(val error:String) : MediaState
}