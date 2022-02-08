package ru.igormayachenkov.list

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ru.igormayachenkov.list.App.Companion.getString
import ru.igormayachenkov.list.data.Item
import ru.igormayachenkov.list.data.List
import java.io.*

object Converter {
    private const val TAG = "myapp.Converter"

    private const val LOAD_ALL_REQUEST      = 111
    private const val SAVE_ALL_REQUEST      = 222
    private const val SAVE_LIST_REQUEST     = 333
    private const val SAVE_LIST_XML_REQUEST = 444

    //----------------------------------------------------------------------------------------------
    // INTERFACE
    fun loadAll() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers, it would be "*/*".
        intent.type = "*/*"

        AMain.instance?.startExternalActivity(intent, LOAD_ALL_REQUEST)
    }

    fun saveAll() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Create a file with the requested MIME type.
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TITLE, "Simple List.json")
        AMain.instance?.startExternalActivity(intent, SAVE_ALL_REQUEST)
    }

    fun saveOpenList() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TITLE, Logic.openList.value!!.name + ".json")
        AMain.instance?.startExternalActivity(intent, SAVE_LIST_REQUEST)
    }

    fun saveOpenListXML() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TITLE, Logic.openList.value!!.name + ".xml")
        AMain.instance?.startExternalActivity(intent, SAVE_LIST_XML_REQUEST)
    }

    // TO CALL FROM AMain
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult requestCode= $requestCode, resultCode=$resultCode")

        if (resultCode != AppCompatActivity.RESULT_OK) return
        if (data == null) return
        when (requestCode) {
            LOAD_ALL_REQUEST        -> doLoad(data.data)
            SAVE_ALL_REQUEST        -> doSaveAll(data.data )
            SAVE_LIST_REQUEST       -> doSaveOpenList(data.data)
            SAVE_LIST_XML_REQUEST   -> doSaveOpenListXML(data.data)
        }
    }

    //----------------------------------------------------------------------------------------------
    // HANDLERS after external activity call
    private fun doLoad(uri: Uri?) {
        try {
            // Read file
            val json = readJSON(uri)
            // Load
            doLoad(json)
        } catch (e: Exception) { Utils.showErrorDialog(e) }
    }

    private fun doLoad(json: JSONObject?) {
        // Estimate loading
        val estimation = estimateLoadingFromJSON(json!!)
        val message =   "${getString(R.string.load_to_insert)} ${estimation.toInsert}\n"+
                        "${getString(R.string.load_to_update)} ${estimation.toUpdate}"
        // Ask user for request
        val dlg = DlgCommon(App.context, R.string.load_title, 0, {
            try {
                loadFromJSON(json)
                // Show result
                Toast.makeText(App.context, R.string.load_success, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                DlgError(App.context, e.message).show()
            }
        })
        dlg.setMessage(message)
        dlg.show()
    }

    private fun doSaveAll(uri: Uri?) {
        try {
            val bytes = saveLists(uri, Logic.listOfLists.asList)
            // Show result
            Toast.makeText(App.context, bytes.toString() + " " + getString(R.string.bytes_saved), Toast.LENGTH_LONG).show()
        } catch (e: Exception) { Utils.showErrorDialog(e) }
    }

    private fun doSaveOpenList(uri: Uri?) {
        Logic.openList.value?.let {
            val lists = ArrayList<List>()
            lists.add(it.list)
            try {
                val bytes = saveLists( uri, lists)
                // Show result
                Toast.makeText(App.context, bytes.toString() + " " + getString(R.string.bytes_saved), Toast.LENGTH_LONG).show()
            }catch (e:Exception){ Utils.showErrorDialog(e)}
        }
    }

    private fun doSaveOpenListXML(uri: Uri?) {
        try {
            val bytes = saveListToXML(Logic.openList.value!!.list, uri)
            // Show result
            Toast.makeText(App.context, bytes.toString() + " " + getString(R.string.bytes_saved), Toast.LENGTH_LONG).show()
        }catch (e:Exception){ Utils.showErrorDialog(e)}
    }

    //----------------------------------------------------------------------------------------------
    // FILE EXPORT/IMPORT UTILS
    private fun saveLists(uri: Uri?, lists: Collection<List>):Int{
        // Open
        val pfd = App.instance()!!.contentResolver.openFileDescriptor(uri!!, "w")
        val fileOutputStream = FileOutputStream(pfd!!.fileDescriptor)

        // Write
        val bytes = dataToJSON(lists).toString().toByteArray()
        fileOutputStream.write(bytes)

        // Close. Let the document provider know you're done by closing the stream.
        fileOutputStream.close()
        pfd.close()

        return bytes.size
    }

    private fun saveListToXML(list:List, uri: Uri?):Int{
        // Open
        val pfd = App.instance()!!.contentResolver.openFileDescriptor(uri!!, "w")
        val fileOutputStream = FileOutputStream(pfd!!.fileDescriptor)

        // Write
        val bytes = listToXML(list).toByteArray()
        fileOutputStream.write(bytes)

        // Close. Let the document provider know you're done by closing the stream.
        fileOutputStream.close()
        pfd.close()

        return bytes.size
    }

    private fun readJSON(uri: Uri?):JSONObject{
        // Open
        val pfd = App.context.contentResolver.openFileDescriptor(uri!!, "r")
        val reader = BufferedReader(FileReader(pfd!!.fileDescriptor))

        // Read
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        val text = sb.toString()

        // Close. Let the document provider know you're done by closing the stream.
        reader.close()
        pfd.close()

        // Parce data
        return JSONObject(text)
    }


    // DATA TO JSON
    @Throws(JSONException::class)
    private fun dataToJSON(lists: Collection<List>): JSONObject {
        val json = JSONObject()
        // Version
        val pInfo = App.instance()?.packageInfo
        if (pInfo != null) {
            json.put("versionCode", pInfo.versionCode)
            json.put("versionName", pInfo.versionName)
        }

        // LISTS
        val listsJSON = JSONArray()
        json.put("lists", listsJSON)
        for (list in lists) {
            listsJSON.put(listToJSON(list))
        }
        return json
    }

    // LOAD FROM JSON
    private class LoadEstimation {
        @JvmField
        var toInsert = 0
        @JvmField
        var toUpdate = 0
    }

    @Throws(JSONException::class)
    private fun estimateLoadingFromJSON(json: JSONObject): LoadEstimation {
        val estimation = LoadEstimation()
        // Version

        // LISTS
        val lists = json.optJSONArray("lists")
        if (lists != null) {
            for (i in 0 until lists.length()) {
                val listJSON = lists.optJSONObject(i) ?: continue

                // Verify list
                val id = listJSON.optLong("id", 0)
                val name = listJSON.optString("name", null)
                if (id == 0L || name == null || name.isEmpty()) continue

                // Search for existed
                val pos = Logic.listOfLists.getPositionById(id)

                // Update counters
                if (pos != null) estimation.toUpdate++ else estimation.toInsert++
            }
        }
        return estimation
    }

    @Throws(JSONException::class)
    private fun loadFromJSON(json: JSONObject) {
        // Version

        // LISTS
        val listsJSON = json.optJSONArray("lists")
        if (listsJSON != null) {
            // Fill existed lists hash
            val existed = HashSet<Long>()
            Logic.listOfLists.asList.forEach { list->
                existed.add(list.id)
            }

            // LOOP BY INPUT
            for (i in 0 until listsJSON.length()) {
                val listJSON = listsJSON.optJSONObject(i) ?: continue

                // Verify list
                val id = listJSON.optLong("id", 0)
                val name = listJSON.optString("name", null)
                if (id == 0L || name == null || name.isEmpty()) continue

                // Search for existed
                //var list = Logic.listOfLists.get(id)
                if(existed.contains(id)) continue

//                if (list != null) {
//                    // Remove old list (& items)
//                    Logic.listOfLists.remove(id)
//                }

                // Append list
                val list = List(id, name, null)
                Database.insertList(list)
                //Logic.listOfLists.put(list.id, list)
                // Insert list items
                val itemsJSON = listJSON.optJSONArray("items")
                if (itemsJSON != null) {
                    for (j in 0 until itemsJSON.length()) {
                        val itemJSON = itemsJSON.optJSONObject(j) ?: continue
                        val itemName = itemJSON.optString("name", null) ?: continue
                        // Update the database only because of the list is not loaded
                        Database.insertItem(
                                Item.create(
                                        list.id,
                                        itemName,
                                        itemJSON.optString("description", null)
                                )
                        )
                    }
                }
            }

            // Reload data
            Logic.listOfLists.load(
                Database.loadListOfLists()
            )

            // Update UI
            AMain.instance?.onDataUpdated()
        }
    }

    //----------------------------------------------------------------------------------------------
    // SINGLE LIST - EXPORT TO A FILE
    @Throws(IOException::class)
    fun listToXML(list:List): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        with(list) {
            sb.append("<list>\n")
            sb.append("<id>$id</id>\n")
            sb.append("<name>$name</name>\n")
            if (description != null && !description!!.isEmpty()) sb.append("<description>$description</description>\n")
            sb.append("<items>\n")
            val items = Database.loadListItems(id)
            for (item in items) {
                sb.append("<item>\n")
                sb.append("""<id>${item.id}</id>""".trimIndent())
                sb.append("""
        <name>${item.name}</name>
    
        """.trimIndent())
                if (item.description != null && !item.description!!.isEmpty()) sb.append("""
        <description>${item.description}</description>
    
        """.trimIndent())
                sb.append("""
        <state>${item.state}</state>
    
        """.trimIndent())
                sb.append("</item>\n")
            }

            sb.append("</items>\n")
            sb.append("</list>")
        }
        return sb.toString()
    }

    @Throws(IOException::class)
    fun listSaveXML(list:List, bw: BufferedWriter) {
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        bw.newLine()
        with(list) {
            bw.write("<list>")
            bw.newLine()
            bw.write("<id>$id</id>")
            bw.newLine()
            bw.write("<name>$name</name>")
            bw.newLine()
            if (description != null && !description!!.isEmpty()) bw.write("<description>$description</description>")
            bw.newLine()
            bw.write("<items>")
            bw.newLine()
            val items = Database.loadListItems(id)
            for (item in items) {
                bw.write("<item>")
                bw.newLine()
                bw.write("<id>" + item.id + "</id>")
                bw.newLine()
                bw.write("<name>" + item.name + "</name>")
                bw.newLine()
                if (item.description != null && !item.description!!.isEmpty()) bw.write("<description>" + item.description + "</description>")
                bw.newLine()
                bw.write("<state>" + item.state + "</state>")
                bw.newLine()
                bw.write("</item>")
                bw.newLine()
            }
            bw.write("</items>")
            bw.newLine()
            bw.write("</list>")
        }
    }

    @Throws(JSONException::class)
    fun listToJSON(list:List): JSONObject {
        val json = JSONObject()
        with(list) {
            json.put("id", id)
            json.put("name", name)
            if (description != null && !description!!.isEmpty()) json.put("description", description)
            // ITEMS
            val items = Database.loadListItems(id)
            val jsItems = JSONArray()
            for (item in items) {
                val js = JSONObject()
                js.put("id", item.id)
                js.put("name", item.name)
                if (item.description != null && !item.description!!.isEmpty()) js.put("description", item.description)
                js.put("state", item.state)
                jsItems.put(js)
            }
            json.put("items", jsItems)
        }
        return json
    }

}