package ru.igormayachenkov.media

/**
 * A STATE OF A SINGLE MEDIA FILE
 */
sealed interface LoadingState {
    object Unloaded                     : LoadingState
    object Loading                      : LoadingState
    object Loaded                       : LoadingState
    data class Error(val error:String)  : LoadingState
}