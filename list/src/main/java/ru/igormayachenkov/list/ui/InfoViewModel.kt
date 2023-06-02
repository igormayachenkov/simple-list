package ru.igormayachenkov.list.ui

import androidx.lifecycle.*
import ru.igormayachenkov.list.App

class InfoViewModel : ViewModel() {
    private val infoRepository = App.infoRepository

    // DATA
    val state = infoRepository.state

    //----------------------------------------------------------------------------------------------
    // EVENTS
    fun onClose(){
        infoRepository.reset()
    }

    fun onSaveAll(){
        MainActivity.resultAPI?.let {
            it.saveAll.launch("Simple list.json")
        }
    }
}