package ru.igormayachenkov.list.data

import java.util.HashMap

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: List + item hash + item sorted list
class OpenList(
    val list:List,
    itemHash : HashMap<Long, Item>
){
    private val hash : HashMap<Long, Item> = itemHash // Item Hash (data object)
    val items = ArrayList<Item>()           // Sorted item list for UI

    init {
        updateItems()
    }

    companion object {
        private const val TAG = "myapp.OpenList"
    }

    val id   : Long     get() = list.id
    var name : String   get() = list.name
                        set(value) {list.name = value}

    fun itemById(itemId:Long):Item?{ return hash.get(itemId) }

    fun updateItems(){
        items.clear()
        items.addAll(hash.values)
        items.sortBy { it.name }
    }

    fun getItemListPosition(item:Item):Int?{
        items.forEachIndexed{index,element->
            if(element.id==item.id)
                return index
        }
        return null
    }

    //----------------------------------------------------------------------------------------------
    // MODIFIERS
    fun insertItem(item:Item){
        hash.put(item.id, item)
        updateItems()
    }
    fun deleteItem(item:Item):Int?{
        hash.remove(item.id)
        getItemListPosition(item)?.let { pos->
            items.removeAt(pos)
            return pos
        }
        return null
    }
}