package ru.igormayachenkov.list;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;


////////////////////////////////////////////////////////////////////////////////////////////////////
// AlertDialog-BASED DIALOG
// FOR NAME INPUT

public class DlgName {
    // EVENT INTERFACE
    public interface IEventListener {
        void onFinishDialog(String text);
    }

    // DATA
    IEventListener listener;
    AlertDialog alert;


    // CONSTRUCTOR
    public DlgName(Context context, int idTitle, int idNameHint, String name, IEventListener listener){
        this.listener=listener;

        // Create builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(idTitle!=0)
            builder.setTitle(idTitle);

        // Set up the input
        final EditText input = new EditText(context);
        if(idNameHint!=0)
            input.setHint(idNameHint);
        if(name!=null)
            input.setText(name);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Raise event
                if(DlgName.this.listener!=null)
                    DlgName.this.listener.onFinishDialog(input.getText().toString().trim());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        // Create alert dialog
        alert = builder.create();

        // Seet keyboard auto show
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    // SHOW
    public void show(){
        if(alert!=null)
            alert.show();
    }
}
