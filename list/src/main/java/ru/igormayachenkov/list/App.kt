package ru.igormayachenkov.list

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import ru.igormayachenkov.list.settings.Settings

////////////////////////////////////////////////////////////////////////////////////////////////////
// THE APPLICATION INSTANCE
class App : Application() {
    companion object{
        private val TAG:String="myapp.App"

        // CONTEXT
        private lateinit var m_context: Context
        val context:Context
            get() = m_context

        // PACKAGE INFO
        val packageInfo: PackageInfo?
            get() {
                val pInfo: PackageInfo? = null
                try {
                    return m_context.packageManager.getPackageInfo(m_context.packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                return null
            }

    }

    override fun onCreate() {
        m_context = this
        Log.d(TAG, "onCreate")
        super.onCreate()

        // Init object
        Settings
        Logic
    }


}