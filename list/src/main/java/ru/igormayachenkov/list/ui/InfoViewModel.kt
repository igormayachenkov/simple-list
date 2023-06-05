package ru.igormayachenkov.list.ui

import androidx.lifecycle.*
import ru.igormayachenkov.list.app

class InfoViewModel : ViewModel() {
    private val infoRepository = app.infoRepository

    // DATA
    val state = infoRepository.state

    //----------------------------------------------------------------------------------------------
    // EVENTS
    fun onClose() {
        infoRepository.reset()
    }

    fun onSaveAll() {
        MainActivity.resultAPI?.let {
            it.saveAll.launch("Simple list.json")
        }
    }

    fun onDeleteAll() {
    }

        fun onLoadAll() {
        MainActivity.resultAPI?.let {
            it.loadAll.launch(arrayOf("application/json"))
        }
    }
}