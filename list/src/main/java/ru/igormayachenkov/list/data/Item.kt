package ru.igormayachenkov.list.data

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: Item
data class Item(
        val id          : Long,
        val parent_id   : Long,
        //var syncState:Int,
        var state       : Int, // checked/unchecked
        var name        : String?,
        var description : String?
) {
    //-----------------------------------------
    // COMPARE FUNCTIONS FOR HASH TABLE
    override fun hashCode(): Int {
        return id.hashCode()
    }
    override fun equals(other: Any?): Boolean {
        if(other is Item) return id==other.id
        return false
    }
    //-----------------------------------------

    companion object {
        const val ITEM_STATE_CHECKED = 1
        fun create(parent_id:Long, name:String?, descr:String?):Item{
            return Item(
                    System.currentTimeMillis(),
                    parent_id,
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