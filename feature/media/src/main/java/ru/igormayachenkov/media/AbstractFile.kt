package ru.igormayachenkov.media

import java.io.File

/**
 * FILE = java.io.File + content (loaded or not)
 */
sealed interface AbstractFile {
    val file     : File
    val isLoaded : Boolean
}