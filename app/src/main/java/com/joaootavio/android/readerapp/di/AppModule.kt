package com.joaootavio.android.readerapp.di

import com.google.firebase.firestore.FirebaseFirestore
import com.joaootavio.android.readerapp.network.BooksApi
import com.joaootavio.android.readerapp.repository.FireRepository
import com.joaootavio.android.readerapp.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFireBookRepository () = FireRepository(queryBook = FirebaseFirestore
        .getInstance().collection("books"))

    @Singleton
    @Provides
    fun providesBookApi(): BooksApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }
}