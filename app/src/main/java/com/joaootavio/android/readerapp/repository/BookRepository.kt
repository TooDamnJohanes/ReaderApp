package com.joaootavio.android.readerapp.repository

import com.joaootavio.android.readerapp.data.DataOrException
import com.joaootavio.android.readerapp.data.Resource
import com.joaootavio.android.readerapp.model.Item
import com.joaootavio.android.readerapp.network.BooksApi
import retrofit2.Response
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: BooksApi) {

    suspend fun getBooks(searchQuery: String): Resource<List<Item>> {
        val itemList = try {
            Resource.Loading(true)
             api.getAllBooks(searchQuery).items
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        Resource.Loading(false)
        return Resource.Success(itemList)
    }

    suspend fun getBookInfo(bookId: String): Resource<Item> {
        val response =  try {
            Resource.Loading(true)
            api.getBookInfo(bookId = bookId)

        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
        Resource.Loading(false)
        return Resource.Success(response)
    }
}