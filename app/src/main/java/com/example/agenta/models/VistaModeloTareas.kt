package com.example.agenta.models

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.agenta.notifications.GestorNotificaciones
import kotlinx.coroutines.launch

/**
 * ViewModel que actúa como intermediario entre la UI y la base de datos Room.
 * Gestiona el ciclo de vida de los datos y asegura que persistan tras cambios de configuración.
 */
class VistaModeloTareas(application: Application) : AndroidViewModel(application) {

    // Inicialización de la base de datos y los DAOs
    private val db = AppDatabase.getDatabase(application)
    private val tareaDao = db.tareaDao()
    private val usuarioDao = db.usuarioDao()

    // Lista de tareas observada desde la base de datos
    val listaTareas: LiveData<List<Tarea>> = tareaDao.getAllTareas()

    // Tarea seleccionada actualmente para ver detalles
    var tareaSeleccionada: Tarea? = null

    /**
     * Agrega una nueva tarea a la base de datos y programa su notificación.
     */
    fun agregarTarea(nuevaTarea: Tarea) {
        viewModelScope.launch {
            val id = tareaDao.insertar(nuevaTarea)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Notificaciones para recordatorios
                GestorNotificaciones.programarNotificaciones(getApplication(), nuevaTarea.copy(id = id.toInt()))
            }
        }
    }

    /**
     * Actualiza el estado de una tarea a 'completada' y otorga puntos al usuario.
     */
    fun marcarComoTerminada(tarea: Tarea) {
        viewModelScope.launch {
            val tareaActualizada = tarea.copy(estaHecha = true)
            tareaDao.actualizar(tareaActualizada)
            
            // Otorgar 5 puntos de recompensa
            val prefs = getApplication<Application>().getSharedPreferences("UserStats", Context.MODE_PRIVATE)
            val currentPoints = prefs.getInt("userPoints", 0)
            prefs.edit { putInt("userPoints", currentPoints + 5) }
        }
    }

    /**
     * Verifica las credenciales del usuario (función de utilidad para Login).
     */
    suspend fun login(nombre: String, contrasena: String): Usuario? {
        return usuarioDao.login(nombre, contrasena)
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     */
    suspend fun registrarUsuario(usuario: Usuario) {
        usuarioDao.registrar(usuario)
    }
}
