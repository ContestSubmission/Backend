package com.github.contestsubmission.backend.util

import java.util.*

fun String.toUUID(): UUID? = UUID.fromString(this)
