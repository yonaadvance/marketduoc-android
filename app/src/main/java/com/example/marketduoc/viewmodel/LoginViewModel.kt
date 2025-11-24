package com.example.marketduoc.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Esta es la función que le faltaba a tu código
    fun iniciarSesion(email: String, pass: String, onResult: (Boolean) -> Unit) {
        // 1. Validaciones básicas
        if (email.isBlank() || pass.isBlank()) {
            onResult(false)
            return
        }

        // 2. Intentar loguear en Firebase
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LoginViewModel", "Login exitoso")
                    onResult(true) // Avisamos a la pantalla que funcionó
                } else {
                    Log.e("LoginViewModel", "Error login: ${task.exception?.message}")
                    onResult(false) // Avisamos que falló
                }
            }
    }
}