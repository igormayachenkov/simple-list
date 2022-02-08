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

    class ItemArray : SortedArray<Item>(){
        override fun updateSortOrder() {
        array.sortBy { it.name }        }
    }
    val items = ItemArray()

    init{
        items.load(itemsCollection)
    }


    val id   : Long     get() = list.id
    var name : String   get() = list.name
                        set(value) {list.name = value}
    fun openItemById(itemId:Long):OpenItem?{
        items.asList.forEachIndexed { pos, item->
            if(item.id==itemId) return OpenItem(item,pos)
        }
        return null
    }

}