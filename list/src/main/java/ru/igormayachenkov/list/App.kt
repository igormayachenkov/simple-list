package ru.igormayachenkov.list

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
// THE APPLICATION INSTANCE
class App : Application() {
    private val TAG:String="myapp.App"

    companion object {

        // CONTEXT
        // use an Activity's Context within that Activity,
        // and the Application Context when passing a context beyond the scope of an Activity
        // to avoid memory leaks.
        private lateinit var m_context: Context
        val context: Context
            get() = m_context

        fun getString(rscId:Int):String {return context.getString(rscId)}

        // PACKAGE INFO
        val packageInfo: PackageInfo?
            get() {
                try {
                    return context.packageManager?.getPackageInfo(context.packageName, 0)
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

        // Init
        Settings
        Logic
    }

}