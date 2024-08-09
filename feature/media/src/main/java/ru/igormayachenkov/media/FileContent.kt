package ru.igormayachenkov.media

import android.graphics.Bitmap

// LOADED FILE CONTENT
sealed interface FileContent {
    data class Binary(val size:Int)     :FileContent
    data class Image(val bitmap:Bitmap) :FileContent

}