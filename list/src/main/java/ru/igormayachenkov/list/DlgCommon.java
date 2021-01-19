package ru.igormayachenkov.list;

////////////////////////////////////////////////////////////////////////////////////////////////////
// AlertDialog-BASED DIALOG
// FOR INFO SHOWING OR CONFIRMATION REQUEST

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DlgCommon {
    // EVENT INTERFACE
    public interface IEventListener {
        void onConfirmation();
    }

    // DATA
    IEventListener listener;
    AlertDialog alert;

    // CONSTRUCTOR
    public DlgCommon(Context context, int idTitle, int idMessage, IEventListener listener){
        this.listener = listener;

        // Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(idTitle!=0)   builder.setTitle(idTitle);
        if(idMessage!=0) builder.setMessage(idMessage);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Raise event
                if (DlgCommon.this.listener != null)
                    DlgCommon.this.listener.onConfirmation();
            }
        });
        if(listener!=null)
            builder.setNegativeButton(android.R.string.cancel, null);

        // Create alert dialog
        alert = builder.create();
    }

    // SETTERS
    public  void setMessage(String message) {
        if (alert != null)
            alert.setMessage(message);
    }

    // SHOW
    public void show(){
        if(alert!=null)
            alert.show();
    }


}
