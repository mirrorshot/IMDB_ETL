package it.mirrorshot.imdb_etl

fun String.clear(): String? = if (this == "\\N") null else this

fun String.asBoolean(): Boolean = when (this) {
    "1" -> true
    "0" -> false
    else -> throw IllegalArgumentException("this")
}

fun String.asBooleanOrNull(): Boolean? = when (this) {
    "1" -> true
    "0" -> false
    else -> throw IllegalArgumentException("this")
}