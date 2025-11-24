package com.example.marketduoc.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.marketduoc.ui.screens.AgregarArticuloScreen
import com.example.marketduoc.ui.screens.HomeScreen
import com.example.marketduoc.ui.screens.LoginScreen
import com.example.marketduoc.ui.screens.RegistroScreen
import com.example.marketduoc.viewmodel.ArticuloViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(
    articuloViewModel: ArticuloViewModel, innerPadding: PaddingValues) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.padding(innerPadding)
    ) {

        composable(route = "login") {
            LoginScreen(
                onLoginExitoso = {
                    // Ir al home y borrar login del historial
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegistro = {
                    navController.navigate("registro")
                }
            )
        }

        composable(route = "registro") {
            val context = androidx.compose.ui.platform.LocalContext.current
            RegistroScreen(
                onRegistroExitoso = {
                    // Mostrar mensaje y volver al Login (NO al home directo)
                    android.widget.Toast.makeText(context, "Cuenta creada con éxito. Inicia sesión.", android.widget.Toast.LENGTH_LONG).show()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = "home") {
            HomeScreen(
                articuloViewModel = articuloViewModel,
                onNavigateToAgregarArticulo = {
                    navController.navigate("agregar_articulo")
                },
                onCerrarSesion = {
                    // Cerrar sesión y volver al login borrando todo el historial
                    try { FirebaseAuth.getInstance().signOut() } catch (e: Exception) {}
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(route = "agregar_articulo") {
            AgregarArticuloScreen(
                articuloViewModel = articuloViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}