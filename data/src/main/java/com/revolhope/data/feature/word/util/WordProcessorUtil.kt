package com.revolhope.data.feature.word.util

import androidx.annotation.VisibleForTesting

/**
 * Util object to process words.
 */
object WordProcessorUtil {

    // Regex to clear words. Given 'hello:' it will result in 'hello'
    private val regexClearWord = Regex("[^\\w]")
    // Regex to clear words. Given 'Alice's' it will result in 'Alice'
    private val regexApostrophe = Regex("'.*")

    /**
     * Filters the word list removing repeated words.
     * @return [List] containing [Pair] of word cleared as string and number of occurrences as Int.
     */
    fun filterWords(words: List<String>): List<Pair<String, Int>> {
        val results = mutableListOf<Pair<String, Int>>()
        words.forEach { word ->
            if (word.isNotBlank() && !results.any { areSameWord(it.first, word) }) {
                results.add(clearWord(word) to words.count { areSameWord(it, word) })
            }
        }
        return results
    }

    /**
     * Clears word using [regexClearWord].
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun clearWord(word: String): String = word.replace(
        regex = regexClearWord,
        replacement = ""
    )

    /**
     * Clears word using [regexApostrophe].
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun clearApostrophe(word: String): String = word.replace(
        regex = regexApostrophe,
        replacement = ""
    )

    /**
     * Checks if two words are the same.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun areSameWord(word: String, anotherWord: String) =
        clearWord(word).equals(other = clearWord(anotherWord), ignoreCase = true) ||
                clearApostrophe(word).equals(other = clearApostrophe(anotherWord), ignoreCase = true)

}