package ru.igormayachenkov.list.data

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: List without items
data class List(
        val id      : Long,
        var name    : String,
        //var syncState = 0
        var description: String?
) {
    companion object {
        private const val TAG = "myapp.List"
    }

}