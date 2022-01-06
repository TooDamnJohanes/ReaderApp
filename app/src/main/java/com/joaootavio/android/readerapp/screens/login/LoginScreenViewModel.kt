package com.joaootavio.android.readerapp.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.joaootavio.android.readerapp.model.MUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {

    //val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("signInWithEmailAndPassword: ${task.result.toString()}")
                        home()
                    }else {
                        println("deu ruim")
                    }
                }
        } catch (e: Exception) {
            println("signInWithEmailAndPassword: ${e.message}")
        }
    }


    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch(Dispatchers.IO){
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val displayName = task.result?.user?.email?.split('@')?.get(0)
                        createUser(displayName)
                        home()
                    } else {
                        println("Deu ruim nego")
                    }
                }
        } catch (e: Exception) {
            println("signInWithEmailAndPassword: ${e.message}")
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = MUser(
            userId = userId.toString(),
            displayName = displayName.toString(),
            avatarUrl = "",
            quote = "Life is great",
            profession = "Android developer",
            id = null
        ).toMap()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }

}