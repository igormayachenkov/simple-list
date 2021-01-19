package ru.igormayachenkov.list;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: List

public class DList {
    static final String TAG = "myapp.DList";

    final long      id;
    int             syncState;
    String          name;
    String          description;
    ArrayList<DItem> items;
    boolean         isLoaded;

    public DList(long id, String name){
        this.id = id;
        this.name = name;
        items = new ArrayList<>();
        isLoaded = false;
    }

    public void load(){
        Log.d(TAG, "List.load");
        if(isLoaded) return;
        Data.instance().database.loadListItems(this);
        isLoaded=true;
    }

    public void rename(String newName){
        Data.instance().database.updateListName(id, newName);
        name = newName;
    }

    public void addItem(String name, String description){
        Log.d(TAG, "List.addItem");
        Data.instance().database.addItem(this.id, name, description);
        // Reload items
        isLoaded = false;
        load();
    }

    public void updateItemName(int itemIndex, String name, String description){
        Log.d(TAG, "List.updateItemName");
        // Get item
        DItem item = items.get(itemIndex);
        // Update item
        Data.instance().database.updateItemName(item.id, name, description);
        // Reload items
        isLoaded = false;
        load();
    }

    public void deleteItem(int itemIndex){
        Log.d(TAG, "List.deleteItem");
        // Get item
        DItem item = items.get(itemIndex);
        // Delete item
        Data.instance().database.deleteItem(item.id);
        // Reload items
        items.remove(itemIndex);
    }

    // EXPORT TO A FILE
    public String toXML() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<list>\n");
        sb.append("<id>"+id+"</id>\n");
        sb.append("<name>"+name+"</name>\n");
        if(description!=null && !description.isEmpty())
            sb.append("<description>"+description+"</description>\n");
        sb.append("<items>\n");
        for (DItem item:items) {
            sb.append("<item>\n");
            sb.append("<id>"+item.id+"</id>\n");
            sb.append("<name>"+item.name+"</name>\n");
            if(item.description!=null && !item.description.isEmpty())
                sb.append("<description>"+item.description+"</description>\n");
            sb.append("<state>"+item.state+"</state>\n");
            sb.append("</item>\n");
        }
        sb.append("</items>\n");
        sb.append("</list>");
        return sb.toString();
    }

    public void saveXML(BufferedWriter bw) throws IOException {
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); bw.newLine();
        bw.write("<list>"); bw.newLine();
        bw.write("<id>"+id+"</id>"); bw.newLine();
        bw.write("<name>"+name+"</name>"); bw.newLine();
        if(description!=null && !description.isEmpty())
            bw.write("<description>"+description+"</description>"); bw.newLine();
        bw.write("<items>"); bw.newLine();
        for (DItem item:items) {
            bw.write("<item>"); bw.newLine();
            bw.write("<id>"+item.id+"</id>"); bw.newLine();
            bw.write("<name>"+item.name+"</name>"); bw.newLine();
            if(item.description!=null && !item.description.isEmpty())
                bw.write("<description>"+item.description+"</description>"); bw.newLine();
            bw.write("<state>"+item.state+"</state>"); bw.newLine();
            bw.write("</item>"); bw.newLine();
        }
        bw.write("</items>"); bw.newLine();
        bw.write("</list>");
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("name",name);
        if(description!=null && !description.isEmpty())
            json.put("description",description);
        // ITEMS
        load();
        JSONArray jsItems = new JSONArray();
        for (DItem item:items) {
            JSONObject js = new JSONObject();
            js.put("id",item.id);
            js.put("name",item.name);
            if(item.description!=null && !item.description.isEmpty())
                js.put("description",item.description);
            js.put("state",item.state);
            jsItems.put(js);
        }
        json.put("items", jsItems);
        return json;
    }

}
