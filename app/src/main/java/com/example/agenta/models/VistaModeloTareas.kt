package com.example.agenta.models

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.core.content.edit
import androidx.lifecycle.*
import com.example.agenta.notifications.GestorNotificaciones
import kotlinx.coroutines.launch

/**
 * Esta clase es el "Administrador de Datos" (ViewModel) de la aplicación.
 * Imaginalo como una oficina que organiza todo: guarda tareas, busca usuarios y lleva la cuenta de los puntos.
 * Su ventaja es que los datos no se borran si giras la pantalla del celular.
 */
class VistaModeloTareas(application: Application) : AndroidViewModel(application) {

    // Herramientas para hablar con la base de datos interna del celular
    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()
    private val tareaDao = db.tareaDao()

    // Aquí guardamos el ID del usuario que está usando la app ahora mismo
    private val _usuarioIdActual = MutableLiveData<Int>()
    
    /**
     * Esta es la lista de tareas que se muestra en pantalla.
     * Es "inteligente": cada vez que cambiamos de usuario, esta lista se actualiza sola
     * pidiendo a la base de datos solo las tareas que le pertenecen a ese usuario.
     */
    val listaTareas: LiveData<List<Tarea>> = _usuarioIdActual.switchMap { id ->
        tareaDao.getTareasPorUsuario(id)
    }

    // Un espacio temporal para guardar la tarea que el usuario está viendo o editando
    var tareaSeleccionada: Tarea? = null

    /**
     * Le avisa a la app quién es el usuario actual para cargar sus cosas.
     */
    fun setUsuarioId(id: Int) {
        _usuarioIdActual.value = id
    }

    /**
     * Nos dice el ID del usuario que tiene la sesión abierta.
     */
    fun getUsuarioId(): Int? = _usuarioIdActual.value

    /**
     * Toma una tarea nueva, la guarda en la base de datos y pone una alarma 
     * para que el celular te avise cuando sea hora de entregarla.
     */
    fun agregarTarea(nuevaTarea: Tarea) {
        viewModelScope.launch {
            val id = tareaDao.insertar(nuevaTarea)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Le pasamos el ID real que le dio la base de datos para la notificación
                GestorNotificaciones.programarNotificaciones(getApplication(), nuevaTarea.copy(id = id.toInt()))
            }
        }
    }

    /**
     * Cuando terminas una tarea, esta función la marca como "Hecha" en la base de datos.
     * ¡También te regala 5 puntos de experiencia por tu esfuerzo!
     * Los puntos se guardan por separado para cada usuario.
     */
    fun marcarComoTerminada(tarea: Tarea) {
        viewModelScope.launch {
            val userId = getUsuarioId() ?: return@launch
            val tareaActualizada = tarea.copy(estaHecha = true)
            tareaDao.actualizar(tareaActualizada)
            
            // Buscamos los puntos que ya tenías y le sumamos 5
            val prefs = getApplication<Application>().getSharedPreferences("UserStats_$userId", Context.MODE_PRIVATE)
            val currentPoints = prefs.getInt("userPoints", 0)
            prefs.edit { putInt("userPoints", currentPoints + 5) }
        }
    }

    /**
     * Busca en la base de datos si existe alguien con ese nombre y contraseña.
     */
    suspend fun login(nombre: String, contrasena: String): Usuario? {
        return usuarioDao.login(nombre, contrasena)
    }

    /**
     * Crea un perfil nuevo en la base de datos y nos da el número de ID que le asignó.
     */
    suspend fun registrarUsuario(usuario: Usuario): Long {
        return usuarioDao.registrar(usuario)
    }
}
