package ru.igormayachenkov.list.data

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: Item
data class DItem(
        val id:Long
) {
    @JvmField
    var syncState = 0
    @JvmField
    var state = 0
    @JvmField
    var name: String? = null
    @JvmField
    var description: String? = null

    fun changeState() {
        // Change
        state = if (state == ITEM_STATE_CHECKED) 0 else ITEM_STATE_CHECKED
        // Save
        Database.updateItemState(id, state)
    }

    companion object {
        const val ITEM_STATE_CHECKED = 1
    }
}