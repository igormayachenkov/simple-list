package ru.igormayachenkov.list

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
// THE APPLICATION INSTANCE
class App : Application() {
    private val TAG:String="myapp.App"

    companion object {

        // CONTEXT
        private lateinit var m_context: Context
        val context: Context
            get() = m_context

        fun getString(rscId:Int):String {return context.getString(rscId)}

        private var instance: App? = null
        @JvmStatic
        fun instance(): App? {
            return instance
        }


    }

    override fun onCreate() {
        m_context = this
        Log.d(TAG, "onCreate")

        super.onCreate()
        instance = this

        // Init Logic
        Logic
    }

    val packageInfo: PackageInfo?
        get() {
            val pInfo: PackageInfo? = null
            try {
                return packageManager.getPackageInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return null
        }
}