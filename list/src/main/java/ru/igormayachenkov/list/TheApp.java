package ru.igormayachenkov.list;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;

////////////////////////////////////////////////////////////////////////////////////////////////////
// THE APPLICATION INSTANCE
public class TheApp extends Application {
    private static TheApp instance;

    public static TheApp instance(){
        return instance;
    }

    public static Context context(){
        return instance;//.getApplicationContext();
    }

    public static SharedPreferences prefs() {
        return PreferenceManager.getDefaultSharedPreferences(context());
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Load data
        Data.instance();
    }

    public PackageInfo getPackageInfo(){
        PackageInfo pInfo = null;
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
