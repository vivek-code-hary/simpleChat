package com.example.ashrut.findx.data.utils


fun String.cleanMessage(): String {
    return this
        .lines()
        .dropWhile { it.isBlank() }      // leading empty lines
        .dropLastWhile { it.isBlank() }  // trailing empty lines
        .joinToString("\n")
}
