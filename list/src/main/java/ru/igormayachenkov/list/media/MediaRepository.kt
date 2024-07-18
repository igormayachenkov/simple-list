package ru.igormayachenkov.list.media

class MediaRepository {
    private val store = HashMap<Long,MediaFile>()

    fun getMediaForItem(itemId:Long):MediaFile {
        return store.get(itemId) ?: run{
            // Create new item
            MediaFile(itemId).apply {
                store.put(itemId, this)
            }
        }
    }
}