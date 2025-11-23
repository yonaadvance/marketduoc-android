package com.example.marketduoc.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje = _errorMensaje.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
        if (_errorMensaje.value != null) {
            _errorMensaje.value = null
        }
    }

    fun onPasswordChange(password: String) {
        _password.value = password
        if (_errorMensaje.value != null) {
            _errorMensaje.value = null
        }
    }

    fun onRegistroClick(onRegistroExitoso: () -> Unit) {
        viewModelScope.launch {
            val emailActual = _email.value
            val passwordActual = _password.value

            _errorMensaje.value = null

            if (emailActual.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailActual).matches()) {
                _errorMensaje.value = "Error: Formato de email inválido o vacío."
                Log.w("RegistroViewModel", _errorMensaje.value!!)
                return@launch
            }

            if (!emailActual.endsWith("@duocuc.cl")) {
                _errorMensaje.value = "Error: El email debe pertenecer al dominio @duocuc.cl."
                Log.w("RegistroViewModel", _errorMensaje.value!!)
                return@launch
            }

            if (passwordActual.length < 6) {
                _errorMensaje.value = "Error: La contraseña debe tener al menos 6 caracteres."
                Log.w("RegistroViewModel", _errorMensaje.value!!)
                return@launch
            }


            try {
                auth.createUserWithEmailAndPassword(emailActual, passwordActual)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("RegistroViewModel", "Usuario creado exitosamente")
                            onRegistroExitoso()
                        } else {
                            val errorFirebase = when (task.exception) {
                                is FirebaseAuthUserCollisionException -> "Error: El email ya está registrado."
                                else -> "Error desconocido al registrar: ${task.exception?.message}"
                            }
                            _errorMensaje.value = errorFirebase
                            Log.w("RegistroViewModel", errorFirebase, task.exception)
                        }
                    }
            } catch (e: Exception) {
                _errorMensaje.value = "Excepción inesperada: ${e.message}"
                Log.e("RegistroViewModel", _errorMensaje.value!!, e)
            }
        }
    }
}