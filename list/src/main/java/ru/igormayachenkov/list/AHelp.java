package ru.igormayachenkov.list;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class AHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_help);

        // Controls
        WebView webView = (WebView)findViewById(R.id.webView);

        // Load webview
        webView.loadUrl(getString(R.string.help_webview));

        // Visibility
        //String activity = getIntent().getStringExtra(Data.ACTIVITY);

        // Version
        PackageInfo pInfo = TheApp.instance().getPackageInfo();
        ((TextView)findViewById(R.id.txtVersion)).setText(
                (pInfo==null)?"":pInfo.versionName);
    }
}
