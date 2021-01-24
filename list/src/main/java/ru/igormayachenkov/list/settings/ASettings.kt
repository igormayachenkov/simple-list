package ru.igormayachenkov.list.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import ru.igormayachenkov.list.R


class ASettings : AppCompatActivity(){

    companion object{
        private const val TAG = "myapp.ASettings"

        var resourceId:Int?=null

        fun open(activity:AppCompatActivity, rscId:Int){
            resourceId = rscId
            val intent = Intent(activity, ASettings::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        // Check data
        if(resourceId ==null){
            finish()
            return
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener  {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            resourceId?.let {
                setPreferencesFromResource(it, rootKey)
            }
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