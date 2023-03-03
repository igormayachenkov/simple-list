package ru.igormayachenkov.list

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ru.igormayachenkov.list.data.DataItem
import ru.igormayachenkov.list.data.DataList
import ru.igormayachenkov.list.data.Element
import java.util.*

////////////////////////////////////////////////////////////////////////////////////////////////////
// GEO DATABASE
object Database {
    const val TAG = "myapp.Database"

    var db: SQLiteDatabase? = null
    var dbHelper: DBHelper? = null

    fun open(context:Context) {
        Log.d(TAG, "open")
        dbHelper = DBHelper(context,
                "maindatabase.db",  // name
                2 // version
        )
        db = dbHelper!!.writableDatabase // open here
    }

    fun close() {
        Log.d(TAG, "close")
        dbHelper?.close()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // INSERTERS
    fun insertList(list: DataList) {
        Log.d(TAG, "addList")

        // Prepare values
        val cv = ContentValues()
        cv.put(LIST_ID, list.id)
        cv.put(NAME, list.name)
        // Insert
        val rowID = db!!.insert(TABLE_LISTS, null, cv)
    }

    fun insertItem(item: DataItem) {
        Log.d(TAG, "insertItem")

        // Prepare values
        val cv = ContentValues()
        with(item) {
            cv.put(ITEM_ID,     id)
            cv.put(LIST_ID,     parent_id)
            cv.put(STATE,       state)
            cv.put(NAME,        name)
            cv.put(DESCRIPTION, description)
        }
        // Insert
        val rowID = db!!.insert(TABLE_ITEMS, null, cv)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATERS
    fun updateListName(id: Long, name: String?) {
        Log.d(TAG, "updateListName")

        // Prepare values
        val cv = ContentValues()
        cv.put(NAME, name)
        // Update
        val args = arrayOf(id.toString())
        db!!.update(
                TABLE_LISTS,  // table
                cv,  // values
                LIST_ID + "=?",  //where
                args // whereArgs
        )
    }

    fun updateItem(item: DataItem) {
        Log.d(TAG, "updateItem $item")

        // Prepare values
        val cv = ContentValues()
        cv.put(NAME,        item.name)
        cv.put(DESCRIPTION, item.description)
        cv.put(STATE,       item.state)

        // Update
        val args = arrayOf(item.id.toString())
        db!!.update(
                TABLE_ITEMS,  // table
                cv,  // values
                ITEM_ID + "=?",  //where
                args // whereArgs
        )
    }

    fun updateItemState(item: DataItem) {
        Log.d(TAG, "updateItemState id:${item.id}  state:${item.state}")

        // Prepare values
        val cv = ContentValues()
        cv.put(STATE, item.state)
        // Update
        val args = arrayOf(item.id.toString())
        db!!.update(
                TABLE_ITEMS,  // table
                cv,  // values
                ITEM_ID + "=?",  //where
                args // whereArgs
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DELETERS
    fun deleteItem(id: Long) {
        Log.d(TAG, "deleteItem")

        // Insert
        val args = arrayOf(id.toString())
        db!!.delete(
                TABLE_ITEMS,  // table
                ITEM_ID + "=?",  //where
                args // whereArgs
        )
    }

    fun deleteList(id: Long) {
        // Dalete items
        val args = arrayOf(id.toString())
        val rItems = db!!.delete(
                TABLE_ITEMS,  // table
                LIST_ID + "=?",  //where
                args // whereArgs
        )
        // Delete list record
        val rLists = db!!.delete(
                TABLE_LISTS,  // table
                LIST_ID + "=?",  //where
                args // whereArgs
        )
        Log.d(TAG, "deleteList list:" + rLists + "rows  items:" + rItems + "rows")
    }

    fun deleteALL() {
        // Dalete items
        val rItems = db!!.delete(
                TABLE_ITEMS,  // table
                null,  //where
                null // whereArgs
        )
        // Delete list record
        val rLists = db!!.delete(
                TABLE_LISTS,  // table
                null,  //where
                null // whereArgs
        )
        Log.d(TAG, "deleteALL lists:$rLists  items:$rItems")
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LOADERS
    fun loadListOfLists():HashSet<DataList> {
        // Open database
        //SQLiteDatabase dbRead = dbHelper.getReadableDatabase();

        // LOAD ITEMS
        val hashSet = HashSet<DataList>()
        // Query all rows and get Cursor
        val c = db!!.query(
                TABLE_LISTS,  // table
                null,  // columns
                null,  // selection
                null,  // selectionArgs
                null,  // group by
                null,  // having
                NAME // order by
        )
        // Loop for all string
        if (c.moveToFirst()) {
            // Define col numbers by name
            val iID = c.getColumnIndex(LIST_ID)
            val iSyncState = c.getColumnIndex(TYPE)
            val iName = c.getColumnIndex(NAME)
            val iDescription = c.getColumnIndex(DESCRIPTION)

            // Load
            do {
                // Create new data object
                val list = DataList(
                        c.getLong(iID),
                        c.getString(iName),
                        // c.getInt(iSyncState)
                        c.getString(iDescription)
                )
                // Add to the list
                hashSet.add(list)
            } while (c.moveToNext())
        }
        c.close()

        // Close database
        //dbHelper.close();
        Log.d(TAG, "loadListOfLists. size:" + hashSet.size)
        return hashSet
    }

    fun loadList(listId:Long):DataList? {
        // Query all rows and get Cursor
        val args = arrayOf(listId.toString())
        val c = db!!.query(
            TABLE_LISTS,  // table
            null,  // columns
            LIST_ID + "=?",  // selection
            args,  // selectionArgs
            null,  // group by
            null,  // having
            null//NAME // order by
        )
        if (c.moveToFirst()) {
            // Define col numbers by name
            val iID = c.getColumnIndex(LIST_ID)
            val iSyncState = c.getColumnIndex(TYPE)
            val iName = c.getColumnIndex(NAME)
            val iDescription = c.getColumnIndex(DESCRIPTION)
            // Create new data object
            return DataList(
                c.getLong(iID),
                c.getString(iName),
                // c.getInt(iSyncState)
                c.getString(iDescription)
            )
        }
        return null
    }

    fun loadListItems(listId:Long):HashSet<DataItem> {
        // Open database
        //SQLiteDatabase dbRead = dbHelper.getReadableDatabase();

        // LOAD ITEMS
        val items = HashSet<DataItem>()
        // Query all rows and get Cursor
        val args = arrayOf(listId.toString())
        val c = db!!.query(
                TABLE_ITEMS,  // table
                null,  // columns
                LIST_ID + "=?",  // selection
                args,  // selectionArgs
                null,  // group by
                null,  // having
                null//NAME // order by
        )
        // Loop for all string
        if (c.moveToFirst()) {
            // Define col numbers by name
            val iID = c.getColumnIndex(ITEM_ID)
            val iType = c.getColumnIndex(TYPE)
            val iState = c.getColumnIndex(STATE)
            val iName = c.getColumnIndex(NAME)
            val iDescription = c.getColumnIndex(DESCRIPTION)

            // Load
            do {
                // Create data object
                val item = DataItem(
                        c.getLong(iID),
                        listId,
                        c.getInt(iState),
                        c.getString(iName),
                        c.getString(iDescription)
                )
                // Add to the list
                items.add(item)
            } while (c.moveToNext())
        }
        c.close()

        // Close database
        //dbHelper.close();
        Log.d(TAG, "loadListItems. size:" + items.size)
        return  items
    }
//
//    fun loadListElements(listId:Long):List<Element> {
//        // LOAD ITEMS
//        val items = ArrayList<Element>()
//        // Query all rows and get Cursor
//        val args = arrayOf(listId.toString())
//        val c = db!!.query(
//            TABLE_ITEMS,  // table
//            null,  // columns
//            LIST_ID + "=?",  // selection
//            args,  // selectionArgs
//            null,  // group by
//            null,  // having
//            null//NAME // order by
//        )
//        // Loop for all string
//        if (c.moveToFirst()) {
//            // Define col numbers by name
//            val iID = c.getColumnIndex(ITEM_ID)
//            val iType = c.getColumnIndex(TYPE)
//            val iState = c.getColumnIndex(STATE)
//            val iName = c.getColumnIndex(NAME)
//            val iDescription = c.getColumnIndex(DESCRIPTION)
//
//            // Load
//            do {
//                // Create deta object
//                when(c.getInt(iType)) {
//                    TYPE_ITEM -> items.add(DataItem(
//                        c.getLong(iID),
//                        listId,
//                        c.getInt(iState),
//                        c.getString(iName),
//                        c.getString(iDescription)
//                    ))
//
//                    TYPE_LIST -> items.add(DataList(
//                        c.getLong(iID),
//
//                        ))
//                }
//            } while (c.moveToNext())
//        }
//        c.close()
//
//        // Close database
//        //dbHelper.close();
//        Log.d(TAG, "loadListItems. size:" + items.size)
//        return  items
//    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // DATABASE STRUCTURE

    // TABLE NAMES
    const val TABLE_LISTS = "lists"
    const val TABLE_ITEMS = "items"
    const val TABLE_CATS = "cats"

    // FIELD NAMES (common for all tables)
    // keys
    const val LIST_ID = "list_id"
    const val ITEM_ID = "item_id"
    const val CAT_ID = "cat_id"

    // flags
    const val TYPE = "type"
    const val STATE = "state"

    // data
    const val NAME = "name"
    const val DESCRIPTION = "description"

    // type
    const val TYPE_LIST = 100
    const val TYPE_ITEM = 200

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // OPEN HELPER
    class DBHelper(context: Context?, name: String?, version: Int) : SQLiteOpenHelper(context, name, null, version) {
        override fun onCreate(db: SQLiteDatabase) {
            Log.d(TAG, "onCreate")
            // Create table lists
//            db.execSQL("CREATE TABLE " + TABLE_LISTS + " ("
//                    + LIST_ID + " INTEGER PRIMARY KEY,"
//                    + TYPE + " INTEGER,"
//                    + NAME + " TEXT,"
//                    + DESCRIPTION + " TEXT"
//                    + ");")

            // Create table items
            db.execSQL("CREATE TABLE " + TABLE_ITEMS + " ("
                    + ITEM_ID + " INTEGER PRIMARY KEY,"
                    + LIST_ID + " INTEGER,"
                    + CAT_ID + " INTEGER,"
                    + TYPE + " INTEGER,"
                    + STATE + " INTEGER,"
                    + NAME + " TEXT,"
                    + DESCRIPTION + " TEXT"
                    + ");")

            // Create table cats
            db.execSQL("CREATE TABLE " + TABLE_CATS + " ("
                    + CAT_ID + " INTEGER PRIMARY KEY,"
                    + TYPE + " INTEGER,"
                    + NAME + " TEXT,"
                    + DESCRIPTION + " TEXT"
                    + ");")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.d(TAG, "ON UPGRADE $oldVersion => $newVersion")
            if(oldVersion<=1){
                // ALTER items table
                db.execSQL("ALTER TABLE items RENAME COLUMN sync_state to type",arrayOf())
                db.execSQL("UPDATE items SET type=$TYPE_ITEM", arrayOf())

                // Copy data: TABLE_LISTS => TABLE_ITEMS
                db.execSQL("INSERT INTO items (item_id,list_id,type,name) SELECT list_id,0,$TYPE_LIST,name FROM lists", arrayOf())
                db.execSQL("DROP TABLE lists",arrayOf())

                data class DataCheck(val id: Long, val name:String, val type:Int)
                val lists = ArrayList<DataCheck>()
                val c = db.rawQuery("SELECT * FROM items", arrayOf())
                if (c.moveToFirst()) {
                    // Define col numbers by name
                    val iID = c.getColumnIndex("item_id")
                    val iName = c.getColumnIndex("name")
                    val iType = c.getColumnIndex("type")
                    // Load
                    do {
                        // Create new DataItem object from the list row!!!
                        lists.add( DataCheck(
                            c.getLong(iID),
                            c.getString(iName),
                            c.getInt(iType)
                        ))
                    } while (c.moveToNext())
                }
                c.close()
            }
            //throw Exception("ON UPGRADE $oldVersion => $newVersion")
        }
    }
}