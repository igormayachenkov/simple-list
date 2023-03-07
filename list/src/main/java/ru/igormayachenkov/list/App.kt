package ru.igormayachenkov.list

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log

private const val TAG = "myapp.App"

////////////////////////////////////////////////////////////////////////////////////////////////////
// THE APPLICATION INSTANCE
class App : Application() {

    companion object {

        // CONTEXT
        // use an Activity's Context within that Activity,
        // and the Application Context when passing a context beyond the scope of an Activity
        // to avoid memory leaks.
//        private lateinit var m_context: Context
//        val context: Context
//            get() = m_context

        lateinit var instance:App
            private set

        val context: Context
            get() = instance


        fun getString(rscId:Int):String {return instance.getString(rscId)}

        // PACKAGE INFO
        val packageInfo: PackageInfo?
            get() {
                try {
                    return instance.packageManager?.getPackageInfo(instance.packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                return null
            }
    }

    val listRepository = ListRepository()

    override fun onCreate() {
        instance = this
        Log.d(TAG, "onCreate")

        super.onCreate()

        // Init
        //Settings
        listRepository.open(this)
        //Logic
    }

}