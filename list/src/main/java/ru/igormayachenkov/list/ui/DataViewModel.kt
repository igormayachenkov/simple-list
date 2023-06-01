package ru.igormayachenkov.list.ui

import androidx.lifecycle.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.igormayachenkov.list.App
import ru.igormayachenkov.list.data.Statistics

class DataViewModel() : ViewModel() {
    private val statisticRepository = App.statisticRepository

    val statistics : StateFlow<Statistics> = statisticRepository.state

    fun calculateStatistics(){
        viewModelScope.launch {
            statisticRepository.calculate()
        }
    }

    fun save(){
        MainActivity.resultAPI?.let {
            it.saveAll.launch("Simple list.json")
        }
    }

}