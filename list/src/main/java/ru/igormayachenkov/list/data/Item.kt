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


    fun changeState() {
        // Change
        state = if (state==ITEM_STATE_CHECKED) 0 else ITEM_STATE_CHECKED
        // Save
        Database.updateItemState(id, state)
    }


}