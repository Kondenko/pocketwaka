package com.kondenko.pocketwaka.utils.extensions

import com.kondenko.pocketwaka.utils.types.Either
import com.kondenko.pocketwaka.utils.types.left
import com.kondenko.pocketwaka.utils.types.right

fun String.replace(replacement: (Int, Char) -> Either<String, Char>): String {
    val sb = StringBuilder()
    forEachIndexed { index, char ->
        sb.append(replacement(index, char))
    }
    return sb.toString()
}

fun String.toSnakeCase(): String = replace { index, c ->
    if (c.isUpperCase()) {
        val peviousChar = this.getOrNull(index - 1)
        if (peviousChar != null && peviousChar != '_') {
            ("_" + c.toLowerCase()).left()
        } else {
            c.toLowerCase().right()
        }
    } else {
        c.right()
    }
}

fun String.isValidUrl() = this.matches(Regex("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$"))