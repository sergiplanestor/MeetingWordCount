package com.revolhope.presentation.feature.dashboard

import android.content.ContentResolver
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.domain.feature.word.usecase.FetchWordsUseCase
import com.revolhope.domain.feature.word.usecase.StoreWordsUseCase
import com.revolhope.presentation.feature.dashboard.sort.SortType
import com.revolhope.presentation.library.base.BaseViewModel
import com.revolhope.presentation.library.extensions.readAsync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val storeWordsUseCase: StoreWordsUseCase,
    private val fetchWordsUseCase: FetchWordsUseCase
) : BaseViewModel() {

    companion object {
        private val regexSplit = Regex("[\\s]")
        private const val ITEMS_PER_PAGE = 50
    }

    // Inner
    private var currentPage: Int = 1
    var currentSortType: SortType? = null
    private var currentFilter: String = ""

    private var originalWords: List<WordModel> = emptyList()
    private val limit get() = currentPage * ITEMS_PER_PAGE
    private var isListFiltered: Boolean = false

    // LiveData
    val errorLiveData: LiveData<String> get() = _errorLiveData

    val loaderLiveData get() = _loaderLiveData
    private val _loaderLiveData = MutableLiveData<Boolean>()

    val wordsLiveData: LiveData<List<WordModel>> get() = _wordsLiveData
    private val _wordsLiveData = MutableLiveData<List<WordModel>>()

    val wordCountLiveData: LiveData<Pair<Int, Int>> get() = _wordCountLiveData
    private val _wordCountLiveData = MutableLiveData<Pair<Int, Int>>()

    fun processFileData(data: Intent, resolver: ContentResolver) {
        if (data.data != null) {
            val uri = data.data
            launch {
                loaderLiveData.value = true
                val content = resolver.readAsync(uri)
                if (content != null) {
                    withContext(Dispatchers.IO) {
                        storeWordsUseCase.invoke(
                            StoreWordsUseCase.Request(
                                rawWords = content.split(regexSplit),
                                fileName = uri?.lastPathSegment
                            )
                        )
                    }.also { handleState(state = it, onSuccess = ::notifyWords) }
                } else {
                    // TODO: Change literal
                    _errorLiveData.value = "T_Generic error"
                }
            }
        } else {
            // TODO: Change literal
            _errorLiveData.value = "T_Generic error"
        }
    }

    fun fetchNextPage() {
        if (isListFiltered) return
        currentPage++
        launch {
            loaderLiveData.value = true
            withContext(Dispatchers.IO) {
                fetchWordsUseCase.invoke(
                    FetchWordsUseCase.Request(limit)
                )
            }.also { handleState(state = it, onSuccess = ::notifyWords) }
        }
    }

    fun applyFilter(words: List<WordModel> = originalWords, query: String): List<WordModel> {
        isListFiltered = query.isNotBlank()
        currentFilter = query
        return if (query.isBlank()) {
            words
        } else {
            words.filter { it.word.contains(query, ignoreCase = true) }
        }
    }

    fun applySort(words: List<WordModel> = originalWords, sortType: SortType?): List<WordModel> {
        currentSortType = sortType
        return sortType?.let { type ->
            words.sortedWith(
                when (type) {
                    SortType.POSITION -> {
                        compareBy { it.position }
                    }
                    SortType.POSITION_REVERSED -> {
                        compareByDescending { it.position }
                    }
                    SortType.ALPHABETIC_ASCENDING -> {
                        compareBy { it.word }
                    }
                    SortType.ALPHABETIC_DESCENDING -> {
                        compareByDescending { it.word }
                    }
                    SortType.OCCURRENCES_ASCENDING -> {
                        compareBy { it.occurrences }
                    }
                    SortType.OCCURRENCES_DESCENDING -> {
                        compareByDescending { it.occurrences }
                    }
                }
            )
        } ?: words
    }

    private fun notifyWords(words: List<WordModel>) {
        originalWords = words
        _wordsLiveData.value = filterAndSort(words)
        _wordCountLiveData.value = originalWords.sumBy { it.occurrences } to originalWords.count()
        _loaderLiveData.value = false
    }

    private fun filterAndSort(words: List<WordModel>): List<WordModel> =
        applySort(words, currentSortType).let { applyFilter(it, currentFilter) }
}