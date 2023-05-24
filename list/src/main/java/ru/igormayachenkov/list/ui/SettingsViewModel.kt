package ru.igormayachenkov.list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import ru.igormayachenkov.list.App
import ru.igormayachenkov.list.SettingsRepository
import ru.igormayachenkov.list.data.Settings

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel(){
    val settings = settingsRepository.settings

    fun onSave(newSettings: Settings):String?{
        viewModelScope.launch {
            settingsRepository.setSettings(newSettings)
        }
        return null
    }

    //----------------------------------------------------------------------------------------------
    // FACTORY
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                SettingsViewModel(
                    settingsRepository = app.settingsRepository
                )
            }
        }
    }

}