package ru.igormayachenkov.media

import java.io.File

class BinaryFile(override val file: File) : AbstractFile {
    var buffer : ByteArray? = null
    override val isLoaded: Boolean
        get() = buffer!=null
}