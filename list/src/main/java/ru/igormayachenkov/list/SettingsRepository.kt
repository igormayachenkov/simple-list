package ru.igormayachenkov.list

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.igormayachenkov.list.data.Settings
import ru.igormayachenkov.list.data.SortOrder

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
    fun setSettings(newSettings: Settings){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Start
                Log.d(TAG, "setSettings started $newSettings")
                // Progress
                //delay(1000)
                dataSource.saveSettings(newSettings)
                // Success
                _settings.value = newSettings
            }catch(ex:Exception){
                // Error
                Log.e(TAG,ex.stackTraceToString())
            }
            Log.d(TAG, "setSettings finished $newSettings")
        }
    }
    fun setSortOrder(sortOrder: SortOrder){
        Log.d(TAG, "setSortOrder $sortOrder")
        setSettings(_settings.value.copy(sortOrder = sortOrder))
    }
}