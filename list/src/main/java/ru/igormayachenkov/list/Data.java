package ru.igormayachenkov.list;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

///////////////////////////////////////////////////////////////////////////////////////////////
// MAIN DATA OBJECT
public class Data {
    static final String TAG = "myapp.Data";

    //----------------------------------------------------------------------------
    // STATIC LOAD / UNLOAD
    // SINGLETON pattern
    // There is normally no need to subclass Application. In most situation, static singletons can provide the same functionality in a more modular way.
    // If your singleton needs a global context (for example to register broadcast receivers),
    // the function to retrieve it can be given a Context which internally uses Context.getApplicationContext() when first constructing the singleton.
    private static Data instance;

    public static Data instance() {
        if(instance==null){
            // Create & load
            instance = new Data();
            instance.load();
        }
        return instance;
    }

    public static void destroy() {
        if(instance!=null)
            instance.unload();
        instance = null;
    }
    //----------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------
    // GLOBAL CONSTANTS
    public static final String LIST_ID      = "LIST_ID";
    public static final String ITEM_INDEX   = "ITEM_INDEX";
    public static final String ACTIVITY     = "ACTIVITY";

    public static final int     RESULT_INSERTED = 10;
    public static final int     RESULT_UPDATED  = 20;
    public static final int     RESULT_DELETED  = 30;


    public static final String APP_FOLDER = "Simple List";

    //--------------------------------------------------------------------------
    // Prefs change listerner
    SharedPreferences.OnSharedPreferenceChangeListener mPrefChangeListener;

    //--------------------------------------------------------------------------
    // Database
    public Database database;

    //--------------------------------------------------------------------------
    // Data objects
    ListOfLists listOfLists;

    long deviceID;//Android device ID

    //--------------------------------------------------------------------------
    Data(){
        Log.d(TAG, "Data");
        // Database
        database = new Database(TheApp.context());

        // Data objects
        listOfLists = new ListOfLists();

        // get device ID
//        String uid = Settings.Secure.getString(TheApp.context().getContentResolver(), Settings.Secure.ANDROID_ID);
//        BigInteger big = new BigInteger(uid, 16);
//        deviceID = big.longValue();

    }
    // LOAD
    private void load(){
        Log.d(TAG, "load started");

        database.open();
        listOfLists.load();

        // Create OnSharedPreferenceChangeListener
        mPrefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
                Data.this.onSharedPreferenceChanged(sp,key);
            }
        };
        // Listern for changes
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TheApp.instance().getApplicationContext());
        sp.registerOnSharedPreferenceChangeListener(mPrefChangeListener);

        Log.d(TAG, "load finished");
    }

    // UNLOAD
    private void unload(){
        Log.d(TAG, "unload started");

        // Close database
        database.close();

        Log.d(TAG, "unload finished");
    }

    // CLEAR ALL
    public void clearALL(){
        Log.d(TAG, "clear ALL");

        // Close database
        database.deleteALL();

        // Reloat total list
        listOfLists.reload();
    }

    // TO JSON
    public JSONObject toJSON(Collection<DList> lists) throws JSONException {
        JSONObject json = new JSONObject();
        // Version
        PackageInfo pInfo = TheApp.instance().getPackageInfo();
        if(pInfo!=null){
            json.put("versionCode", pInfo.versionCode);
            json.put("versionName", pInfo.versionName);
        }

        // LISTS
        JSONArray listsJSON =  new JSONArray();
        json.put("lists", listsJSON);
        for(DList list : lists){
            listsJSON.put(list.toJSON());
        }

        return json;
    }

    // LOAD FROM JSON
    public class LoadEstimation {
        public int toInsert=0;
        public int toUpdate=0;
    }
    public LoadEstimation estimateLoadingFromJSON(JSONObject json) throws JSONException {
        LoadEstimation estimation = new LoadEstimation();
        // Version

        // LISTS
        JSONArray lists = json.optJSONArray("lists");
        if(lists!=null) {
            for (int i = 0; i < lists.length(); i++) {
                JSONObject listJSON = lists.optJSONObject(i);
                if(listJSON==null) continue;

                // Verify list
                long id = listJSON.optLong("id",0);
                String name = listJSON.optString("name",null);
                if(id==0 || name==null || name.isEmpty()) continue;

                // Search for existed
                DList list = listOfLists.getList(id);

                // Update counters
                if(list!=null)
                    estimation.toUpdate++;
                else
                    estimation.toInsert++;
            }
        }
        return estimation;
    }

    public void loadFromJSON(JSONObject json) throws JSONException{
        // Version

        // LISTS
        JSONArray listsJSON = json.optJSONArray("lists");
        if(listsJSON!=null) {
            for (int i = 0; i < listsJSON.length(); i++) {
                JSONObject listJSON = listsJSON.optJSONObject(i);
                if(listJSON==null) continue;

                // Verify list
                long id = listJSON.optLong("id",0);
                String name = listJSON.optString("name",null);
                if(id==0 || name==null || name.isEmpty()) continue;

                // Search for existed
                DList list = listOfLists.getList(id);

                // UPDATE DATA
                if(list!=null){
                    // Remove old list (& items)
                    listOfLists.deleteList(id);
                }
                // Insert list
                list = new DList(id, name);
                listOfLists.addList(list);
                // Insert list items
                JSONArray itemsJSON = listJSON.optJSONArray("items");
                if(itemsJSON!=null){
                    for (int j = 0; j < itemsJSON.length(); j++) {
                        JSONObject itemJSON = itemsJSON.optJSONObject(j);
                        if (itemJSON == null) continue;
                        String itemName = itemJSON.optString("name",null);
                        if(itemName==null) continue;
                        // Update the database only because of the list is not loaded
                        Data.instance().database.addItem(
                                list.id,
                                itemName,
                                itemJSON.optString("description",null)
                        );
                    }
                }
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // HANDLERS

    private void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        Log.d(TAG, "onSharedPreferenceChanged key:" + key);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DATA OBJECT: List of lists
    public class ListOfLists {
        private boolean            isLoaded;
        private HashMap<Long,DList> hashMap;

        public ListOfLists(){
            isLoaded = false;
            hashMap = new HashMap<>();
        }

        DList getList(long id){
            return hashMap.get(id);
        }
        Collection<DList> getALL(){
            return hashMap.values();
        }

        void load(){
            Log.d(TAG, "ListOfLists.load");
            if(isLoaded) return;
            database.loadListOfLists(this.hashMap);
            isLoaded=true;
        }
        void reload(){
            Log.d(TAG, "ListOfLists.reload");
            // Unload
            isLoaded = false;
            // Load
            load();
        }

        // Add a new list
        DList addList(DList list){
            // Save in the database
            database.insertList(list);
            // Add to the hashmap
            hashMap.put(list.id, list);

            return list;
        }

        // Delete list
        void deleteList(long id){
            // Delete from database
            database.deleteList(id);
            // Remove from the hashMap
            hashMap.remove(id);
        }

        public JSONArray toJSON() throws JSONException {
            JSONArray json = new JSONArray();
            for (DList list: hashMap.values()) {
                json.put(list.toJSON());
            }

            return json;
        }
    }

}
