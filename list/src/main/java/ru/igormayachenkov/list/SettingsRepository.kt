package ru.igormayachenkov.list

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import ru.igormayachenkov.list.data.ItemsState
import ru.igormayachenkov.list.data.SavedOpenList
import ru.igormayachenkov.list.data.Settings

private const val TAG = "myapp.SettingsRepository"

class SettingsRepository(
    private val dataSource: SettingsDataSource
) {
    //----------------------------------------------------------------------------------------------
    // SETTINGS as FLOW
    private val _settings = MutableStateFlow<Settings>( dataSource.restoreSettings() )
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    //----------------------------------------------------------------------------------------------
    // MODIFIERS
    suspend fun setSettings(newSettings: Settings){
        withContext(Dispatchers.IO){
            try {
                // Start
                Log.d(TAG, "setSettings started")
                // Progress
                //delay(1000)
                dataSource.saveSettings(newSettings)
                // Success
                _settings.value = newSettings
            }catch(ex:Exception){
                // Error
                Log.e(TAG,ex.stackTraceToString())
            }
            Log.d(TAG, "setSettings finished")
        }
    }
}