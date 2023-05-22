package ru.igormayachenkov.list

import androidx.compose.runtime.*
import androidx.lifecycle.*
import ru.igormayachenkov.list.ui.MainActivity

class DataViewModel() : ViewModel() {
    var isVisible by mutableStateOf(false)
        private set

    fun show(){isVisible=true}
    fun hide(){isVisible=false}
    fun save(){
        MainActivity.archivator?.let {
            it.createDoc.launch("Simple list.json")
        }
    }

}