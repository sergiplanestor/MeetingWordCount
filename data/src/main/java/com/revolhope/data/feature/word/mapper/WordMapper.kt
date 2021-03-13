package com.revolhope.data.feature.word.mapper

import com.revolhope.domain.feature.word.model.WordModel
import java.io.File

/**
 * Mapper to parse from raw word model (String) to domain model [WordModel].
 */
object WordMapper {

    /**
     * Auto incremental property to define position of word in file.
     */
    private var incrementalPosition: Int = -1
        get() {
            incrementalPosition = field + 1
            return field + 1
        }

    /**
     * Parse from given pair to [WordModel].
     * @param pair of word as first param and number of occurrences of it as second.
     * @param fileName file name.
     * @return [WordModel].
     */
    fun fromProcessedWordToModel(
        pair: Pair<String, Int>,
        fileName: String?
    ): WordModel =
        WordModel(
            word = pair.first,
            position = incrementalPosition,
            occurrences = pair.second,
            originalFileName = parseFileName(fileName)
        )

    /**
     * Parse fileName from string received from natives picker.
     * @param fileName string received from native picker.
     * @return file name to display.
     */
    private fun parseFileName(fileName: String?): String =
        fileName?.substring(fileName.lastIndexOf(File.separator) + 1) ?: ""
}