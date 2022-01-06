package com.joaootavio.android.readerapp.screens.search

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.joaootavio.android.readerapp.components.InputField
import com.joaootavio.android.readerapp.components.ReaderAppBar
import com.joaootavio.android.readerapp.model.Item
import com.joaootavio.android.readerapp.navigation.ReaderScreens

@ExperimentalComposeUiApi
@Composable
@Preview
fun SearchScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: BooksSearchViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Search Books",
                icon = Icons.Default.ArrowBack,
                navController = navController,
                showProfiles = false
            ) {
                navController.navigate(ReaderScreens.ReaderHomeScreen.name)
            }
        },

        ) {
        Surface {
            Column {
                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) { searchQuery ->
                    viewModel.searchBooks(searchQuery)
                }
                Spacer(modifier = Modifier.height(13.dp))
                BookList(
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun BookList(
    navController: NavController,
    viewModel: BooksSearchViewModel = hiltViewModel()
) {
    val listOfBooks = viewModel.list
    if (viewModel.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else {
        if (!listOfBooks.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(listOfBooks) { book ->
                    BookRow(
                        book = book,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun BookRow(book: Item, navController: NavController) {
    Card(modifier = Modifier
        .clickable {
            navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
        }
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
            val imageUrl = book.volumeInfo.imageLinks.thumbnail ?: ""
            if (!imageUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = "Book Image",
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Image(
                    painter = rememberImagePainter(data = Icons.Default.ThumbDown),
                    contentDescription = "Book Image",
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.width(1.dp))
            Column {
                Text(
                    text = book.volumeInfo.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Author: ${book.volumeInfo.authors}",
                    overflow = TextOverflow.Clip,
                    maxLines = 1,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Published date: ${book.volumeInfo.publishedDate}",
                    overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Description: ${book.volumeInfo.description}",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption,
                )

            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    hint: String = "Search",
    onSearch: (String) -> Unit = {}
) {
    Column {
        val searchQueryState = rememberSaveable { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value) {
            searchQueryState.value.trim().isNotEmpty()
        }

        InputField(
            valueState = searchQueryState,
            labelId = "Search",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(searchQueryState.value.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            }
        )

    }
}