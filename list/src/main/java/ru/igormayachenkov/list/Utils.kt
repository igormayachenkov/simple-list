package ru.igormayachenkov.list

object Utils {
    fun areEqual(a: String?, b: String?): Boolean {
        if (a != null && b != null)
            return a.compareTo(b) == 0
        if (a == null && b == null)
            return true
        return false
    }

    fun areNotEqual(a: String?, b: String?): Boolean {
        return !areEqual(a, b)
    }

}