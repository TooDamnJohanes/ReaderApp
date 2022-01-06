package com.joaootavio.android.readerapp.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaootavio.android.readerapp.data.Resource
import com.joaootavio.android.readerapp.model.Item
import com.joaootavio.android.readerapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetaisViewModel @Inject constructor(private val repository: BookRepository): ViewModel() {

    suspend fun getBookInfo(bookId: String): Resource<Item> {
        return repository.getBookInfo(bookId = bookId)
    }

}