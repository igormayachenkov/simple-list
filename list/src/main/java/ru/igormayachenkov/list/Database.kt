package ru.igormayachenkov.list

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ru.igormayachenkov.list.data.*
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
    fun insertItem(item: DataItem) {
        Log.d(TAG, "insertItem")

        // Prepare values
        val cv = ContentValues()
        with(item) {
            cv.put(ITEM_ID,     id)
            cv.put(LIST_ID,     parent_id)
            cv.put(TYPE,        type)
            cv.put(STATE,       state)
            cv.put(NAME,        name)
            cv.put(DESCRIPTION, description)
        }
        // Insert
        val rowID = db!!.insert(TABLE_ITEMS, null, cv)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATERS
    fun updateItem(item: DataItem) {
        Log.d(TAG, "updateItem $item")

        // Prepare values
        val cv = ContentValues()
        cv.put(LIST_ID,     item.parent_id)
        cv.put(TYPE,        item.type)
        cv.put(STATE,       item.state)
        cv.put(NAME,        item.name)
        cv.put(DESCRIPTION, item.description)

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

    fun deleteALL() {
        // Delete items
        val rItems = db!!.delete(
                TABLE_ITEMS,  // table
                null,  //where
                null // whereArgs
        )
        Log.d(TAG, "deleteALL, number of items: $rItems")
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LOADERS
    fun loadItem(id:Long):DataItem? {
        // Query all rows and get Cursor
        val args = arrayOf(id.toString())
        val cursor = db!!.query(
            TABLE_ITEMS,  // table
            null,  // columns
            ITEM_ID + "=?",  // selection
            args,  // selectionArgs
            null,  // group by
            null,  // having
            null//NAME // order by
        )
        val items = readItems(cursor)

        return items.firstOrNull()
    }

    fun loadListItems(listId:Long):List<DataItem> {
        // LOAD ITEMS
        // Query all rows and get Cursor
        val args = arrayOf(listId.toString())
        val cursor = db!!.query(
                TABLE_ITEMS,  // table
                null,  // columns
                LIST_ID + "=?",  // selection
                args,  // selectionArgs
                null,  // group by
                null,  // having
                null//NAME // order by
        )
        val items = readItems(cursor)

        Log.d(TAG, "loadListItems. size:" + items.size)
        return  items
    }
    private fun readItems(c:Cursor):List<DataItem>{
        val items = ArrayList<DataItem>()
        // Loop for all string
        if (c.moveToFirst()) {
            // Define col numbers by name
            val iItemId = c.getColumnIndex(ITEM_ID)
            val iListId = c.getColumnIndex(LIST_ID)
            val iType   = c.getColumnIndex(TYPE)
            val iState  = c.getColumnIndex(STATE)
            val iName   = c.getColumnIndex(NAME)
            val iDescr  = c.getColumnIndex(DESCRIPTION)

            // Load
            do {
                // Create data object
                val item = DataItem(
                    c.getLong(iItemId),
                    c.getLong(iListId),
                    c.getInt(iType),
                    c.getInt(iState),
                    c.getString(iName),
                    c.getString(iDescr)
                )
                // Add to the list
                items.add(item)
            } while (c.moveToNext())
        }
        c.close()
        return items
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // DATABASE STRUCTURE

    // TABLE NAMES
    //const val TABLE_LISTS = "lists"
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