package com.revolhope.data.feature.word.mapper

import com.revolhope.domain.feature.word.model.WordModel

object WordMapper {

    fun fromProcessedWordToModel(
        entry: Map.Entry<String, List<String>>,
        fileName: String?
    ): WordModel =
        WordModel(
            word = entry.key,
            occurrences = entry.value.count(),
            originalFileName = fileName ?: "",
            occurrencesWords = entry.value
        )

}