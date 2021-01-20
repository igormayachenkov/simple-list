package ru.igormayachenkov.list.data

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: Item
data class Item(
        val id      : Long,
        //var syncState:Int,
        var state   : Int, // checked/unchecked
        var name: String?,
        var description: String?
) {
    companion object {
        const val ITEM_STATE_CHECKED = 1
    }

    fun changeState() {
        // Change
        state = if (state==ITEM_STATE_CHECKED) 0 else ITEM_STATE_CHECKED
        // Save
        Database.updateItemState(id, state)
    }


}