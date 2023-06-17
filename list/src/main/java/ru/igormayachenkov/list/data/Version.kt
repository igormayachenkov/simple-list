package ru.igormayachenkov.list.data

import android.util.Log

private const val TAG = "myapp.App"


// Version
data class Version(
    val major: Int = 0,
    val minor: Int = 0,
    val patch: String = "unknown"
){
    companion object {
        fun fromString(string: String):Version{
            try {
                string.split(".", limit = 3).let {
                    return Version(
                        major = it[0].toInt(),
                        minor = it[1].toInt(),
                        patch = it[2]
                    )
                }
            }catch(e:Exception) {
                Log.e(TAG,"can't parse version from string: $string")
                throw Exception("can't parse version from string: $string")
                //return Version()
            }
        }
    }

    override fun toString(): String {
        return "$major.$minor.$patch"
    }

    infix fun isBelow(v:Version):Boolean =
        (major<v.major) ||
        (major==v.major && minor<v.minor)

}