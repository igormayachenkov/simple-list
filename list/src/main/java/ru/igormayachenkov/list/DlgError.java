package ru.igormayachenkov.list;

import android.app.AlertDialog;
import android.content.Context;

////////////////////////////////////////////////////////////////////////////////////////////////////
// AlertDialog-BASED DIALOG
// FOR ERROR MESSAGE SHOW

public class DlgError {
    // DATA
    AlertDialog alert;

    public DlgError(Context context, String message){
        // Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);

        // Create Alert dialog
        alert = builder.create();
    }

    // SHOW
    public void show(){
        if(alert!=null)
            alert.show();
    }


}
