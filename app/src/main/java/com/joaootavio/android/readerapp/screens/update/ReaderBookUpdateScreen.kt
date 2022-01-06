package com.joaootavio.android.readerapp.screens.update

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.joaootavio.android.readerapp.R
import com.joaootavio.android.readerapp.components.InputField
import com.joaootavio.android.readerapp.components.RatingBar
import com.joaootavio.android.readerapp.components.ReaderAppBar
import com.joaootavio.android.readerapp.components.RoundedButton
import com.joaootavio.android.readerapp.data.DataOrException
import com.joaootavio.android.readerapp.model.MBook
import com.joaootavio.android.readerapp.navigation.ReaderScreens
import com.joaootavio.android.readerapp.screens.home.HomeScreenViewModel
import com.joaootavio.android.readerapp.utils.formatDate
import java.lang.Exception

@ExperimentalComposeUiApi
@Composable
fun BookUpdateScreen(
    navController: NavController,
    bookItemId: String,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            ReaderAppBar(
                title = "Update Book",
                icon = Icons.Default.ArrowBack,
                showProfiles = false,
                navController = navController
            ) {
                navController.popBackStack()
            }
        }
    ) {
        val bookInfo = viewModel.data.value

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(5.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bookInfo.loading == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(text = "Loading...")
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .padding(5.dp),
                        shape = RoundedCornerShape(10.dp),
                        elevation = 2.dp,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Start
                        ) {
                            ShowBookUpdate(
                                bookInfo = viewModel.data.value,
                                bookItemId = bookItemId
                            )
                        }

                    }
                    ShowSimpleForm(book = viewModel.data.value.data?.first { mBook ->
                        mBook.googleBookId == bookItemId
                    }!!, navController = navController)
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun ShowSimpleForm(
    book: MBook,
    navController: NavController
) {
    val notesText = remember {
        mutableStateOf(book.notes)
    }

    val isStartedReading = remember {
        mutableStateOf(false)
    }

    val isFinishedReading = remember {
        mutableStateOf(false)
    }

    val ratingVal = remember {
        mutableStateOf(book.rating?.toDouble())
    }

    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SimpleForm(
            defaultValue = if (book.notes.toString().isNotEmpty()) book.notes.toString()
            else "No thoughts available ):"
        ) { note ->
            notesText.value = note
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(5.dp)
    ) {
        TextButton(
            onClick = { isStartedReading.value = true },
            enabled = book.startReading == null
        ) {
            if (book.startReading == null) {
                if (!isStartedReading.value) {
                    Text(text = "Start Reading")
                } else {
                    Text(
                        text = "Started Reading",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(text = "Started on: ${formatDate(book.startReading!!)}")
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        TextButton(
            onClick = { isFinishedReading.value = true },
            enabled = book.finishedReading == null && (isStartedReading.value || book.startReading != null)
        ) {
            if (book.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text("Mark as Read")
                } else {
                    Text(
                        text = "Finished Reading",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(text = "Finished on: ${formatDate(book.finishedReading!!)}")
            }
        }
    }
    Text(
        text = "Rating",
        modifier = Modifier.padding(bottom = 8.dp)
    )
    book.rating?.toInt().let { rating ->
        RatingBar(rating = rating!!, modifier = Modifier) { rating->
            ratingVal.value = rating.toDouble()
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 15.dp))

    Row {

        val changedNotes = book.notes != notesText.value
        val changedRating = book.rating?.toInt()?.toDouble() != ratingVal.value
        val isStartedTimeStamp = if (isStartedReading.value) Timestamp.now() else book.startReading
        val isFinishedTimeStamp = if (isFinishedReading.value) Timestamp.now() else book.finishedReading
        val changedStartedTimeStamp = isStartedReading.value
        val changedFinishedTimeStamp = isFinishedReading.value

        val bookUpdate = changedNotes || changedRating || changedStartedTimeStamp || changedFinishedTimeStamp

        val bookToUpdate = hashMapOf(
            "book_finished_reading" to isFinishedTimeStamp,
            "book_start_reading" to isStartedTimeStamp,
            "rating" to ratingVal.value,
            "notes" to notesText.value
        ).toMap()

        RoundedButton(
            label = "Update"
        ) {
            if (bookUpdate) {
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .update(bookToUpdate)
                    .addOnCompleteListener {
                        showToast(context = context, msg = "Updated Successfully" )
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                    .addOnFailureListener {
                        Log.w("Error", "Error updating document", it)
                    }
            } else {
                navController.navigate(ReaderScreens.ReaderHomeScreen.name)
            }
        }
        Spacer(modifier = Modifier.width(80.dp))
        val openDialog = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            ShowAlertDialog(
                msg = stringResource(id = R.string.sure) + "\n" + stringResource(R.string.action),
                openDialog = openDialog
            ) {
                FirebaseFirestore
                    .getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            openDialog.value = false
                            showToast(context = context, msg = "Book Delete Successfully")
                            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                        }
                    }
            }
        }
        RoundedButton(
            label = "Delete"
        ) {
            openDialog.value = true
        }
    }

}

@Composable
fun ShowAlertDialog(
    msg: String,
    openDialog: MutableState<Boolean>,
    onYesPressed: () -> Unit
) {

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {  }, title = { Text(text = "Delete Book") }, text = { Text(text = msg) }, buttons = {
            Row (
                modifier = Modifier
                .padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    onClick = { onYesPressed.invoke() }
                ) {
                    Text(text = "Yes")
                }

                TextButton(
                    onClick = { openDialog.value = false }
                ) {
                    Text(text = "No")
                }
            }
        })
    }

}

fun showToast(
    context: Context,
    msg: String
) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

@ExperimentalComposeUiApi
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Great Book",
    onSearch: (String) -> Unit = {}
) {
    val textFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
    val keyBoardController = LocalSoftwareKeyboardController.current
    val valid = remember(textFieldValue.value) {
        textFieldValue.value.trim().isNotEmpty()
    }

    InputField(
        valueState = textFieldValue,
        labelId = "Enter your thoughts",
        enabled = true,
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(5.dp)
            .background(Color.White, RoundedCornerShape(5.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        onAction = KeyboardActions {
            if (!valid) return@KeyboardActions
            onSearch(textFieldValue.value.trim())
            keyBoardController?.hide()
        }
    )
}

@Composable
fun ShowBookUpdate(
    bookInfo: DataOrException<List<MBook>, Boolean, Exception>,
    bookItemId: String
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        if (!bookInfo.data.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start

            ) {
                CardListItem(book = bookInfo.data!!.first { mBook ->
                    mBook.googleBookId == bookItemId
                }, onPressDetails = {})
            }
        }
    }
}

@Composable
fun CardListItem(book: MBook, onPressDetails: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { },
        elevation = 4.dp,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = rememberImagePainter(data = book.photoUrl.toString()),
                contentDescription = null,
                modifier = Modifier
                    .height(140.dp)
                    .width(160.dp)
                    .padding(5.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 80.dp,
                            topEnd = 20.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp)
                )

                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp)
                )
            }
        }
    }
}
