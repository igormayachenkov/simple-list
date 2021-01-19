package ru.igormayachenkov.list;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

////////////////////////////////////////////////////////////////////////////////////////////////////
// GEO DATABASE
public class Database {
    static final String TAG = "myapp.Database";

    // TABLE NAMES
    protected static final String TABLE_LISTS       = "lists";
    protected static final String TABLE_ITEMS       = "items";
    protected static final String TABLE_CATS        = "cats";

    // FIELD NAMES (common for all tables)
    // keys
    protected static final String LIST_ID       = "list_id";
    protected static final String ITEM_ID       = "item_id";
    protected static final String CAT_ID        = "cat_id";
    // flags
    protected static final String SYNC_STATE    = "sync_state";
    protected static final String STATE         = "state";
    // data
    protected static final String NAME          = "name";
    protected static final String DESCRIPTION   = "description";

    public SQLiteDatabase db;

    DBHelper    dbHelper;

    public Database(Context context){
        dbHelper = new DBHelper(context,
                "maindatabase.db",  // name
                1                   // version
        );
    }


    public void open(){
        Log.d(TAG, "open");
        db = dbHelper.getWritableDatabase();// Open database
    }

    public void close(){
        Log.d(TAG, "close");
        dbHelper.close();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // INSERTERS
    public void insertList(DList list) {
        Log.d(TAG, "addList");

        // Prepare values
        ContentValues cv = new ContentValues();
        cv.put(LIST_ID, list.id);
        cv.put(NAME, list.name);
        // Insert
        long rowID = db.insert(TABLE_LISTS, null, cv);
    }

    public void addItem(long listID, String name, String description){
        Log.d(TAG, "addItem");

        // Prepare values
        long id = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_ID, id);
        cv.put(LIST_ID, listID);
        cv.put(NAME, name);
        cv.put(DESCRIPTION, description);
        // Insert
        long rowID = db.insert(TABLE_ITEMS, null, cv);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATERS
    public void updateListName(long id, String name){
        Log.d(TAG, "updateListName");

        // Prepare values
        ContentValues cv = new ContentValues();
        cv.put(NAME,name);
        // Update
        String[] args = {String.valueOf(id)};
        db.update(
                TABLE_LISTS,// table
                cv,// values
                LIST_ID+"=?", //where
                args// whereArgs
        );

    }

    public void updateItemName(long id, String name, String description){
        Log.d(TAG, "updateItemName");

        // Prepare values
        ContentValues cv = new ContentValues();
        cv.put(NAME,name);
        cv.put(DESCRIPTION,description);
        // Update
        String[] args = {String.valueOf(id)};
        db.update(
                TABLE_ITEMS,// table
                cv,// values
                ITEM_ID+"=?", //where
                args// whereArgs
        );

    }

    public void updateItemState(long id, int state){
        Log.d(TAG, "updateItemState");

        // Prepare values
        ContentValues cv = new ContentValues();
        cv.put(STATE,state);
        // Update
        String[] args = {String.valueOf(id)};
        db.update(
                TABLE_ITEMS,// table
                cv,// values
                ITEM_ID+"=?", //where
                args// whereArgs
        );

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DELETERS
    public void deleteItem(long id){
        Log.d(TAG, "deleteItem");

        // Insert
        String[] args = {String.valueOf(id)};
        db.delete(
                TABLE_ITEMS,// table
                ITEM_ID+"=?", //where
                args// whereArgs
        );

    }

    public void deleteList(long id){
        // Dalete items
        String[] args = {String.valueOf(id)};
        int rItems = db.delete(
                TABLE_ITEMS,// table
                LIST_ID+"=?", //where
                args// whereArgs
        );
        // Delete list record
        int rLists = db.delete(
                TABLE_LISTS,// table
                LIST_ID+"=?", //where
                args// whereArgs
        );
        Log.d(TAG, "deleteList list:"+rLists+"rows  items:"+rItems+"rows");
    }

    public void deleteALL() {
        // Dalete items
        int rItems = db.delete(
                TABLE_ITEMS,// table
                null, //where
                null// whereArgs
        );
        // Delete list record
        int rLists = db.delete(
                TABLE_LISTS,// table
                null, //where
                null// whereArgs
        );
        Log.d(TAG, "deleteALL lists:"+rLists+"  items:"+rItems);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LOADERS
    public void loadListOfLists(HashMap<Long, DList> hashMap){
        // Open database
        //SQLiteDatabase dbRead = dbHelper.getReadableDatabase();

        // LOAD ITEMS
        hashMap.clear();
        // Query all rows and get Cursor
        Cursor c = db.query(
                TABLE_LISTS,// table
                null,       // columns
                null,       // selection
                null,       // selectionArgs
                null,       // group by
                null,       // having
                NAME        // order by
        );
        // Loop for all string
        if (c.moveToFirst()) {
            // Define col numbers by name
            int iID         = c.getColumnIndex(LIST_ID);
            int iSyncState  = c.getColumnIndex(SYNC_STATE);
            int iName       = c.getColumnIndex(NAME);
            int iDescription= c.getColumnIndex(DESCRIPTION);

            // Load
            do {
                // Create new data object
                DList list = new DList(
                        c.getLong(iID),
                        c.getString(iName));
                list.syncState  = c.getInt(iSyncState);
                list.description= c.getString(iDescription);
                // Add to the list
                hashMap.put(list.id, list);
            } while (c.moveToNext());
        }
        c.close();

        // Close database
        //dbHelper.close();

        Log.d(TAG, "loadListOfLists. size:" + hashMap.size());
    }

    public void loadListItems(DList list){
        // Open database
        //SQLiteDatabase dbRead = dbHelper.getReadableDatabase();

        // LOAD ITEMS
        list.items.clear();
        // Query all rows and get Cursor
        String[] args = {String.valueOf(list.id)};
        Cursor c = db.query(
                TABLE_ITEMS,// table
                null,       // columns
                LIST_ID+"=?",// selection
                args,       // selectionArgs
                null,       // group by
                null,       // having
                NAME        // order by
        );
        // Loop for all string
        if (c.moveToFirst()) {
            // Define col numbers by name
            int iID         = c.getColumnIndex(ITEM_ID);
            int iSyncState  = c.getColumnIndex(SYNC_STATE);
            int iState      = c.getColumnIndex(STATE);
            int iName       = c.getColumnIndex(NAME);
            int iDescription= c.getColumnIndex(DESCRIPTION);

            // Load
            do {
                // Create deta object
                DItem item = new DItem();
                // Load it
                item.id         = c.getLong(iID);
                item.syncState  = c.getInt(iSyncState);
                item.state      = c.getInt(iState);
                item.name       = c.getString(iName);
                item.description= c.getString(iDescription);
                // Add to the list
                list.items.add(item);
            } while (c.moveToNext());
        }
        c.close();

        // Close database
        //dbHelper.close();

        Log.d(TAG, "loadListItems. size:" + list.items.size());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // DATABASE STRUCTURE
    protected void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        // Create table lists
        db.execSQL("CREATE TABLE " + TABLE_LISTS + " ("
                + LIST_ID       + " INTEGER PRIMARY KEY,"
                + SYNC_STATE    + " INTEGER,"
                + NAME          + " TEXT,"
                + DESCRIPTION   + " TEXT"
                + ");");

        // Create table items
        db.execSQL("CREATE TABLE "+ TABLE_ITEMS +" ("
                + ITEM_ID       + " INTEGER PRIMARY KEY,"
                + LIST_ID       + " INTEGER,"
                + CAT_ID        + " INTEGER,"
                + SYNC_STATE    + " INTEGER,"
                + STATE         + " INTEGER,"
                + NAME          + " TEXT,"
                + DESCRIPTION   + " TEXT"
                + ");");

        // Create table cats
        db.execSQL("CREATE TABLE " + TABLE_CATS + " ("
                + CAT_ID        + " INTEGER PRIMARY KEY,"
                + SYNC_STATE    + " INTEGER,"
                + NAME          + " TEXT,"
                + DESCRIPTION   + " TEXT"
                + ");");
    }

    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // OPEN HELPER
    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, int version) {
            super(context, name, null, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            Database.this.onCreate(db);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Database.this.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
