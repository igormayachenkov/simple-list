package ru.igormayachenkov.list.data

data class EditableData(
    val isNew:Boolean,
    val id:Long,
    val name:String,
    val descr:String
){
    constructor(isNew: Boolean, dataItem: DataItem):this(
        isNew,
        id = dataItem.id,
        name = dataItem.name,
        descr = dataItem.description ?: ""
    )
}
