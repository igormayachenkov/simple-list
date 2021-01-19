package ru.igormayachenkov.list;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

public class AList extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    static final String TAG = "myapp.AList";
    static final int ITEM_OPEN_REQUEST      = 111;
//    static final int FILE_OPEN_REQUEST      = 222;
    static final int FILE_CREATE_REQUEST    = 333;
    static final int FILE_CREATE_XML_REQUEST= 444;

    // Data objects
    private DList   list;

    // Controls
    ListView            viewList;
    View                viewEmpty;

    // Adapter
    private ListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Controls
        viewList = (ListView) findViewById(R.id.listView);
        viewEmpty = findViewById(R.id.emptyView);

        // Get data objects
        long id = getIntent().getLongExtra(Data.LIST_ID, 0);
        list = Data.instance().listOfLists.getList(id);

        // Load data objects
        list.load();

        // Load controls
        setTitle(list.name);

        // List
        ListView lv = (ListView) findViewById(R.id.listView);
        adapter = new ListAdapter(this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATER
    private void update(){
        if(adapter.getCount()==0){
            viewList.setVisibility(View.GONE);
            viewEmpty.setVisibility(View.VISIBLE);
        }else {
            viewEmpty.setVisibility(View.GONE);
            viewList.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // HANDLERS

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                onMenuAdd();
                return true;

            case R.id.menu_rename:
                onMenuRename();
                return true;

            case R.id.menu_save_json:
                onMenuSave();
                return true;

            case R.id.menu_save_xml:
                onMenuSaveXML();
                return true;

            case R.id.menu_delete:
                onMenuDelete();
                return true;

            case R.id.menu_help:
                onMenuHelp();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Change item state
        DItem item = list.items.get(position);
        item.changeState();

        // Update row
        adapter.updateChecked(item, view, null, null);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // Go to item activity
        startAItem(position);
        return true;
    }

    private void onMenuAdd(){
        // Go to item activity
        startAItem(-1);
    }

    private void startAItem(int index){
        Intent intent = new Intent(this, AItem.class);
        intent.putExtra(Data.LIST_ID,   list.id);
        intent.putExtra(Data.ITEM_INDEX,index);
        startActivityForResult(intent, ITEM_OPEN_REQUEST);
    }

    private void onMenuHelp(){
        // Go to help activity
        Intent intent = new Intent(this, AHelp.class);
        intent.putExtra(Data.ACTIVITY, "AList");
        startActivity(intent);
    }

    private void onMenuDelete(){
        // Show yes/no dialog
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.list_delete_alert_title))
                .setMessage(getString(R.string.list_delete_alert_text))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Data.instance().listOfLists.deleteList(list.id);
                        // Go out
                        setResult(Data.RESULT_DELETED);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void onMenuRename(){
        DlgName dlg = new DlgName(
            this,
            R.string.dialog_title_rename,
            R.string.main_add_hint,
            list.name,
            new DlgName.IEventListener() {
                @Override
                public void onFinishDialog(String text) {
                    if(text.isEmpty()){
                        Toast.makeText(AList.this, R.string.dialog_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Rename List
                    list.rename(text);
                    // Update title
                    setTitle(list.name);
                    // Set flag
                    setResult(Data.RESULT_UPDATED);
                }
            }
        );
        dlg.show();
    }

    private void onMenuSave(){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, list.name+".json");
        startActivityForResult(intent, FILE_CREATE_REQUEST);
    }
    private void onMenuSaveXML(){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, list.name+".xml");
        startActivityForResult(intent, FILE_CREATE_XML_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode= " + requestCode + ", resultCode=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ITEM_OPEN_REQUEST){
            update();
        } else {
            if (resultCode != RESULT_OK) return;
            if (data == null) return;

            switch (requestCode) {
                case FILE_CREATE_REQUEST:
                    doSave(data.getData());
                    break;
                case FILE_CREATE_XML_REQUEST:
                    doSaveXML(data.getData());
                    break;
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DO ACTIONs
    void doSaveXML(Uri uri) {
        try {
            // Open
            ParcelFileDescriptor pfd = getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());

            // Write
            byte[] bytes = list.toXML().getBytes();
            fileOutputStream.write(bytes);

            // Close. Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();

            // Show result
            Toast.makeText(this, bytes.length + " " + getString(R.string.bytes_saved), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            new DlgError(this, e.getMessage()).show();
        }
    }
    void doSave(Uri uri) {
        ArrayList<DList> lists = new ArrayList();
        lists.add(list);
        AMain.doSave(this, uri, lists);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // List ADAPTER
    private class ListAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        int colorChecked;
        int colorUnchecked;


        public ListAdapter(Context context) {
            ctx = context;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            colorChecked = ContextCompat.getColor(context, R.color.colorChecked);
            colorUnchecked = ContextCompat.getColor(context, R.color.colorUnchecked);
        }

        @Override
        public int getCount() {
            return list.items.size();
        }

        @Override
        public Object getItem(int position) {
            return list.items.get(position);
        }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.item_list, parent, false);
            }
            // Fill View
            DItem item = list.items.get(position);
            updateView(item,view);

            return view;
        }

        public void updateView(DItem item, View view){
            //Log.d(TAG, "updateItem "+item.name);

            // Name
            TextView txtName = (TextView) view.findViewById(R.id.txtName);
            txtName.setText(item.name);
            // Description
            TextView txtDescr = (TextView)view.findViewById(R.id.txtDescription);
            if(item.description==null || item.description.isEmpty()) {
                txtDescr.setVisibility(View.GONE);
            }else{
                txtDescr.setVisibility(View.VISIBLE);
                txtDescr.setText(item.description);
            }
            // Checked / Category
            updateChecked(item,view,txtName,txtDescr);
        }

        public void updateChecked(DItem item, View view, TextView txtName, TextView txtDescr) {
            if(txtName==null) txtName  = (TextView) view.findViewById(R.id.txtName);
            if(txtDescr==null)txtDescr = (TextView) view.findViewById(R.id.txtDescription);
            if(item.state==DItem.ITEM_STATE_CHECKED) {
                txtName.setTextColor(colorChecked);
                txtDescr.setTextColor(colorChecked);
            }else{
                txtName.setTextColor(colorUnchecked);
                txtDescr.setTextColor(colorUnchecked);
            }
        }
    }

    private void save(String filename, String format){
        filename = filename+"."+format;

        File file=null;
        String error = null;
        try {
            //File dir = TheApp.context().getExternalFilesDir(null); //Android/data/<package.name>/files
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), Data.APP_FOLDER);
            if (!dir.exists())
                if(!dir.mkdirs())
                    throw new Exception("Directory not created");
            file = new File(dir, filename);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            switch(format) {
                case "xml":
                    list.saveXML(bw);
                    break;
                case "json":
                    bw.write(list.toJSON().toString());
                    break;
                default:
                    throw new Exception("unknown format '"+format+"'");
            }

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
        }

        // SHOW RESULT
        if(error==null){
            Log.d(TAG,"save size:"+(file==null?"0":file.getUsableSpace()));
            new AlertDialog.Builder(this)
                    //.setTitle(getString(R.string.dialog_title_save))
                    .setMessage(getString(R.string.list_save_ok)+"\n"
                            +"Android/Documents/\n"
                            +Data.APP_FOLDER+"/\n"
                            +filename)
                    .setPositiveButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }else{
            Log.d(TAG,"Save error:"+error);
            new AlertDialog.Builder(this)
                    //.setTitle(getString(R.string.dialog_title_save))
                    .setMessage(getString(R.string.list_save_error)+"\n"
                            + error)
                    .setPositiveButton("OK",null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

}
