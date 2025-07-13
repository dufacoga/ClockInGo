package com.example.clockingo.presentation.utils

import android.util.Base64

fun String.toBase64(): String =
    Base64.encodeToString(this.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

fun String.toBase16(): String =
    this.toByteArray(Charsets.UTF_8).joinToString("") { "%02x".format(it) }