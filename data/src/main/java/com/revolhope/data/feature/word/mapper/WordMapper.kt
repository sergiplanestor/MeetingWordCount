package com.revolhope.data.feature.word.mapper

import com.revolhope.domain.feature.word.model.WordModel
import java.io.File

object WordMapper {

    fun fromProcessedWordToModel(
        entry: Map.Entry<String, List<String>>,
        fileName: String?
    ): WordModel =
        WordModel(
            word = entry.key,
            occurrences = entry.value.count() + 1, // +1 is the entry.key
            originalFileName = parseFileName(fileName)
        )

    private fun parseFileName(fileName: String?): String =
        fileName?.substring(fileName.lastIndexOf(File.separator) + 1) ?: ""
}