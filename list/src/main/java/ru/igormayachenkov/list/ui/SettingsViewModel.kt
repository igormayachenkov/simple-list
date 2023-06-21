package ru.igormayachenkov.list.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ru.igormayachenkov.list.app
import ru.igormayachenkov.list.data.Settings

class SettingsViewModel() : ViewModel(){
    private val settingsRepository = app.settingsRepository
    val settings = app.settingsRepository.settings
    // Screen visibility
    var isVisible by mutableStateOf(false)
        private set

    //----------------------------------------------------------------------------------------------
    // EVENTS
    fun onSave(newSettings: Settings){
        settingsRepository.setSettings(newSettings)
    }
    fun showSettings(){isVisible=true}
    fun hideSettings(){isVisible=false}
}