package com.joaootavio.android.readerapp.components

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.joaootavio.android.readerapp.R
import com.joaootavio.android.readerapp.model.MBook
import com.joaootavio.android.readerapp.navigation.ReaderScreens
import com.joaootavio.android.readerapp.screens.update.showToast
import kotlin.reflect.KProperty

@Composable
fun ReaderLogo(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .padding(16.dp),
        text = stringResource(id = R.string.logo_text),
        style = MaterialTheme.typography.h3,
        color = Color.Red.copy(alpha = 0.5f)
    )
}

@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    InputField(
        modifier = modifier,
        valueState = emailState,
        labelId = labelId,
        enabled = enabled,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        onAction = onAction
    )
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        label = {
            Text(
                text = labelId
            )
        },
        singleLine = isSingleLine,
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = MaterialTheme.colors.onBackground
        ),
        modifier = modifier
            .padding(
                bottom = 10.dp,
                start = 10.dp,
                end = 10.dp
            )
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = onAction
    )
}

@Composable
fun PasswordInput(
    modifier: Modifier = Modifier,
    passwordState: MutableState<String>,
    labelId: String = "Password",
    enabled: Boolean = true,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    InputFieldPassword(
        modifier = modifier,
        valueState = passwordState,
        labelId = labelId,
        enabled = enabled,
        passwordVisibility = passwordVisibility,
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        onAction = onAction
    )
}

@Composable
fun InputFieldPassword(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    passwordVisibility: MutableState<Boolean>,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    val visualTrasnformation = if (passwordVisibility.value) VisualTransformation.None else
        PasswordVisualTransformation()
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        label = {
            Text(
                text = labelId
            )
        },
        singleLine = isSingleLine,
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = MaterialTheme.colors.onBackground
        ),
        modifier = modifier
            .padding(
                bottom = 10.dp,
                start = 10.dp,
                end = 10.dp
            )
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = onAction,
        visualTransformation = visualTrasnformation,
        trailingIcon = { PasswordVisibility(passwordVisibility = passwordVisibility) }
    )
}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value
    IconButton(onClick = { passwordVisibility.value = !visible }) {
        Icons.Default.Close
    }
}

@Composable
fun TitleSection(
    modifier: Modifier = Modifier,
    label: String
) {
    Surface(
        modifier = modifier
            .padding(start = 5.dp, top = 1.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 19.sp,
                fontStyle = FontStyle.Normal,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun ReaderAppBar(
    title: String,
    icon: ImageVector? = null,
    showProfiles: Boolean = true,
    navController: NavController,
    onBackArrowClicked: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showProfiles) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Logo Icon",
                        modifier =
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .scale(0.9f)
                    )
                }
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Arrow Back",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.clickable { onBackArrowClicked.invoke() }
                    )

                    Spacer(
                        modifier =
                        Modifier
                            .width(50.dp)
                    )

                }

                Text(
                    text = title,
                    color = Color.Red.copy(alpha = 0.7f),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Spacer(
                    modifier =
                    Modifier
                        .width(150.dp)
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut().run {
                        navController.navigate(ReaderScreens.LoginScreen.name)
                    }
                }
            ) {
                if (showProfiles) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Logout",
                        //tint = Color.Green.copy(alpha = 0.4f)
                    )
                }

            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}


@Composable
fun FABContent(onTap: () -> Unit) {
    FloatingActionButton(
        onClick = { onTap() },
        shape = RoundedCornerShape(50.dp),
        backgroundColor = Color(0XFF92CBDF)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add a Book",
            tint = Color.White
        )
    }
}

