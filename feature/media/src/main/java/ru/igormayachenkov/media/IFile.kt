package ru.igormayachenkov.media

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface IFile {
    val loadingState : StateFlow<LoadingState>
    suspend fun load(context: Context)
}