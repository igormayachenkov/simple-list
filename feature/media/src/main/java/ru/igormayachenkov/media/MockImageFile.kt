package ru.igormayachenkov.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockImageFile(
    bitmap:Bitmap
) : IFile {
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Success(FileContent.Image(bitmap)))
    override val loadingState = _loadingState.asStateFlow()

    override suspend fun load(context: Context){}
}