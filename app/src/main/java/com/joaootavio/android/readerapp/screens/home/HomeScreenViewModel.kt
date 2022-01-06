package com.joaootavio.android.readerapp.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaootavio.android.readerapp.data.DataOrException
import com.joaootavio.android.readerapp.model.MBook
import com.joaootavio.android.readerapp.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: FireRepository
) : ViewModel() {

    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>> = mutableStateOf(
        DataOrException(listOf(), true, Exception(""))
    )

    init {
        getAllBooksFromDataBase()
    }

    fun getAllBooksFromDataBase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllBooksFromDataBase()
            if (!data.value.data.isNullOrEmpty()) {
                data.value.loading = false
            }
        }
    }
}