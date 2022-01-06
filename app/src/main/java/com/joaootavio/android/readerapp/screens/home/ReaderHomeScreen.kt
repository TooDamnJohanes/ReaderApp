package com.joaootavio.android.readerapp.screens.home


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.joaootavio.android.readerapp.components.*
import com.joaootavio.android.readerapp.model.MBook
import com.joaootavio.android.readerapp.navigation.ReaderScreens

@Composable
fun Home(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { ReaderAppBar(title = "Home", navController = navController) },
        floatingActionButton = {
            FABContent {
                navController.navigate(ReaderScreens.SearchScreen.name)
            }
        }
    ) {
        Surface(
            modifier =
            Modifier
                .fillMaxSize()
        ) {
            HomeContent(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {

    viewModel.getAllBooksFromDataBase()
    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!viewModel.data.value.data.isNullOrEmpty()) {
        listOfBooks = viewModel.data.value.data!!.toList().filter { mBook ->
            mBook.userId == currentUser?.uid.toString()
        }
    }

    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty())
        email.split('@')[0] else "N/A"

    Column(
        modifier = Modifier.padding(2.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp)
                .align(alignment = Alignment.Start),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TitleSection(label = "Your reading" + "\nactivity right now...")
            Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.7f))
            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colors.secondaryVariant
                )
                Text(
                    text = currentUserName,
                    modifier = Modifier
                        .padding(2.dp),
                    style = MaterialTheme.typography.overline,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
                Divider()
            }
        }
        ReadingRightNowArea(
            books = listOfBooks,
            navController = navController,
        )

        TitleSection(label = "Reading List")

        BookListArea(
            listOfBooks = listOfBooks,
            navController = navController
        )
    }
}

@Composable
fun BookListArea(
    listOfBooks: List<MBook>,
    navController: NavController
) {
    val addedBooks = listOfBooks.filter { mBook ->
        mBook.startReading == null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(listOfBooks = addedBooks) {
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun HorizontalScrollableComponent(
    listOfBooks: List<MBook>,
    onCardPressed: (String) -> Unit
) {
    if (listOfBooks.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(260.dp)
        ) {
            Text(
                text = "No books found. Add a book",
                color = Color.Red.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(240.dp)
        ) {

            LazyRow {
                items(listOfBooks) { book ->
                    ListCard(book = book) {
                        onCardPressed(book.googleBookId.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun ReadingRightNowArea(
    books: List<MBook>,
    navController: NavController,
) {

    val addedBook = books.filter { mBook ->
        mBook.startReading != null && mBook.finishedReading == null
    }

    if (addedBook.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(260.dp)
        ) {
            Text(
                text = "No books found. Add a book",
                color = Color.Red.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
    } else {
        HorizontalScrollableComponent(listOfBooks = addedBook) {
            navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
        }
    }


}


@Composable
@Preview
fun HomePreview() {
    Home(navController = NavController(LocalContext.current))
}
