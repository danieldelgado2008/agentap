package com.example.agenta.models

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.core.content.edit
import androidx.lifecycle.*
import com.example.agenta.notifications.GestorNotificaciones
import kotlinx.coroutines.launch

class VistaModeloTareas(application: Application) : AndroidViewModel(application) {

    // Instancia de la base de datos y los DAOs para interactuar con Room
    private val db = AppDatabase.getDatabase(application)
    private val tareaDao = db.tareaDao()
    private val usuarioDao = db.usuarioDao()

    // ID del usuario que ha iniciado sesión actualmente
    private val _usuarioIdActual = MutableLiveData<Int>()
    
    // Lista de tareas que se actualiza automáticamente cuando cambia el usuarioIdActual
    val listaTareas: LiveData<List<Tarea>> = _usuarioIdActual.switchMap { id ->
        tareaDao.getTareasPorUsuario(id)
    }

    // Tarea que se selecciona para ver detalles o editar
    var tareaSeleccionada: Tarea? = null

    // Establece el ID del usuario actual
    fun setUsuarioId(id: Int) {
        _usuarioIdActual.value = id
    }

    // Obtiene el ID del usuario actual
    fun getUsuarioId(): Int? = _usuarioIdActual.value

    // Agrega una nueva tarea y programa una notificación
    fun agregarTarea(nuevaTarea: Tarea) {
        viewModelScope.launch {
            val id = tareaDao.insertar(nuevaTarea)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Copiamos la tarea con el ID generado para la notificación
                GestorNotificaciones.programarNotificaciones(getApplication(), nuevaTarea.copy(id = id.toInt()))
            }
        }
    }

    // Marca una tarea como terminada y otorga puntos al usuario
    fun marcarComoTerminada(tarea: Tarea) {
        viewModelScope.launch {
            val userId = getUsuarioId() ?: return@launch
            val tareaActualizada = tarea.copy(estaHecha = true)
            tareaDao.actualizar(tareaActualizada)
            
            // Los puntos se guardan en SharedPreferences ligados al ID del usuario
            val prefs = getApplication<Application>().getSharedPreferences("UserStats_$userId", Context.MODE_PRIVATE)
            val currentPoints = prefs.getInt("userPoints", 0)
            prefs.edit { putInt("userPoints", currentPoints + 5) }
        }
    }

    // Intento de inicio de sesión buscando en la base de datos
    suspend fun login(nombre: String, contrasena: String): Usuario? {
        return usuarioDao.login(nombre, contrasena)
    }

    // Registra un nuevo usuario y devuelve su ID
    suspend fun registrarUsuario(usuario: Usuario): Long {
        return usuarioDao.registrar(usuario)
    }
}
