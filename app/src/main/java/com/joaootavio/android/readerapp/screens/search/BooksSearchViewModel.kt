package com.joaootavio.android.readerapp.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaootavio.android.readerapp.data.Resource
import com.joaootavio.android.readerapp.model.Item
import com.joaootavio.android.readerapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksSearchViewModel @Inject constructor(private val repository: BookRepository) : ViewModel() {
    var list: List<Item> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(false)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        //searchBooks(query = "")
    }

    fun searchBooks(query: String) {
        isLoading = true
        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty()) {
                return@launch
            }
            try {
                when(val response = repository.getBooks(query)) {
                    is Resource.Success -> {
                        list = response.data!!
                        if (list.isNotEmpty()) {
                            isLoading = false
                        }
                    }
                    is Resource.Error -> {
                        isLoading = false
                        Log.e("Network", "searchBooks: Faiou")
                    }
                    else -> {isLoading = false}
                }
            } catch (e: Exception) {
                isLoading = false
                Log.e("Network", "searchBooks: ${e.message.toString()}")
            }
        }
    }
}