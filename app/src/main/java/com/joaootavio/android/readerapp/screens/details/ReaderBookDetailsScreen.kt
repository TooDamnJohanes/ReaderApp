package com.joaootavio.android.readerapp.screens.details

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.joaootavio.android.readerapp.components.ReaderAppBar
import com.joaootavio.android.readerapp.components.RoundedButton
import com.joaootavio.android.readerapp.data.Resource
import com.joaootavio.android.readerapp.model.Item
import com.joaootavio.android.readerapp.model.MBook
import com.joaootavio.android.readerapp.navigation.ReaderScreens

@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: String,
    viewModel: DetaisViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = "Book Details",
            navController = navController,
            icon = Icons.Default.ArrowBack,
            showProfiles = false
        ) {
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
    }) {
        Surface(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = viewModel.getBookInfo(bookId = bookId)
                }.value

                if (bookInfo.data == null) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading...",
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                } else {
                    ShowDetails(bookInfo, navController)
                }

            }
        }
    }
}

@Composable
fun ShowDetails(
    bookInfo: Resource<Item>,
    navController: NavController
) {
    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id

    Card(
        modifier = Modifier
            .padding(34.dp),
        shape = CircleShape,
        elevation = 4.dp
    ) {
        Image(
            painter = rememberImagePainter(data = bookData?.imageLinks?.thumbnail.toString()),
            contentDescription = "Book image",
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .padding(1.dp)
        )
    }
    Text(
        text = bookData?.title ?: "",
        style = MaterialTheme.typography.h6,
        overflow = TextOverflow.Ellipsis,
        maxLines = 20,
        textAlign = TextAlign.Center
    )
    Column(
        modifier = Modifier
            .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Authors: ${bookData?.authors ?: ""}")
        Text(text = "Page Count: ${bookData?.pageCount ?: ""}")
        Text(
            text = "Categories: ${bookData?.categories ?: ""}",
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Published: ${bookData?.publishedDate ?: ""}",
            style = MaterialTheme.typography.subtitle1
        )
    }
    Spacer(modifier = Modifier.height(5.dp))

    val localDims = LocalContext.current.resources.displayMetrics

    Surface(
        modifier = Modifier
            .height(localDims.heightPixels.dp.times(0.09f))
            .padding(5.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(0.8.dp, Color.DarkGray)
    ) {
        val cleanDescription =
            HtmlCompat.fromHtml(bookData?.description ?: "Empty Description", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        LazyColumn(
            modifier = Modifier
                .padding(5.dp)
        ) {
            item {
                Text(
                    text = cleanDescription,
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        RoundedButton(
            label = "Save"
        ) {
            val book = MBook(
                title = bookData?.title,
                authors = bookData?.authors.toString(),
                description = bookData?.description,
                categories = bookData?.categories.toString(),
                notes = "",
                photoUrl = bookData?.imageLinks?.thumbnail,
                publishedDate = bookData?.publishedDate,
                pageCount = bookData?.pageCount.toString(),
                rating = 0.0,
                googleBookId = googleBookId,
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
            SaveToFirebase(
                book = book,
                navController = navController
            )
        }

        Spacer(modifier = Modifier.width(25.dp))
        RoundedButton(
            label = "Cancel"
        ) {
            navController.popBackStack()
        }
    }

}

fun SaveToFirebase(
    book: MBook,
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()) {
        dbCollection.add(book)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId).update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.popBackStack()
                        }
                    }
                    .addOnFailureListener {
                        Log.w("Tag", "Save to firebase: Life is hard")
                    }
            }
    }
}
