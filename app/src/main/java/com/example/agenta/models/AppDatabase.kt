package com.example.agenta.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos de la aplicación definida mediante Room.
 * Contiene las entidades Usuario y Tarea.
 */
@Database(entities = [Usuario::class, Tarea::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    
    // Métodos abstractos para obtener los DAOs
    abstract fun usuarioDao(): UsuarioDao
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Patrón Singleton para obtener la instancia de la base de datos.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agenta_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
