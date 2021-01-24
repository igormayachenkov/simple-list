package ru.igormayachenkov.list.settings

object BodyAction{
    const val OPEN    = "open"
    const val CHECK   = "check"
    const val NOTHING = "nothing"
    const val default = OPEN
}

object ColumnsNumnber{
    const val ONE    = "open"
    const val TWO    = "two"
    const val THREE  = "three"
    const val default = ONE
    fun getNumber(value:String):Int{
        return when(value){
            ONE    -> 1
            TWO    -> 2
            THREE  -> 3
            else   -> 1
        }
    }
}