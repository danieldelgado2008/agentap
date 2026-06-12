package com.example.agenta.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UsuarioDao {
    // Busca un usuario por nombre y contraseña (Login)
    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND contrasena = :contrasena LIMIT 1")
    suspend fun login(nombre: String, contrasena: String): Usuario?

    // Registra un nuevo usuario y devuelve su ID generado
    @Insert
    suspend fun registrar(usuario: Usuario): Long
}

@Dao
interface TareaDao {
    // Obtiene todas las tareas de un usuario específico mediante su ID
    @Query("SELECT * FROM tareas WHERE usuarioId = :uId")
    fun getTareasPorUsuario(uId: Int): LiveData<List<Tarea>>

    // Inserta una nueva tarea en la base de datos
    @Insert
    suspend fun insertar(tarea: Tarea): Long

    // Actualiza una tarea existente (por ejemplo, marcar como terminada)
    @Update
    suspend fun actualizar(tarea: Tarea)

    // Elimina una tarea de la base de datos
    @Delete
    suspend fun eliminar(tarea: Tarea)
}
