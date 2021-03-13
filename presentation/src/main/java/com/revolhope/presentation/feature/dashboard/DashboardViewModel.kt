package com.revolhope.presentation.feature.dashboard

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.domain.feature.word.usecase.ClearWordsUseCase
import com.revolhope.domain.feature.word.usecase.FetchWordsUseCase
import com.revolhope.domain.feature.word.usecase.StoreWordsUseCase
import com.revolhope.presentation.R
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
    private val fetchWordsUseCase: FetchWordsUseCase,
    private val clearWordsUseCase: ClearWordsUseCase,
) : BaseViewModel() {

    companion object {
        private val regexSplit = Regex("[\\s]")
        private const val ITEMS_PER_PAGE = 50
    }

    // Inner
    private var currentPage: Int = 1
    var currentSortType: SortType? = null
    var currentFilter: String = ""

    private var originalWords: List<WordModel> = emptyList()
    private val limit get() = currentPage * ITEMS_PER_PAGE

    // LiveData
    val errorLiveData: LiveData<String> get() = _errorLiveData

    val loaderLiveData get() = _loaderLiveData
    private val _loaderLiveData = MutableLiveData<Boolean>()

    val wordsLiveData: LiveData<List<WordModel>> get() = _wordsLiveData
    private val _wordsLiveData = MutableLiveData<List<WordModel>>()

    val wordCountLiveData: LiveData<Pair<Int, Int>> get() = _wordCountLiveData
    private val _wordCountLiveData = MutableLiveData<Pair<Int, Int>>()

    fun processFileData(context: Context, data: Intent, resolver: ContentResolver) {
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
                    }.also { handleState(state = it, onSuccess = ::onWordsReceived) }
                } else {
                    _errorLiveData.value = context.getString(R.string.generic_error)
                }
            }
        } else {
            _errorLiveData.value = context.getString(R.string.generic_error)
        }
    }

    fun fetchNextPage() {
        if (currentFilter.isNotBlank()) return
        currentPage++
        launch {
            loaderLiveData.value = true
            withContext(Dispatchers.IO) {
                fetchWordsUseCase.invoke(
                    FetchWordsUseCase.Request(limit)
                )
            }.also { handleState(state = it, onSuccess = ::onWordsReceived) }
        }
    }

    fun notifyWords(words: List<WordModel> = originalWords) {
        _wordsLiveData.value = filterAndSort(words)
        _wordCountLiveData.value = originalWords.sumBy { it.occurrences } to originalWords.count()
        _loaderLiveData.value = false
    }

    fun clearWords() {
        launch {
            withContext(Dispatchers.IO) {
                clearWordsUseCase.invoke()
            }.also {
                handleState(
                    state = it,
                    onSuccess = {
                        currentPage = 1
                        currentSortType = null
                        currentFilter = ""
                        onWordsReceived(emptyList())
                    }
                )
            }
        }
    }

    private fun applyFilter(words: List<WordModel>, query: String): List<WordModel> =
        if (query.isBlank()) {
            words
        } else {
            words.filter { it.word.contains(query, ignoreCase = true) }
        }

    private fun applySort(words: List<WordModel>, sortType: SortType?): List<WordModel> {
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

    private fun onWordsReceived(words: List<WordModel>) {
        originalWords = words
        notifyWords(words)
    }

    private fun filterAndSort(words: List<WordModel>): List<WordModel> =
        applyFilter(applySort(words, currentSortType), currentFilter)
}