@Composable
@Preview
fun BookRating(score: Double = 3.5) {
    Surface(
        modifier = Modifier
            .height(70.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(25.dp),
        elevation = 6.dp,
        color = Color.White
    ) {
        Column(
            modifier =
            Modifier
                .padding(4.dp)
        ) {
            val icon =
                when {
                    score > 0.0 && score < 2.0 -> {
                        Icons.Default.StarBorder
                    }
                    score > 2.0 && score < 4.0 -> {
                        Icons.Default.StarHalf
                    }
                    score > 4.0 -> {
                        Icons.Default.Star
                    }
                    else -> {
                        Icons.Default.StarBorder
                    }
                }

            val color =
                when {
                    score > 0.0 && score < 2.0 -> {
                        Color.Red.copy(alpha = 0.7f)
                    }
                    score > 2.0 && score < 4.0 -> {
                        Color.Green.copy(alpha = 0.2f)
                    }
                    score > 4.0 -> {
                        Color.Green
                    }
                    else -> {
                        Color.Red.copy(alpha = 0.7f)
                    }
                }

            Icon(
                imageVector = icon,
                contentDescription = "Star",
                modifier = Modifier
                    .padding(3.dp),
                tint = color
            )
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}


@Composable
fun ListCard(
    book: MBook,
    onPressDetails: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val resources = context.resources
    val displayMetrics = resources.displayMetrics

    val screenWidth = displayMetrics.widthPixels / displayMetrics.density

    val spacing = 10.dp

    val favBook = remember {
        mutableStateOf(book.favBook)
    }

    Card(
        shape = RoundedCornerShape(15.dp),
        backgroundColor = Color.LightGray,
        elevation = 6.dp,
        modifier = Modifier
            .padding(16.dp)
            .height(242.dp)
            .width(202.dp)
            .clickable { onPressDetails.invoke(book.title.toString()) }
    ) {
        Column(
            modifier =
            Modifier
                .width(screenWidth.dp - (spacing * 2)),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Image(
                        painter = rememberImagePainter(data = book.photoUrl.toString()),
                        contentDescription = "Book Image",
                        modifier = Modifier
                            .height(140.dp)
                            .width(100.dp)
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(46.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = if (favBook.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Fav Icon",
                        tint = if (favBook.value) Color.Red else Color.White,
                        modifier = Modifier
                            .padding(bottom = 1.dp)
                            .clickable {
                                favBook.value = (!favBook.value)
                                val bookToUpdate = hashMapOf(
                                    "book_fav_book" to favBook.value,
                                ).toMap()
                                FirebaseFirestore.getInstance()
                                    .collection("books")
                                    .document(book.id!!)
                                    .update(bookToUpdate)
                                    .addOnCompleteListener {
                                        showToast(context = context, msg = "Updated Successfully")
                                    }
                                    .addOnFailureListener {
                                        Log.w("Error", "Error updating document", it)
                                    }
                            },

                        )
                    BookRating(score = book.rating?.toDouble() ?: 0.0)
                }
            }

            Text(
                text = book.title.toString(),
                modifier = Modifier
                    .padding(8.dp),
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = book.authors.toString(),
                modifier = Modifier
                    .padding(8.dp),
                style = MaterialTheme.typography.caption
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            val readingStats =
                when {
                    book.startReading == null && book.finishedReading == null -> {
                        "Not Started"
                    }
                    book.startReading != null && book.finishedReading == null -> {
                        "Reading..."
                    }
                    book.startReading != null && book.finishedReading != null -> {
                        "Finished!"
                    }
                    else -> {
                        ""
                    }
                }
            RoundedButton(label = readingStats, radius = 39)
        }
    }
}

@Composable
@Preview
fun RoundedButton(
    label: String = "Reading",
    radius: Int = 29,
    onPress: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    bottomEndPercent = radius,
                    topStartPercent = radius
                )
            ),
        color = Color(0xFF92CBDF)
    ) {
        Column(
            modifier = Modifier
                .width(90.dp)
                .heightIn(40.dp)
                .clickable { onPress.invoke() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 15.sp
                )
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun RatingBar(
    modifier: Modifier,
    rating: Int,
    onPressRating: (Int) -> Unit
) {
    var ratingState by remember {
        mutableStateOf(rating)
    }

    var selected by remember {
        mutableStateOf(false)
    }

    val size by animateDpAsState(
        targetValue = if (selected) 42.dp else 34.dp,
        spring(Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier
            .width(280.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..5) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_star_24),
                contentDescription = "star",
                modifier = Modifier
                    .width(size)
                    .height(size)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                selected = true
                                onPressRating(i)
                                ratingState = i
                            }
                            MotionEvent.ACTION_UP -> {
                                selected = false
                            }
                        }
                        true
                    },
                tint = if (i <= ratingState) Color(0xFFFFD700) else Color(0xFFA2ADB1)
            )
        }
    }
}
