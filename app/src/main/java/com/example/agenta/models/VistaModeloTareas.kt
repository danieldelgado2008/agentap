package com.example.agenta.models

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.core.content.edit
import androidx.lifecycle.*
import com.example.agenta.notifications.GestorNotificaciones
import kotlinx.coroutines.launch

/**
 * ViewModel que actúa como intermediario entre la UI y la base de datos (Repository/DAOs).
 * Gestiona el estado de las tareas del usuario actual y la lógica de negocio como los puntos.
 */
class VistaModeloTareas(application: Application) : AndroidViewModel(application) {

    // Instancia de la base de datos y los DAOs para interactuar con Room
    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()
    private val tareaDao = db.tareaDao()

    // LiveData que almacena el ID del usuario que ha iniciado sesión actualmente
    private val _usuarioIdActual = MutableLiveData<Int>()
    
    /**
     * Lista reactiva de tareas. 
     * Se actualiza automáticamente cada vez que cambia el [_usuarioIdActual] 
     * gracias a switchMap, consultando a la DB por las tareas de ese usuario.
     */
    val listaTareas: LiveData<List<Tarea>> = _usuarioIdActual.switchMap { id ->
        tareaDao.getTareasPorUsuario(id)
    }

    // Almacena temporalmente una tarea seleccionada para ver sus detalles o editarla
    var tareaSeleccionada: Tarea? = null

    /**
     * Establece el ID del usuario actual, disparando la actualización de [listaTareas].
     */
    fun setUsuarioId(id: Int) {
        _usuarioIdActual.value = id
    }

    /**
     * Obtiene el ID del usuario actual almacenado en el LiveData.
     */
    fun getUsuarioId(): Int? = _usuarioIdActual.value

    /**
     * Inserta una nueva tarea en la base de datos y programa su notificación de recordatorio.
     */
    fun agregarTarea(nuevaTarea: Tarea) {
        viewModelScope.launch {
            val id = tareaDao.insertar(nuevaTarea)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Copiamos la tarea con el ID generado por Room para la configuración de la notificación
                GestorNotificaciones.programarNotificaciones(getApplication(), nuevaTarea.copy(id = id.toInt()))
            }
        }
    }

    /**
     * Marca una tarea como terminada y otorga 5 puntos de experiencia al usuario.
     * Los puntos se guardan de forma persistente en SharedPreferences.
     */
    fun marcarComoTerminada(tarea: Tarea) {
        viewModelScope.launch {
            val userId = getUsuarioId() ?: return@launch
            val tareaActualizada = tarea.copy(estaHecha = true)
            tareaDao.actualizar(tareaActualizada)
            
            // Los puntos se guardan en SharedPreferences ligados al ID del usuario para que sean únicos por perfil
            val prefs = getApplication<Application>().getSharedPreferences("UserStats_$userId", Context.MODE_PRIVATE)
            val currentPoints = prefs.getInt("userPoints", 0)
            prefs.edit { putInt("userPoints", currentPoints + 5) }
        }
    }

    /**
     * Realiza el proceso de login buscando un usuario con nombre y contraseña coincidentes.
     */
    suspend fun login(nombre: String, contrasena: String): Usuario? {
        return usuarioDao.login(nombre, contrasena)
    }

    /**
     * Registra un nuevo usuario en la base de datos y devuelve su ID generado automáticamente.
     */
    suspend fun registrarUsuario(usuario: Usuario): Long {
        return usuarioDao.registrar(usuario)
    }
}
