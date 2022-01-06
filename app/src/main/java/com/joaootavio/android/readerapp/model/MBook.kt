package com.joaootavio.android.readerapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class MBook(
    @Exclude
    var id: String? = null,

    var title: String? = null,
    var authors: String? = null,
    var notes: String? = null,

    @get:PropertyName("book_photo_url")
    @set:PropertyName("book_photo_url")
    var photoUrl: String? = null,

    var categories: String? = null,

    @get:PropertyName("book_published_date")
    @set:PropertyName("book_published_date")
    var publishedDate: String? = null,

    var rating: Double? = null,
    var description: String? = null,

    @get:PropertyName("book_page_count")
    @set:PropertyName("book_page_count")
    var pageCount: String? = null,

    @get:PropertyName("book_start_reading")
    @set:PropertyName("book_start_reading")
    var startReading: Timestamp? = null,

    @get:PropertyName("book_finished_reading")
    @set:PropertyName("book_finished_reading")
    var finishedReading: Timestamp? = null,

    @get:PropertyName("book_user_id")
    @set:PropertyName("book_user_id")
    var userId: String? = null,

    @get:PropertyName("book_google_book_id")
    @set:PropertyName("book_google_book_id")
    var googleBookId: String? = null,

    @get:PropertyName("book_fav_book")
    @set:PropertyName("book_fav_book")
    var favBook: Boolean = false
)
