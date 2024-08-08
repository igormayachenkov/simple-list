package ru.igormayachenkov.media

import java.util.TreeMap

class Cache<T>(val sizeLimit:Int) {
    private val map  = HashMap<Long,T>()
    private var age  = TreeMap<Int, Long>()
    private var time = 0 // current "timestamp"

    fun put(id:Long, value:T){
        age.put(++time, id)
        map.put(id,value)
        // Limit the size
        if(map.size>sizeLimit){
            map.remove(
                with(age){ remove(firstKey()) }
            )
        }
    }

    fun get(id:Long):T?{
        return map[id]
    }

    val size:Int
        get() = map.size
}