package com.example.marketduoc.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.marketduoc.ui.screens.AgregarArticuloScreen // <-- Importado
import com.example.marketduoc.ui.screens.HomeScreen
import com.example.marketduoc.ui.screens.LoginScreen
import com.example.marketduoc.ui.screens.RegistroScreen
import com.example.marketduoc.viewmodel.ArticuloViewModel

@Composable
fun AppNavigation(
    articuloViewModel: ArticuloViewModel, innerPadding: PaddingValues) { //viewmodel compartido
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.padding(innerPadding)  //creo las ordenes
    ) {

        composable(route = "login") {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegistro = {
                    navController.navigate("registro") //conecto el boton registrarse
                }
            )
        }

        composable(route = "registro") {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onNavigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // aqui es la ruta a home
        composable(route = "home") {
            HomeScreen(
                articuloViewModel = articuloViewModel,
                onNavigateToAgregarArticulo = {
                    navController.navigate("agregar_articulo") // conecta el boton mas
                }
            )
        }

        composable(route = "agregar_articulo") {
            AgregarArticuloScreen(
                articuloViewModel = articuloViewModel,
                onNavigateBack = {
                    navController.popBackStack() // vuelve a homescreen
                }
            )
        }
    }
}