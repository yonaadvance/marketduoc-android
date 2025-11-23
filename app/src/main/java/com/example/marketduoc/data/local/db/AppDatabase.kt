package com.example.marketduoc.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.marketduoc.data.local.dao.ArticuloDao
import com.example.marketduoc.data.model.Articulo

@Database(
    entities = [Articulo::class], // aqui creo la tabla
    version = 1, // version de la bd
    exportSchema = false // adve de room
)
abstract class AppDatabase : RoomDatabase() { //abst xke room escri el cod

    // Función abstracta para tu DAO
    abstract fun articuloDao(): ArticuloDao

    // ejemplo SQLite patr singlet
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null //misma para todos los hilos

        fun getDatabase(context: Context): AppDatabase {  //se obtiene la bd
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "marketduoc_db" // como se constru la bd
                ).build()
                INSTANCE = instance // guardo la ins de la bd pa no crear denvo
                instance
            }
        }
    }
}

//asi solo existe una instancia en la bd de la app y asi no se pega