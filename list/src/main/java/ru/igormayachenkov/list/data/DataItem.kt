package ru.igormayachenkov.list.data

// type
const val TYPE_LIST = 100
const val TYPE_ITEM = 200


////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: Item
data class DataItem(
    val id  : Long,
    val parent_id   : Long,
    val type        : Int,
    var state       : Int, // checked/unchecked
    var name        : String,
    var description : String?
) {
    val hasChildren:Boolean = (type==TYPE_LIST)

    //-----------------------------------------
    // COMPARE FUNCTIONS FOR HASH TABLE
//    override fun hashCode(): Int {
//        return id.hashCode()
//    }
//    override fun equals(other: Any?): Boolean {
//        if(other is Item) return id==other.id
//        return false
//    }
    //-----------------------------------------

    companion object {
        const val ITEM_STATE_CHECKED = 1
        fun create(parent_id:Long, name:String="", descr:String?=null):DataItem{
            return DataItem(
                    System.currentTimeMillis(),
                    parent_id,
                    TYPE_ITEM,
                    0,
                    name,
                    descr
            )
        }
    }

    var isChecked : Boolean
        get() { return state==ITEM_STATE_CHECKED }
        set(value)  {  state = if(value) ITEM_STATE_CHECKED else 0 }

    fun toggleState() {
        state = if (state==ITEM_STATE_CHECKED) 0 else ITEM_STATE_CHECKED
    }


}