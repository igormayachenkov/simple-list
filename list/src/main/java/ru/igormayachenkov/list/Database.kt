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

    private lateinit var db: SQLiteDatabase
    private lateinit var dbHelper: DBHelper

    // Open results
    var isUpgraded: Boolean = false; private set
    var isCreated: Boolean = false; private set

    fun open(context: Context) {
        Log.d(TAG, "open")
        isUpgraded = false
        isCreated = false
        dbHelper = DBHelper(
            context,
            "maindatabase.db",  // name
            2 // version
        )
        db = dbHelper.writableDatabase // open here
    }

    fun close() {
        Log.d(TAG, "close")
        dbHelper.close()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // INSERTERS
    fun insertItem(item: DataItem, log: Boolean = true) {
        if (log) Log.d(TAG, "insertItem")

        // Prepare values
        val cv = ContentValues()
        with(item) {
            cv.put(ITEM_ID, id)
            cv.put(LIST_ID, parent_id)
            cv.put(TYPE, type.toInt())
            cv.put(STATE, state.toInt())
            cv.put(NAME, name)
            cv.put(DESCRIPTION, description)
        }
        // Insert
        db.insert(TABLE_ITEMS, null, cv)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATERS
    fun updateItem(item: DataItem) {
        Log.d(TAG, "updateItem $item")

        // Prepare values
        val cv = ContentValues()
        cv.put(LIST_ID, item.parent_id)
        cv.put(TYPE, item.type.toInt())
        cv.put(STATE, item.state.toInt())
        cv.put(NAME, item.name)
        cv.put(DESCRIPTION, item.description)

        // Update
        val args = arrayOf(item.id.toString())
        db.update(
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
        cv.put(STATE, item.state.toInt())
        // Update
        val args = arrayOf(item.id.toString())
        db.update(
            TABLE_ITEMS,  // table
            cv,  // values
            "$ITEM_ID=?",  //where
            args // whereArgs
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DELETERS
    fun deleteItem(id: Long) {
        val n = db.delete(
            TABLE_ITEMS,  // table
            "$ITEM_ID=$id",  //where
            null // whereArgs
        )
        Log.d(TAG, "deleteItem id:$id n:$n")
    }

    fun deleteChildren(parentId: Long) {
        val n = db.delete(
            TABLE_ITEMS,  // table
            "$LIST_ID=$parentId",  //where
            null // whereArgs
        )
        Log.d(TAG, "deleteChildren parentId:$parentId n:$n")
    }

    fun deleteALL() {
        // Delete items
        val rItems = db.delete(
            TABLE_ITEMS,  // table
            null,  //where
            null // whereArgs
        )
        Log.d(TAG, "deleteALL, number of items: $rItems")
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LOADERS
    fun loadItem(id: Long): DataItem? {
        // Query all rows and get Cursor
        val args = arrayOf(id.toString())
        val cursor = db.query(
            TABLE_ITEMS,  // table
            null,  // columns
            ITEM_ID + "=?",  // selection
            args,  // selectionArgs
            null,  // group by
            null,  // having
            null//NAME // order by
        )
        val items = readItems(cursor)
        val item = items.firstOrNull()
        Log.d(TAG, "loadItem id:$id => $item")
        return item
    }

    fun loadListItems(listId: Long): List<DataItem> {
        // LOAD ITEMS
        // Query all rows and get Cursor
        val args = arrayOf(listId.toString())
        val cursor = db.query(
            TABLE_ITEMS,  // table
            null,  // columns
            LIST_ID + "=?",  // selection
            args,  // selectionArgs
            null,  // group by
            null,  // having
            null//NAME // order by
        )
        val items = readItems(cursor)

        Log.d(TAG, "loadItems. parentId:$listId n:${items.size}")
        return items
    }

    private fun readItems(c: Cursor): List<DataItem> {
        val items = ArrayList<DataItem>()
        // Loop for all string
        if (c.moveToFirst()) {
            // Define col numbers by name
            val iItemId = c.getColumnIndex(ITEM_ID)
            val iListId = c.getColumnIndex(LIST_ID)
            val iType = c.getColumnIndex(TYPE)
            val iState = c.getColumnIndex(STATE)
            val iName = c.getColumnIndex(NAME)
            val iDescr = c.getColumnIndex(DESCRIPTION)

            // Load
            do {
                // Create data object
                val item = DataItem(
                    c.getLong(iItemId),
                    c.getLong(iListId),
                    DataItem.Type(c.getInt(iType)),
                    DataItem.State(c.getInt(iState)),
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DATA INFO
    fun queryInfo(): DataInfo {
        Log.d(TAG, "queryInfo")
        val cursor = db.rawQuery("SELECT $TYPE FROM $TABLE_ITEMS", arrayOf())
        var nLists = 0
        var nItems = 0
        while (cursor.moveToNext()) {
            val type = cursor.getInt(0)
            if ((type and DataItem.Type.MASK_hasChildren) > 0) nLists++
            else nItems++
        }
        cursor.close()
        return DataInfo(nLists, nItems)
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
    class DBHelper(context: Context?, name: String?, version: Int) :
        SQLiteOpenHelper(context, name, null, version) {
        override fun onCreate(db: SQLiteDatabase) {
            Log.d(TAG, "onCreate")
            isCreated = true
            // Create table lists
//            db.execSQL("CREATE TABLE " + TABLE_LISTS + " ("
//                    + LIST_ID + " INTEGER PRIMARY KEY,"
//                    + TYPE + " INTEGER,"
//                    + NAME + " TEXT,"
//                    + DESCRIPTION + " TEXT"
//                    + ");")

            // Create table items
            db.execSQL(
                "CREATE TABLE " + TABLE_ITEMS + " ("
                        + ITEM_ID + " INTEGER PRIMARY KEY,"
                        + LIST_ID + " INTEGER,"
                        + CAT_ID + " INTEGER,"
                        + TYPE + " INTEGER,"
                        + STATE + " INTEGER,"
                        + NAME + " TEXT,"
                        + DESCRIPTION + " TEXT"
                        + ");"
            )

            // Create table cats
            db.execSQL(
                "CREATE TABLE " + TABLE_CATS + " ("
                        + CAT_ID + " INTEGER PRIMARY KEY,"
                        + TYPE + " INTEGER,"
                        + NAME + " TEXT,"
                        + DESCRIPTION + " TEXT"
                        + ");"
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.d(TAG, "ON UPGRADE $oldVersion => $newVersion")
            isUpgraded = true
            if (oldVersion == 1 && newVersion == 2) upgrade_1_2(db)
        }
    }


    private fun upgrade_1_2(db: SQLiteDatabase) {
        // Legacy "type" field values
        val typeItem = DataItem.Type(hasChildren = false, isCheckable = true).toInt()
        val typeList = DataItem.Type(hasChildren = true, isCheckable = false).toInt()
        // ALTER items table
        //db.execSQL("ALTER TABLE items RENAME COLUMN sync_state to type",arrayOf()) CRASH ON ANDROID 9,10
        renameSyncStateColumn(db)
        db.execSQL("UPDATE items SET type=$typeItem", arrayOf())

        // Copy data: TABLE_LISTS => TABLE_ITEMS
        db.execSQL(
            "INSERT INTO items (item_id,list_id,type,name) SELECT list_id,0,$typeList,name FROM lists",
            arrayOf()
        )
        db.execSQL("DROP TABLE lists", arrayOf())
    }

    // Implement "ALTER TABLE items RENAME COLUMN sync_state to type"
    // without "RENAME COLUMN"
    private fun renameSyncStateColumn(db: SQLiteDatabase) {
        // 1. Create item_tmp table with correct column name
        db.execSQL(
            "CREATE TABLE items_tmp ("
                    + "item_id INTEGER PRIMARY KEY,"
                    + "list_id INTEGER,"
                    + "cat_id INTEGER,"
                    + "type INTEGER,"
                    + "state INTEGER,"
                    + "name TEXT,"
                    + "description TEXT"
                    + ");"
        )
        // 2. Copy the data from items to items_tmp
        db.execSQL(
            "INSERT INTO items_tmp(item_id, list_id, cat_id, type,       state, name, description)" +
                              " SELECT item_id, list_id, cat_id, sync_state, state, name, description" +
                              " FROM items;"
        )
        // 3. Drop table item
        db.execSQL("DROP TABLE items;")
        // 4. Rename the item_tmp table
        db.execSQL("ALTER TABLE items_tmp RENAME TO items;")
    }

    // THE 1-2 UPGRADE FUNCTION WITH CRASH ON ANDROID 9, 10
    private fun upgrade_1_2_WITH_ERROR(db: SQLiteDatabase) {
        // Legacy "type" field values
        val typeItem = DataItem.Type(hasChildren = false, isCheckable = true).toInt()
        val typeList = DataItem.Type(hasChildren = true, isCheckable = false).toInt()
        // ALTER items table
        db.execSQL("ALTER TABLE items RENAME COLUMN sync_state to type", arrayOf())
        db.execSQL("UPDATE items SET type=$typeItem", arrayOf())

        // Copy data: TABLE_LISTS => TABLE_ITEMS
        db.execSQL(
            "INSERT INTO items (item_id,list_id,type,name) SELECT list_id,0,$typeList,name FROM lists",
            arrayOf()
        )
        db.execSQL("DROP TABLE lists", arrayOf())
    }
}
//                // CHECK THE RESULT (FOR DEBUGGER)
//                data class DataCheck(val id: Long, val name:String, val type:Int)
//                val lists = ArrayList<DataCheck>()
//                val c = db.rawQuery("SELECT * FROM items", arrayOf())
//                if (c.moveToFirst()) {
//                    // Define col numbers by name
//                    val iID = c.getColumnIndex("item_id")
//                    val iName = c.getColumnIndex("name")
//                    val iType = c.getColumnIndex("type")
//                    // Load
//                    do {
//                        // Create new DataItem object from the list row!!!
//                        lists.add( DataCheck(
//                            c.getLong(iID),
//                            c.getString(iName),
//                            c.getInt(iType)
//                        ))
//                    } while (c.moveToNext())
//                }
//                c.close()