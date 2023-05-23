package ru.igormayachenkov.list.ui

import androidx.lifecycle.*

class DataViewModel() : ViewModel() {
    fun save(){
        MainActivity.archivator?.let {
            it.createDoc.launch("Simple list.json")
        }
    }

}