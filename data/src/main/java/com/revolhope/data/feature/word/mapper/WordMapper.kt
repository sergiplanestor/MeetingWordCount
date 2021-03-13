package com.revolhope.data.feature.word.mapper

import com.revolhope.domain.feature.word.model.WordModel
import java.io.File

object WordMapper {

    private var incrementalPosition: Int = -1
        get() {
            incrementalPosition = field + 1
            return field + 1
        }


    fun fromProcessedWordToModel(
        pair: Pair<String, Int>,
        fileName: String?
    ): WordModel =
        WordModel(
            word = pair.first,
            position = incrementalPosition,
            occurrences = pair.second + 1, // +1 is the entry.key
            originalFileName = parseFileName(fileName)
        )

    private fun parseFileName(fileName: String?): String =
        fileName?.substring(fileName.lastIndexOf(File.separator) + 1) ?: ""
}