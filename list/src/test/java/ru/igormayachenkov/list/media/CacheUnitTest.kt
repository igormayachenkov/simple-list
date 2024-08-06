package ru.igormayachenkov.list.media

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CacheUnitTest {
    private val SIZELIMIT = 5
    private val cache = Cache<String>(SIZELIMIT)

    init {
    }

    @Test
    fun initialSize_isZero() {
        assertEquals(0, cache.size)
    }

    @Test
    fun cache_works() {
        // Put 3 elements
        cache.put(1L,"one")
        cache.put(2L,"two")
        cache.put(3L,"three")
        assertEquals(3, cache.size)

        // Get the existed elements
        assertEquals("one",  cache.get(1L))
        assertEquals("two",  cache.get(2L))
        assertEquals("three",cache.get(3L))

        // Get un existed elements
        assertEquals(null,  cache.get(13L))

        // Fill over the max size
        cache.put(4L,"four")
        cache.put(5L,"five")
        cache.put(6L,"six")
        cache.put(7L,"seven")

        // Size is the max size
        assertEquals(SIZELIMIT, cache.size)

        // Check the elements
        assertEquals(null,      cache.get(1L)) // removed
        assertEquals(null,      cache.get(2L)) // removed
        assertEquals("three",   cache.get(3L))
        assertEquals("four",    cache.get(4L))
        assertEquals("five",    cache.get(5L))
        assertEquals("six",     cache.get(6L))
        assertEquals("seven",   cache.get(7L))
    }
}