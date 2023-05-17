package ru.igormayachenkov.list

import ru.igormayachenkov.list.data.Settings

interface SettingsDataSource {
    suspend fun saveSettings(settings: Settings)
    fun restoreSettings():Settings
}