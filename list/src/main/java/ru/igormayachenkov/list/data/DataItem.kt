package ru.igormayachenkov.list.data

////////////////////////////////////////////////////////////////////////////////////////////////
// ATOMIC DATA OBJECT
data class DataItem(
    val id  : Long,
    val parent_id   : Long,
    val type        : Type,
    val state       : State, // checked/unchecked
    val name        : String,
    val description : String?
){
    // TYPE FLAGS
    data class Type(
        val hasChildren : Boolean,
        val isCheckable : Boolean
    ){
        companion object {
            const val MASK_hasChildren = 0x01
            const val MASK_isCheckable = 0x10
        }
        constructor(int:Int):this(
            (int and MASK_hasChildren)>0,
            (int and MASK_isCheckable)>0
        )
        fun toInt():Int =
            (if(hasChildren) MASK_hasChildren else 0) or
                    (if(isCheckable) MASK_isCheckable else 0)
    }

    // STATE FLAGS
    data class State(
        val isChecked : Boolean
    ){
        companion object {
            const val MASK_isChecked = 0x01
        }
        constructor(int:Int):this(
            (int and MASK_isChecked)>0,
        )
        fun toInt():Int =
            (if(isChecked) MASK_isChecked else 0)
    }

}