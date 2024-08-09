package ru.igormayachenkov.media

/**
 * A STATE OF A SINGLE MEDIA FILE
 */
sealed interface LoadingState {
    object Unloaded                     : LoadingState
    object Loading                      : LoadingState
    data class Success(val content:FileContent) : LoadingState
    data class Error(val error:String)  : LoadingState
}