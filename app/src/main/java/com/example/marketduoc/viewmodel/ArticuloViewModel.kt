package com.example.marketduoc.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketduoc.data.local.db.AppDatabase
import com.example.marketduoc.data.model.Articulo
import com.example.marketduoc.data.model.ProductoDTO
import com.example.marketduoc.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticuloViewModel(application: Application) : AndroidViewModel(application) {

    private val articuloDao = AppDatabase.getDatabase(application).articuloDao()
    private val apiService = RetrofitClient.apiService

    private val _todosLosArticulos = MutableStateFlow<List<Articulo>>(emptyList())
    val todosLosArticulos: StateFlow<List<Articulo>> = _todosLosArticulos

    init {
        // Cargar datos locales al iniciar
        viewModelScope.launch {
            articuloDao.obtenerTodos().collect { lista ->
                _todosLosArticulos.value = lista
            }
        }
        sincronizar()
    }

    // --- FUNCIÓN REAL ---
    private fun obtenerCorreoReal(): String {
        val sharedPref = getApplication<Application>().getSharedPreferences("MarketDuocPrefs", Context.MODE_PRIVATE)
        val correo = sharedPref.getString("email_usuario", null)

        // Si no hay correo guardado, significa que el usuario se saltó el login (algo grave)
        // Devolvemos "Sin Sesión" para que se note el error si ocurriera, pero ya no "inventamos" un correo.
        return correo ?: "Error: Sin Sesión"
    }

    fun agregarArticulo(articuloOriginal: Articulo) {
        viewModelScope.launch {
            // 1. Obtenemos el correo REAL desde la memoria
            val quienPublica = obtenerCorreoReal()

            // 2. Le pegamos tu firma al artículo
            val articuloConCorreo = articuloOriginal.copy(emailVendedor = quienPublica)

            // 3. Guardar en tu celular
            articuloDao.insertar(articuloConCorreo)

            // 4. Subir a la Nube
            try {
                val dto = ProductoDTO(
                    nombre = articuloConCorreo.titulo,
                    contenido = articuloConCorreo.descripcion,
                    precio = articuloConCorreo.precio.toInt(),
                    imagen = articuloConCorreo.fotoUri,
                    emailUsuario = quienPublica, // SE VA CON TU CORREO REAL
                    categoriaId = articuloConCorreo.categoriaId
                )

                val respuesta = apiService.crearProducto(dto)
                if (respuesta.isSuccessful) {
                    sincronizar()
                    Toast.makeText(getApplication(), "¡Publicado!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Nube", "Error del servidor: ${respuesta.code()}")
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Guardado local (Sin internet)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            try {
                val respuesta = apiService.obtenerProductos()

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val productosNube = respuesta.body()!!
                    articuloDao.borrarTodo()

                    val nuevosArticulos = productosNube.map { dto ->
                        Articulo(
                            idRemoto = dto.id,
                            titulo = dto.nombre,
                            descripcion = dto.contenido,
                            precio = dto.precio.toDouble(),
                            fotoUri = dto.imagen ?: "",
                            emailVendedor = dto.emailUsuario ?: "Anónimo",
                            categoriaId = dto.categoriaId ?: 1
                        )
                    }
                    articuloDao.insertarVarios(nuevosArticulos)
                }
            } catch (e: Exception) {
                Log.e("Sincronizar", "Error al conectar: ${e.message}")
            }
        }
    }

    fun eliminarArticulo(articulo: Articulo) {
        viewModelScope.launch {
            try {
                if (articulo.idRemoto != null) {
                    val resp = apiService.eliminarProducto(articulo.idRemoto)
                    if (resp.isSuccessful) {
                        articuloDao.eliminar(articulo)
                        Toast.makeText(getApplication(), "Eliminado de la nube", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    articuloDao.eliminar(articulo)
                }
            } catch (e: Exception) {
                articuloDao.eliminar(articulo)
            }
        }
    }
}