package com.example.marketduoc.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview as CameraXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.marketduoc.viewmodel.ArticuloViewModel
import com.google.common.util.concurrent.ListenableFuture
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarArticuloScreen(
    modifier: Modifier = Modifier,
    articuloViewModel: ArticuloViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Categoría por defecto: 1 (Tecno)
    var selectedCategory by remember { mutableStateOf(1L) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )

    LaunchedEffect(key1 = hasPermission) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Artículo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasPermission) {
                if (capturedImageUri == null) {
                    // CÁMARA
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f)) {
                        SimpleCameraPreview(
                            modifier = Modifier.fillMaxSize(),
                            onImageCaptureReady = { imageCapture = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        val imgCap = imageCapture
                        if (imgCap != null) takePhoto(context, imgCap) { capturedImageUri = it }
                    }) { Text("Tomar Foto") }
                } else {
                    // FORMULARIO
                    AsyncImage(model = capturedImageUri, contentDescription = null, modifier = Modifier.fillMaxWidth().aspectRatio(1f), contentScale = ContentScale.Crop)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Categoría:", style = MaterialTheme.typography.labelLarge)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FilterChip(selected = selectedCategory == 1L, onClick = { selectedCategory = 1L }, label = { Text("Tecno") })
                        FilterChip(selected = selectedCategory == 2L, onClick = { selectedCategory = 2L }, label = { Text("Manual") })
                        FilterChip(selected = selectedCategory == 3L, onClick = { selectedCategory = 3L }, label = { Text("Otros") })
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        enabled = titulo.isNotBlank() && precio.isNotBlank(),
                        onClick = {
                            articuloViewModel.insertarArticulo(context, titulo, descripcion, precio, capturedImageUri!!, selectedCategory)
                            Toast.makeText(context, "Publicando...", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Publicar") }
                    TextButton(onClick = { capturedImageUri = null }) { Text("Cambiar Foto") }
                }
            } else {
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Permiso Cámara") }
            }
        }
    }
}

@Composable
private fun SimpleCameraPreview(modifier: Modifier = Modifier, cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA, onImageCaptureReady: (ImageCapture) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    LaunchedEffect(key1 = true) { onImageCaptureReady(imageCapture) }
    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx)
        val executor = ContextCompat.getMainExecutor(ctx)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = CameraXPreview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        }, executor)
        previewView
    }, modifier = modifier)
}

private fun takePhoto(context: Context, imageCapture: ImageCapture, onPhotoTaken: (Uri) -> Unit) {
    val photoFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) { onPhotoTaken(Uri.fromFile(photoFile)) }
        override fun onError(exc: ImageCaptureException) { Log.e("Camera", "Error: ${exc.message}") }
    })
}