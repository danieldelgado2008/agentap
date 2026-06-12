package com.example.agenta.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Clase de base de datos de Room que sirve como punto de acceso principal a la persistencia.
 * Define las entidades que se almacenan y la versión del esquema.
 */
@Database(entities = [Usuario::class, Tarea::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    
    // Métodos abstractos para obtener los DAOs (Objetos de Acceso a Datos)
    abstract fun usuarioDao(): UsuarioDao
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retorna la instancia única (Singleton) de la base de datos.
         * Crea la base de datos si aún no existe.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agenta_db"
                )
                // Permite cambios de esquema destructivos (limpia datos al actualizar versión)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
