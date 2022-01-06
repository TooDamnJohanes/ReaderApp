package com.joaootavio.android.readerapp.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.joaootavio.android.readerapp.components.ReaderAppBar
import com.joaootavio.android.readerapp.model.Item
import com.joaootavio.android.readerapp.model.MBook
import com.joaootavio.android.readerapp.navigation.ReaderScreens
import com.joaootavio.android.readerapp.screens.home.HomeScreenViewModel
import com.joaootavio.android.readerapp.screens.search.BookRow
import com.joaootavio.android.readerapp.utils.formatDate

@Composable
fun ReaderStatsScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel
) {
    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Book Stats",
                showProfiles = false,
                navController = navController,
                icon = Icons.Default.ArrowBack
            ) {
                navController.navigate(ReaderScreens.ReaderHomeScreen.name)
            }
        }
    ) {
        books = if (!viewModel.data.value.data.isNullOrEmpty()) {
            viewModel.data.value.data!!.filter { mBook ->
                (mBook.userId == currentUser?.uid)
            }
        } else {
            emptyList()
        }
        Column {
            Row {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Sharp.Person,
                        contentDescription = "icon"
                    )
                }
                Text(
                    text = "Hi, ${currentUser?.email.toString().split("@")[0].uppercase()}"
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                shape = RoundedCornerShape(5.dp),
                elevation = 5.dp
            ) {
                val readBookList: List<MBook> = if (!viewModel.data.value.data.isNullOrEmpty()) {
                    books.filter { mBook ->
                        (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                    }
                } else {
                    emptyList()
                }

                val readingbooks = books.filter { mBook ->
                    (mBook.startReading != null) && (mBook.finishedReading == null)
                }

                Column(
                    modifier = Modifier
                        .padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Your Stats",
                        style = MaterialTheme.typography.h5
                    )
                    Divider()
                    Text(
                        text = "You're reading: ${readingbooks.size} books"
                    )
                    Text(
                        text = "You're reading: ${readBookList.size} books"
                    )
                }
            }
            if (viewModel.data.value.loading == true) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Divider()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(all = 10.dp)
                ) {
                    val readBooks: List<MBook> =
                        if (!viewModel.data.value.data.isNullOrEmpty()) {
                            viewModel.data.value.data!!.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.startReading != null) && (mBook.finishedReading != null)
                            }
                        } else {
                            emptyList()
                        }
                    items(readBooks) { books ->
                        BookRow(
                            book = books,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookRow(book: MBook, navController: NavController) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(3.dp),
        shape = RectangleShape,
        elevation = 7.dp
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            val imageUrl = book.photoUrl
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = "Book Image",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(1.dp))
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = book.title ?: "Empty Title",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )

                    if (book.rating ?: 0.0 >= 4.0) {
                        Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Thumbs up",
                            tint = Color.Green.copy(alpha = 0.5f)
                        )
                    } else if (book.rating ?: 0.0 < 3.0) {
                        Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = "Thumbs up",
                            tint = Color.Red.copy(alpha = 0.5f)
                        )
                    }
                }
                Text(
                    text = "Author: ${book.authors ?: "Empty Authors"}",
                    overflow = TextOverflow.Clip,
                    maxLines = 1,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Stared ${formatDate(book.startReading!!) ?: "Empty Date"}",
                    softWrap= true,
                    overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Finished: ${formatDate(book.finishedReading!!) ?: "Empty Date"}",
                    softWrap= true,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption,
                )

            }
        }
    }
}