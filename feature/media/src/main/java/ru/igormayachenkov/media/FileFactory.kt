package ru.igormayachenkov.media

import android.graphics.BitmapFactory
import java.io.File

object FileFactory{
    private const val TAG = "myapp.FileFactory"

    fun createFile(file: File):AbstractFile{
        val ext = file.name.substringAfterLast('.')
        return when(ext){
            in setOf("png","webp") -> ImageFile(file)
            else  -> BinaryFile(file)
        }
    }

    fun loadFile(abstractFile: AbstractFile){
        when(abstractFile){
            is BinaryFile -> {
                abstractFile.buffer = abstractFile.file.readBytes()
            }
            is ImageFile -> {
                abstractFile.bitmap = BitmapFactory.decodeFile(abstractFile.file.path)
            }
        }
    }
}