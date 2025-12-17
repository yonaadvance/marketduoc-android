package com.example.marketduoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marketduoc.navigation.AppNavigation
import com.example.marketduoc.ui.theme.MarketDuocTheme
import com.example.marketduoc.viewmodel.ArticuloViewModel
import com.example.marketduoc.viewmodel.ArticuloViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarketDuocTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // AHORA ES MÁS SIMPLE: Solo le pasamos 'application'
                    val articuloViewModel: ArticuloViewModel = viewModel(
                        factory = ArticuloViewModelFactory(application)
                    )

                    AppNavigation(
                        articuloViewModel = articuloViewModel,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}