package com.example.agenta.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Esta clase es el "Almacén Principal" o la Base de Datos de la aplicación.
 * Imaginalo como un archivo donde guardamos permanentemente las listas de usuarios y tareas.
 */
@Database(entities = [Usuario::class, Tarea::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    
    // Estos son los "empleados" que saben cómo entrar a buscar usuarios o tareas al almacén
    abstract fun usuarioDao(): UsuarioDao
    abstract fun tareaDao(): TareaDao

    companion object {
        // La instancia única de la base de datos para toda la app
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Función para abrir o crear el almacén de datos la primera vez que se usa.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agenta_db" // Nombre del archivo de la base de datos
                )
                // Si cambiamos la estructura de la base de datos, borra lo anterior para no dar errores
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
