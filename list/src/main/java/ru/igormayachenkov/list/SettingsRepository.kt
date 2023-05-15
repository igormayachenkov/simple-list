package ru.igormayachenkov.list

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.igormayachenkov.list.data.Settings

class SettingsRepository {
    //----------------------------------------------------------------------------------------------
    // SETTINGS as FLOW
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    //----------------------------------------------------------------------------------------------
    // MODIFIERS

}