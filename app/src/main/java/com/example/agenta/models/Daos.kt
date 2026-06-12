package com.example.agenta.models

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Interfaz para el acceso a datos de los Usuarios.
 */
@Dao
interface UsuarioDao {
    /**
     * Busca un usuario por nombre y contraseña para validar el inicio de sesión.
     */
    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND contrasena = :contrasena LIMIT 1")
    suspend fun login(nombre: String, contrasena: String): Usuario?

    /**
     * Registra un nuevo usuario y devuelve su ID generado automáticamente.
     */
    @Insert
    suspend fun registrar(usuario: Usuario): Long
}

/**
 * Interfaz para el acceso a datos de las Tareas.
 */
@Dao
interface TareaDao {
    /**
     * Obtiene todas las tareas pertenecientes a un usuario específico.
     * Retorna LiveData para observar cambios en tiempo real.
     */
    @Query("SELECT * FROM tareas WHERE usuarioId = :uId")
    fun getTareasPorUsuario(uId: Int): LiveData<List<Tarea>>

    /**
     * Inserta una nueva tarea en la base de datos.
     */
    @Insert
    suspend fun insertar(tarea: Tarea): Long

    /**
     * Actualiza los datos de una tarea (ej. para marcarla como hecha).
     */
    @Update
    suspend fun actualizar(tarea: Tarea)

    /**
     * Elimina una tarea permanentemente de la base de datos.
     */
    @Delete
    suspend fun eliminar(tarea: Tarea)
}
