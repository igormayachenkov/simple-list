package ru.igormayachenkov.list.ui

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.*
import ru.igormayachenkov.list.app
import java.util.Date

class InfoViewModel : ViewModel() {
    private val infoRepository = app.infoRepository
    private val saverRepository = app.saverRepository

    // DATA
    val state = infoRepository.state

    //----------------------------------------------------------------------------------------------
    // EVENTS
    fun onClose() {
        infoRepository.reset()
    }

    fun onSaveAll() {
        MainActivity.resultAPI?.let {
            val f = SimpleDateFormat("yy-MM-dd")
            it.saveAll.launch("List ${f.format(Date())}.json")
        }
    }

    fun onDeleteAll() {
        saverRepository.deleteAll()
    }

    fun onLoadAll() {
        MainActivity.resultAPI?.let {
            it.loadAll.launch(arrayOf("*/*")) // "application/json" blocks input on old devices
        }
    }
}