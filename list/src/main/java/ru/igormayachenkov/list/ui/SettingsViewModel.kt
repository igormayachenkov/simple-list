package ru.igormayachenkov.list.ui

import androidx.lifecycle.ViewModel
import ru.igormayachenkov.list.app
import ru.igormayachenkov.list.data.Settings

class SettingsViewModel() : ViewModel(){
    private val settingsRepository = app.settingsRepository
    val settings = app.settingsRepository.settings

    fun onSave(newSettings: Settings){
        settingsRepository.setSettings(newSettings)
    }
}