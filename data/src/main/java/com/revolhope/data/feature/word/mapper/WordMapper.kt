package com.revolhope.data.feature.word.mapper

import com.revolhope.domain.feature.word.model.WordModel
import java.io.File

object WordMapper {

    fun fromProcessedWordToModel(
        pair: Pair<String, Int>,
        fileName: String?
    ): WordModel =
        WordModel(
            word = pair.first,
            occurrences = pair.second + 1, // +1 is the entry.key
            originalFileName = parseFileName(fileName)
        )

    private fun parseFileName(fileName: String?): String =
        fileName?.substring(fileName.lastIndexOf(File.separator) + 1) ?: ""
}