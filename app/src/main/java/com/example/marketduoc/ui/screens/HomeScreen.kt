package com.example.marketduoc.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.marketduoc.data.local.dao.ArticuloDao
import com.example.marketduoc.data.model.Articulo
import com.example.marketduoc.ui.theme.MarketDuocTheme
import com.example.marketduoc.viewmodel.ArticuloViewModel
import com.example.marketduoc.viewmodel.ArticuloViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    articuloViewModel: ArticuloViewModel,
    onNavigateToAgregarArticulo: () -> Unit
) {

    val listaArticulos by articuloViewModel.todosLosArticulos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MarketDuoc") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAgregarArticulo,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Artículo")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            AnimatedVisibility(visible = listaArticulos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay artículos publicados. ¡Sé el primero!")
                }
            }

            AnimatedVisibility(visible = listaArticulos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaArticulos) { articulo ->
                        ArticuloItem(articulo = articulo)
                    }
                }
            }
        }
    }
}

@Composable
fun ArticuloItem(articulo: Articulo) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = Uri.parse(articulo.fotoUri),
                contentDescription = articulo.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = articulo.titulo, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "$ ${articulo.precio}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = articulo.descripcion, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MarketDuocTheme {
        val fakeDao = object : ArticuloDao {
            override suspend fun insertarArticulo(articulo: Articulo) {}
            override fun obtenerTodosLosArticulos(): Flow<List<Articulo>> = flowOf(emptyList())
        }
        val fakeFactory = ArticuloViewModelFactory(fakeDao)

        HomeScreen(
            articuloViewModel = viewModel(factory = fakeFactory),
            onNavigateToAgregarArticulo = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ArticuloItemPreview() {
    MarketDuocTheme {
        ArticuloItem(
            articulo = Articulo(
                id = 1,
                titulo = "Artículo de Prueba",
                descripcion = "Esta es una descripción de ejemplo.",
                precio = 10000.0,
                fotoUri = ""
            )
        )
    }
}