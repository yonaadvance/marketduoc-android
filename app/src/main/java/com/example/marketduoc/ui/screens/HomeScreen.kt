package com.example.marketduoc.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.marketduoc.data.model.Articulo
import com.example.marketduoc.utils.ImageUtils
import com.example.marketduoc.viewmodel.ArticuloViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    articuloViewModel: ArticuloViewModel,
    onNavigateToAgregarArticulo: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val listaCompleta by articuloViewModel.todosLosArticulos.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Todos", "Tecno", "Manual", "Otros")

    val listaFiltrada = when(selectedTabIndex) {
        1 -> listaCompleta.filter { it.categoriaId == 1L }
        2 -> listaCompleta.filter { it.categoriaId == 2L }
        3 -> listaCompleta.filter { it.categoriaId == 3L }
        else -> listaCompleta
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("MarketDuoc", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        TextButton(onClick = onCerrarSesion) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = MaterialTheme.colorScheme.onPrimary)
                            Text("Salir", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    edgePadding = 0.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAgregarArticulo,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            AnimatedVisibility(visible = listaFiltrada.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay artículos aquí.", color = Color.Gray)
                }
            }
            AnimatedVisibility(visible = listaFiltrada.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaFiltrada) { articulo ->
                        ArticuloItem(articulo)
                    }
                }
            }
        }
    }
}

@Composable
fun ArticuloItem(articulo: Articulo) {
    val context = LocalContext.current
    val bitmap = remember(articulo.fotoUri) {
        if (articulo.fotoUri.length > 200) ImageUtils.base64ToBitmap(articulo.fotoUri) else null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = Uri.parse(articulo.fotoUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(articulo.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "$ ${articulo.precio.toInt()}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (articulo.emailVendedor.isNotEmpty()) articulo.emailVendedor else "Desconocido",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(articulo.descripcion, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { Toast.makeText(context, "Contactando a ${articulo.emailVendedor}...", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Contactar Vendedor")
                }
            }
        }
    }
}