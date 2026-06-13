package com.example.agenta.models

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Estas interfaces son las ordenes para la base de datos.
 * Definen exactamente qué podemos preguntarle o pedirle que guarde.
 */

@Dao
interface UsuarioDao {
    /**
     * Busca a un usuario comparando su nombre y clave. Se usa al iniciar sesión.
     */
    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND contrasena = :contrasena LIMIT 1")
    suspend fun login(nombre: String, contrasena: String): Usuario?

    /**
     * Guarda a una persona nueva en la base de datos.
     */
    @Insert
    suspend fun registrar(usuario: Usuario): Long
}

@Dao
interface TareaDao {
    /**
     * Busca y nos da todas las tareas que le pertenecen a un usuario específico.
     */
    @Query("SELECT * FROM tareas WHERE usuarioId = :uId")
    fun getTareasPorUsuario(uId: Int): LiveData<List<Tarea>>

    /**
     * Guarda una tarea nueva en la lista.
     * Si la tarea ya existe (mismo ID), la actualiza con los nuevos datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: Tarea): Long

    /**
     * Cambia la información de una tarea (como marcarla como terminada).
     */
    @Update
    suspend fun actualizar(tarea: Tarea)

    /**
     * Borra una tarea de la lista para siempre.
     */
    @Delete
    suspend fun eliminar(tarea: Tarea)
}
