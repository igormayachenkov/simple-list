package ru.igormayachenkov.list

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val TAG = "myapp.App"

val Context.prefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

private lateinit var instance:App
val app:App get() = instance

class App : Application() {

    private val prefs       by lazy { Prefs(prefsDataStore) }
    val settingsRepository  by lazy { SettingsRepository(prefs) }
    val listRepository      by lazy { ListRepository(prefs) }
    val itemsRepository     by lazy { ItemsRepository() }
    val infoRepository      by lazy { InfoRepository() }
    val saverRepository     by lazy { SaverRepository() }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        instance = this

        // Init
        Database.open(this)
    }
    
    // PACKAGE INFO
    private fun getPackageInfo():PackageInfo? {
        try {
            return packageManager?.getPackageInfoCompat(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
    // Version
    data class Version(val code:Long, val name:String)
    val version by lazy { getAppVersion() }
    private fun getAppVersion():Version?{
        getPackageInfo()?.let{
            return Version(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) it.longVersionCode
                else it.versionCode.toLong(),
                it.versionName
            )
        }
        return null
    }
}

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }
