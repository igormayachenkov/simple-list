package ru.igormayachenkov.list

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class ASettings : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener  {
        companion object {
            private const val TAG = "myapp.FSettings"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sp: SharedPreferences?, key: String?) {
            Log.d(TAG,"onSharedPreferenceChanged key:$key")
            if(key=="item_ui"){
                val v = sp!!.getString(key,null)
                Log.d(TAG,"value: $v")

//
//                val builder = AlertDialog.Builder(context!!)
//                val dlg = builder
//                        .setCancelable(false)
//                        .setTitle("Warning")
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setMessage("The same action type")
//                        .setPositiveButton("Ok", null)
//                        .create()
//                dlg.show()
            }
        }
    }

}