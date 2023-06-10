package ru.igormayachenkov.list.data

enum class SortOrder{
    NameAsc, NameDesc
}

//--------------------------------------------------------------------------------------------------
// COMPARATORS
fun getDataItemComparator(settings: Settings):Comparator<DataItem>{
    return when(settings.sortOrder) {
        SortOrder.NameAsc  -> DataItemComparator(ascending = true,  settings = settings)
        SortOrder.NameDesc -> DataItemComparator(ascending = false, settings = settings)
    }
}

class DataItemComparator(
    val ascending  : Boolean,
    val settings   : Settings
) : Comparator<DataItem>{
    override fun compare(a: DataItem?, b: DataItem?): Int {
        if(a==null||b==null) return 0
        // Move lists up
        if(settings.sortListsUp) {
            if (a.type.hasChildren && !b.type.hasChildren) return -1
            if (!a.type.hasChildren && b.type.hasChildren) return  1
        }
        // Move checked items down
        if(settings.sortCheckedDown) {
            if (a.state.isChecked && !b.state.isChecked) return 1
            if (!a.state.isChecked && b.state.isChecked) return -1
        }
        // Compare by name
        val res = a.name.compareTo(b.name)
        return if(ascending) res else -res
    }
}
