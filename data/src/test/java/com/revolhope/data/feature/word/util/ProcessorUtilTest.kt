package com.revolhope.data.feature.word.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ProcessorUtilTest {

    private val fileContent: List<String> = listOf(" ", "Hello", " ", "World:", "\n", "This", "is",
        "a", "test", "of", "11", "words", "repeated", "repeated", "repeated", "repeated", " ", ",", "world")


    @Test
    fun clearWord_test1() {
        val wordCleared = WordProcessorUtil.clearWord("Hello:")
        assertEquals("Hello", wordCleared)
    }

    @Test
    fun clearWord_test2() {
        val wordCleared = WordProcessorUtil.clearWord(" Hello?")
        assertEquals("Hello", wordCleared)
    }

    @Test
    fun clearApostrophe_test1() {
        val wordCleared = WordProcessorUtil.clearApostrophe("Alice's")
        assertEquals("Alice", wordCleared)
    }

    @Test
    fun clearApostrophe_test2() {
        val wordCleared = WordProcessorUtil.clearApostrophe("ALICE'S")
        assertEquals("ALICE", wordCleared)
    }

    @Test
    fun areSameWord_test1() {
        val areSame = WordProcessorUtil.areSameWord("ALICE", "ALICE")
        assert(areSame)
    }

    @Test
    fun areSameWord_test2() {
        val areSame = WordProcessorUtil.areSameWord("hello", "ALICE")
        assertFalse(areSame)
    }

    @Test
    fun areSameWord_test3() {
        val areSame = WordProcessorUtil.areSameWord("Alice's", "ALICE")
        assert(areSame)
    }

    @Test
    fun filterWords_test1() {
        val result = WordProcessorUtil.filterWords(fileContent)
        assertEquals(11, result.count())
    }

    @Test
    fun filterWords_test2() {
        val result = WordProcessorUtil.filterWords(fileContent)
        assertEquals(4, result.find { it.first == "repeated" }?.second)
    }

    @Test
    fun filterWords_test3() {
        val result = WordProcessorUtil.filterWords(fileContent)
        assertEquals(2, result.find { it.first.equals("world", ignoreCase = true) }?.second)
    }
}