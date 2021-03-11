package com.revolhope.domain.feature.word.model

data class WordModel(
    val word: String,
    val occurrences: Int,
    val originalFileName: String,
    val occurrencesWords: List<String>
)
