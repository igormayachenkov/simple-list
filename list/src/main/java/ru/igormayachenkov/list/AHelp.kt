package ru.igormayachenkov.list

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.igormayachenkov.list.App.Companion.instance

class AHelp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_help)

        // Controls
        val webView = findViewById<View>(R.id.webView) as WebView

        // Load webview
        webView.loadUrl(getString(R.string.help_webview))

        // Visibility
        //String activity = getIntent().getStringExtra(Data.ACTIVITY);

        // Version
        val pInfo = instance()!!.packageInfo
        (findViewById<View>(R.id.txtVersion) as TextView).text = if (pInfo == null) "" else pInfo.versionName
    }
}