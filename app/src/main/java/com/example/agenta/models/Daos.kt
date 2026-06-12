package com.example.agenta.models

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Interfaz para el acceso a datos de los usuarios.
 */
@Dao
interface UsuarioDao {
    /**
     * Busca un usuario por nombre y contraseña (Login).
     */
    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND contrasena = :contrasena LIMIT 1")
    suspend fun login(nombre: String, contrasena: String): Usuario?

    /**
     * Inserta un nuevo usuario (Registro).
     */
    @Insert
    suspend fun registrar(usuario: Usuario)
}

/**
 * Interfaz para el acceso a datos de las tareas.
 */
@Dao
interface TareaDao {
    /**
     * Obtiene todas las tareas y las devuelve envueltas en LiveData para observar cambios.
     */
    @Query("SELECT * FROM tareas")
    fun getAllTareas(): LiveData<List<Tarea>>

    /**
     * Inserta una nueva tarea.
     */
    @Insert
    suspend fun insertar(tarea: Tarea): Long

    /**
     * Actualiza una tarea existente (ej. marcar como hecha).
     */
    @Update
    suspend fun actualizar(tarea: Tarea)

    /**
     * Elimina una tarea de la base de datos.
     */
    @Delete
    suspend fun eliminar(tarea: Tarea)
}
