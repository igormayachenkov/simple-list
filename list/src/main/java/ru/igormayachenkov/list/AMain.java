package ru.igormayachenkov.list;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class AMain extends AppCompatActivity implements AdapterView.OnItemClickListener {
    static final String TAG = "myapp.AMain";
    static final int LIST_OPEN_REQUEST      = 111;
    static final int FILE_OPEN_REQUEST      = 222;
    static final int FILE_CREATE_REQUEST    = 333;

    // Data objects
    private ArrayList<DList>  sortedList     = new ArrayList<>();
    private Comparator<DList> comparatorName = new Comparator<DList>() {
        @Override
        public int compare(DList a, DList b) {
            return a.name.compareTo(b.name);
        }
    };

    // Controls
    ListView    viewList;
    View        viewEmpty;

    // Adapter
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.main_title);

        // Controls
        viewList = (ListView) findViewById(R.id.listView);
        viewEmpty = findViewById(R.id.emptyView);

        // List
        adapter = new ListAdapter(this);
        viewList.setAdapter(adapter);
        viewList.setOnItemClickListener(this);

        //updateScheduled=false;
        update();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        Data.destroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

//        if(updateScheduled) {
//            listAdapter.notifyDataSetChanged();
//            updateScheduled = false;
//        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATER
    private void update(){
        // Reload sorted list
        sortedList.clear();
        sortedList.addAll(Data.instance().listOfLists.getALL());
        Collections.sort(sortedList, comparatorName);

        // Update controls
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

            case R.id.menu_save:
                onMenuSave();
                return true;

            case R.id.menu_clear:
                onMenuClear();
                return true;

            case R.id.menu_load:
                onMenuLoad();
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

    private void startAList(DList list){
        Intent intent = new Intent(AMain.this, AList.class);
        intent.putExtra(Data.LIST_ID, list.id);
        startActivityForResult(intent,LIST_OPEN_REQUEST);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this, "onItemClick", Toast.LENGTH_SHORT).show();
        // Go to list activity
        startAList((DList)adapter.getItem(position));
    }

    public void onMenuAdd(){
        // ALERT DIALOG
        DlgName dlg = new DlgName(
            this,
            R.string.main_add_menu,
            R.string.main_add_hint,
            null,// name value
            new DlgName.IEventListener() {
                @Override
                public void onFinishDialog(String text) {
                    if(text.isEmpty()){
                        Toast.makeText(AMain.this, R.string.dialog_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Create a new list object
                    DList list = new DList(System.currentTimeMillis(), text);
                    // Add new list
                    Data.instance().listOfLists.addList(list);
                    // Go to list activity
                    startAList(list);
                }
            }
        );
        dlg.show();
    }

    public void onMenuHelp(){
        // Go to help activity
        Intent intent = new Intent(this, AHelp.class);
        intent.putExtra(Data.ACTIVITY, "AMain");
        startActivity(intent);
    }

    public void onMenuClear(){
        // SHOW DIALOG
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.main_clear_message);
        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doClear();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }
    public void onMenuLoad(){
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers, it would be "*/*".
        intent.setType("*/*");

//        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
        startActivityForResult(intent, FILE_OPEN_REQUEST);
    }

    public void onMenuSave(){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "Simple List.json");
        startActivityForResult(intent, FILE_CREATE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode= " + requestCode + ", resultCode=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==LIST_OPEN_REQUEST){
            update();
        } else {
            if (resultCode != RESULT_OK) return;
            if (data == null) return;

            switch (requestCode) {
                case FILE_OPEN_REQUEST:
                    doLoad(data.getData());
                    break;
                case FILE_CREATE_REQUEST:
                    doSave(this,
                            data.getData(),
                            Data.instance().listOfLists.getALL() // all lists
                    );
                    break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DO ACTIONs
    void doClear(){
        Data.instance().clearALL();
        update();
    }
    public static void doSave(Context context, Uri uri, Collection<DList> lists) {
        try {
            // Open
            ParcelFileDescriptor pfd = context.getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());

            // Write
            byte[] bytes = Data.instance().toJSON(lists).toString().getBytes();
            fileOutputStream.write(bytes);

            // Close. Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();

            // Show result
            Toast.makeText(context, bytes.length + " " + context.getString(R.string.bytes_saved), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            new DlgError(context, e.getMessage()).show();
        }
    }
    void doLoad(Uri uri){
        try {
            // Open
            ParcelFileDescriptor pfd = getContentResolver().
                    openFileDescriptor(uri, "r");
            BufferedReader reader =
                    new BufferedReader(new FileReader(pfd.getFileDescriptor()));

            // Read
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
            String text = sb.toString();

            // Close. Let the document provider know you're done by closing the stream.
            reader.close();
            pfd.close();

            // Parce & update data
            JSONObject json = new JSONObject(text);
            doLoad(json);

        } catch (Exception e) {
            e.printStackTrace();
            new DlgError(this, e.getMessage()).show();
        }
    }

    void doLoad(final JSONObject json) throws JSONException {
        // Estimate loading
        Data.LoadEstimation estimation = Data.instance().estimateLoadingFromJSON(json);
        String message =
                getString(R.string.load_to_insert)+ " " + estimation.toInsert+"\n"+
                getString(R.string.load_to_update)+ " " + estimation.toUpdate;
        // Ask user for request
        DlgCommon dlg = new DlgCommon(this, R.string.load_title, 0, new DlgCommon.IEventListener() {
            @Override
            public void onConfirmation() {
                try {
                    // Update data
                    Data.instance().loadFromJSON(json);
                    // Update UI
                    update();
                    // Show result
                    Toast.makeText(AMain.this, R.string.load_success, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new DlgError(AMain.this, e.getMessage()).show();
                }
            }
        });
        dlg.setMessage(message);
        dlg.show();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // List ADAPTER
    private class ListAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;


        public ListAdapter(Context context) {
            ctx = context;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return sortedList.size();
        }

        @Override
        public Object getItem(int position) {
            return sortedList.get(position);
        }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.item_main, parent, false);
            }

            // UPDATE View
            DList list = sortedList.get(position);
            // Name
            ((TextView) view.findViewById(R.id.txtName)).setText(list.name);

            return view;
        }
    }

}
