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
import ru.igormayachenkov.list.data.Version

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

    lateinit var version:Version
        private set
    var prevVersion:Version?=null
        private set
    var fatalError:Exception?=null
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        //try {

            // Read and remember Version info
            version = getAppVersion()
            prevVersion = prefs.readWriteVersion(version)

            Log.d(TAG, "onCreate version: $version  prevVersion: $prevVersion")

            // Open database
            Database.open(this)
            if (Database.isCreated)
                fillMockData()

//        }catch (e:Exception){
//            fatalError = e
//            Log.e(TAG, "FATAL ERROR: ${e.message}")
//            e.printStackTrace()
//        }
    }

    override fun onTerminate() { // will never call!
        Log.d(TAG, "onTerminate")
        Database.close()
        super.onTerminate()
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

    private fun getAppVersion(): Version{
        getPackageInfo()?.let{
//            code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) it.longVersionCode
//            else it.versionCode.toLong(),
            return Version.fromString(it.versionName)
        }
        return Version()
    }

}

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }

