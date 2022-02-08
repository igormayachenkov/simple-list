package ru.igormayachenkov.list.data

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: List + item hash + item sorted list
class OpenList(
    val list:List,
    itemsCollection : Collection<Item>
){
    companion object {
        private const val TAG = "myapp.OpenList"
    }

    //private val hash : HashSet<Item> = itemsHash// Item Hash (data object)
    private val sortedItems = ArrayList<Item>()           // Sorted item list for UI

    init {
        sortedItems.addAll(itemsCollection)
        updateSortOrder()
    }

    //
    val id   : Long     get() = list.id
    var name : String   get() = list.name
                        set(value) {list.name = value}
    fun openItemById(itemId:Long):OpenItem?{
        sortedItems.forEachIndexed { pos, item->
            if(item.id==itemId) return OpenItem(item,pos)
        }
        return null
    }

    val items : kotlin.collections.List<Item>
        get() = sortedItems // sorted items

    fun updateSortOrder(){
        sortedItems.sortBy { it.name }
    }

    //----------------------------------------------------------------------------------------------
    // MODIFIERS
    fun insertItem(item:Item){
        sortedItems.add(item)
        updateSortOrder()
    }
    fun deleteItem(item:Item, pos:Int){
        sortedItems.removeAt(pos)
    }
}