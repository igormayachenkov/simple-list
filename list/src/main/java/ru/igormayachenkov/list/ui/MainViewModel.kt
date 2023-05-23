package ru.igormayachenkov.list.ui

import androidx.compose.runtime.*
import androidx.lifecycle.*

class MainViewModel : ViewModel(){
    // Data Screen
    var isDataVisible by mutableStateOf(false)
        private set
    fun showData(){isDataVisible=true}
    fun hideData(){isDataVisible=false}

    // Settings Screen
    var isSettingsVisible by mutableStateOf(false)
        private set
    fun showSettings(){isSettingsVisible=true}
    fun hideSettings(){isSettingsVisible=false}
}