package ru.igormayachenkov.media

import android.graphics.Bitmap
import java.io.File

class ImageFile(override val file: File) : AbstractFile {
    var bitmap : Bitmap? = null
    override val isLoaded: Boolean
        get() = bitmap!=null
}