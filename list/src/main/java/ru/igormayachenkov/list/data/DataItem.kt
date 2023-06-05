package ru.igormayachenkov.list.data

import org.json.JSONObject
import ru.igormayachenkov.list.UtilsJSON

////////////////////////////////////////////////////////////////////////////////////////////////
// ATOMIC DATA OBJECT
data class DataItem(
    val id  : Long,
    val parent_id   : Long,
    val type        : Type,
    val state       : State, // checked/unchecked
    val name        : String,
    val description : String?
){
    val logString:String = "#$id $name"

    // TYPE FLAGS
    data class Type(
        val hasChildren : Boolean,
        val isCheckable : Boolean
    ){
        // to/from INT
        companion object {
            const val MASK_hasChildren = 0x01
            const val MASK_isCheckable = 0x10
        }
        fun toInt():Int =
            (if(hasChildren) MASK_hasChildren else 0) or
                    (if(isCheckable) MASK_isCheckable else 0)
        constructor(int:Int):this(
            (int and MASK_hasChildren)>0,
            (int and MASK_isCheckable)>0
        )

        // to/from JSON
        fun toJSON():JSONObject{
            return JSONObject()
            .put("hasChildren", hasChildren)
            .put("isCheckable", isCheckable)
        }
        constructor(json:JSONObject):this(
            json.getBoolean("hasChildren"),
            json.getBoolean("isCheckable")
        )

    }

    // STATE FLAGS
    data class State(
        val isChecked : Boolean
    ){
        // to/from INT
        companion object {
            const val MASK_isChecked = 0x01
        }
        fun toInt():Int =
            (if(isChecked) MASK_isChecked else 0)
        constructor(int:Int):this(
            (int and MASK_isChecked)>0,
        )

        // to/from JSON
        fun toJSON():JSONObject{
            return JSONObject()
                .put("isChecked", isChecked)
        }
        constructor(json:JSONObject):this(
            json.getBoolean("isChecked")
        )
    }

    // to/from JSON
    fun toJSON():JSONObject{
        return JSONObject()
            .put    ("id",          id)
            // without parent_id
            .put    ("type",        type.toJSON())
            .put    ("state",       state.toJSON())
            .put    ("name",        name)
            .putOpt ("description", description)
    }
    constructor(json:JSONObject, parent_id: Long):this(
            json.getLong("id"),
            parent_id,
            Type (json.getJSONObject("type")),
            State(json.getJSONObject("state")),
            json.getString("name"),
            UtilsJSON.getStringOrNull(json,"description")
    )

}