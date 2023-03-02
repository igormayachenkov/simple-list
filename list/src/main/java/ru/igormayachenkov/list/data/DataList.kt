package ru.igormayachenkov.list.data

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: List without items
data class DataList(
    override val id      : Long,
    var name    : String,
        //var syncState = 0
    var description: String?
):Element {
    companion object {
        private const val TAG = "myapp.List"
    }

}