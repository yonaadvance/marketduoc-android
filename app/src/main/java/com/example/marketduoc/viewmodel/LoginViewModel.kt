package com.example.marketduoc.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje = _errorMensaje.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
        if (_errorMensaje.value != null) _errorMensaje.value = null
    }

    fun onPasswordChange(password: String) {
        _password.value = password
        if (_errorMensaje.value != null) _errorMensaje.value = null
    }

    fun onLoginClick(onLoginExitoso: () -> Unit) {
        viewModelScope.launch {
            val emailActual = _email.value
            val passwordActual = _password.value

            _errorMensaje.value = null


            if (emailActual.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailActual).matches()) {
                _errorMensaje.value = "Error: Formato de email inválido o vacío."
                Log.w("LoginViewModel", _errorMensaje.value!!)
                return@launch
            }
            if (passwordActual.isBlank()) {
                _errorMensaje.value = "Error: La contraseña no puede estar vacía."
                Log.w("LoginViewModel", _errorMensaje.value!!)
                return@launch
            }

            try {
                auth.signInWithEmailAndPassword(emailActual, passwordActual)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginViewModel", "Inicio de sesión exitoso")
                            onLoginExitoso()
                        } else {
                            val errorFirebase = when (task.exception) {
                                is FirebaseAuthInvalidUserException -> "Error: El email no está registrado."
                                is FirebaseAuthInvalidCredentialsException -> "Error: La contraseña es incorrecta."
                                else -> "Error desconocido al iniciar sesión: ${task.exception?.message}"
                            }
                            _errorMensaje.value = errorFirebase
                            Log.w("LoginViewModel", errorFirebase, task.exception)
                        }
                    }
            } catch (e: Exception) {
                _errorMensaje.value = "Excepción inesperada: ${e.message}"
                Log.e("LoginViewModel", _errorMensaje.value!!, e)
            }
        }
    }
}

