package com.revolhope.domain.feature.word.model

import java.util.UUID

data class WordModel(
    val id: String = UUID.randomUUID().toString(),
    val word: String,
    val occurrences: Int,
    val originalFileName: String
)
