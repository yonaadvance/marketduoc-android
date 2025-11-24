package com.example.marketduoc

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.marketduoc.data.local.dao.ArticuloDao
import com.example.marketduoc.data.remote.ApiService
import com.example.marketduoc.data.remote.RetrofitClient
import com.example.marketduoc.utils.ImageUtils
import com.example.marketduoc.viewmodel.ArticuloViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ArticuloViewModelTest {

    private lateinit var viewModel: ArticuloViewModel
    private lateinit var dao: ArticuloDao
    private lateinit var apiService: ApiService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // 1. Mockear dependencias
        dao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)

        // 2. Mockear Retrofit
        mockkObject(RetrofitClient)
        every { RetrofitClient.apiService } returns apiService

        // 3. Mockear Utilidades
        mockkObject(ImageUtils)
        every { ImageUtils.uriToBase64(any(), any()) } returns "base64_falso"

        // 4. Mockear Firebase
        mockkStatic(FirebaseAuth::class)
        val mockAuth = mockk<FirebaseAuth>()
        val mockUser = mockk<FirebaseUser>()
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.email } returns "test@duoc.cl"


        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0

        viewModel = ArticuloViewModel(dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `insertarArticulo deberia llamar a la API y luego sincronizar`() = runTest {

        val mockContext = mockk<Context>(relaxed = true)
        val mockUri = mockk<Uri>(relaxed = true)


        coEvery { apiService.crearProducto(any()) } returns Response.success(Any())
        coEvery { apiService.obtenerProductos() } returns Response.success(emptyList())


        viewModel.insertarArticulo(mockContext, "Test", "Desc", "1000", mockUri, 1L)
        advanceUntilIdle()


        coVerify { apiService.crearProducto(any()) }
        coVerify { apiService.obtenerProductos() }
    }
}