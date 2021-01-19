package ru.igormayachenkov.list;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AItem extends AppCompatActivity {

    // Data objects
    private DList       list;
    private int         itemIndex;

    // Controls
    TextView    txtName;
    TextView    txtDescr;
    ImageButton btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_item);

        // Get data objects
        long id = getIntent().getLongExtra(Data.LIST_ID,0);
        list = Data.instance().listOfLists.getList(id);
        itemIndex = getIntent().getIntExtra(Data.ITEM_INDEX,-1);

        // Load data objects

        // Controls
        txtName     = (TextView) findViewById(R.id.txtName);
        txtDescr    = (TextView) findViewById(R.id.txtDescr);
        btnDelete   = (ImageButton) findViewById(R.id.btnDel);

        // LOAD DATA FIELDS
        if(itemIndex>=0){
            DItem item = list.items.get(itemIndex);
            // Load item fialds
            txtName.setText(item.name);
            txtDescr.setText(item.description);
        }else {
            btnDelete.setVisibility(View.GONE);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // HANDLERS
    public void onButtonSave(View v){
        // Validate data
        String name = txtName.getText().toString();
        if(name.isEmpty()){
            txtName.setError(getString(R.string.item_error));
            return;
        }

        // Save data
        if(itemIndex<0) {
            // New item
            list.addItem(name, txtDescr.getText().toString());
            setResult(Data.RESULT_INSERTED);
        }else{
            // Existed item
            list.updateItemName(itemIndex, name, txtDescr.getText().toString());
            setResult(Data.RESULT_UPDATED);
        }

        // Return
        finish();
    }
    public void onButtonDelete(View v){
        //Toast.makeText(this, "onButtonDelete", Toast.LENGTH_SHORT).show();
        // Delete item
        list.deleteItem(itemIndex);

        // Return
        setResult(Data.RESULT_DELETED);
        finish();
    }
}
