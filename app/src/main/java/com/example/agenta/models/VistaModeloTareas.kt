package com.example.agenta.models

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.core.content.edit
import androidx.lifecycle.*
import com.example.agenta.notifications.GestorNotificaciones
import kotlinx.coroutines.launch

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Esta clase es el "Administrador de Datos" (ViewModel) de la aplicación.
 * Imaginalo como una oficina que organiza todo: guarda tareas, busca usuarios y lleva la cuenta de los puntos.
 * Se encarga de sincronizar lo que haces en el celular con la nube de Google.
 */
class VistaModeloTareas(application: Application) : AndroidViewModel(application) {

    // Herramientas para hablar con la base de datos interna del celular (Room)
    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()
    private val tareaDao = db.tareaDao()
    
    // Herramientas para hablar con internet (Google Firebase)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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
     * Al activarse, busca automáticamente las tareas que tengas en internet.
     */
    fun setUsuarioId(id: Int) {
        _usuarioIdActual.value = id
        sincronizarConNube()
    }

    /**
     * Nos dice el ID numérico del usuario que tiene la sesión abierta.
     */
    fun getUsuarioId(): Int? = _usuarioIdActual.value

    /**
     * Toma una tarea nueva, la guarda en el celular y TAMBIÉN envía una copia a internet.
     * También pone una alarma para que el celular te avise cuando sea hora de entregarla.
     */
    fun agregarTarea(nuevaTarea: Tarea) {
        viewModelScope.launch {
            // 1. Guardar primero en la memoria del celular
            val idLocal = tareaDao.insertar(nuevaTarea)
            val tareaConId = nuevaTarea.copy(id = idLocal.toInt())
            
            // 2. Intentar guardar en la nube de Google (Firebase) si hay internet
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                firestore.collection("usuarios")
                    .document(firebaseUser.uid)
                    .collection("tareas")
                    .document(idLocal.toString())
                    .set(tareaConId)
            }

            // 3. Programar el recordatorio visual
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                GestorNotificaciones.programarNotificaciones(getApplication(), tareaConId)
            }
        }
    }

    /**
     * Descarga todas las tareas que tengas guardadas en internet hacia este celular.
     * Es lo que permite abrir tu cuenta en cualquier dispositivo y ver tus cosas.
     */
    private fun sincronizarConNube() {
        val firebaseUser = auth.currentUser ?: return
        val idLocalActual = getUsuarioId() ?: return
        
        viewModelScope.launch {
            try {
                // Pedimos a Google (Firebase) todas las tareas registradas de este usuario
                val result = firestore.collection("usuarios")
                    .document(firebaseUser.uid)
                    .collection("tareas")
                    .get()
                    .await()
                
                for (doc in result.documents) {
                    val tareaNube = doc.toObject(Tarea::class.java)
                    if (tareaNube != null) {
                        // IMPORTANTE: Ajustamos la tarea para que este celular sepa que nos pertenece
                        val tareaAjustada = tareaNube.copy(usuarioId = idLocalActual)
                        tareaDao.insertar(tareaAjustada)
                    }
                }
            } catch (_: Exception) {
                // Si no hay internet o falla, simplemente no descarga nada por ahora
            }
        }
    }

    /**
     * Cuando marcas una tarea como terminada, avisa tanto al celular como a la nube.
     * ¡También te regala 5 puntos para tu mascota!
     */
    fun marcarComoTerminada(tarea: Tarea) {
        viewModelScope.launch {
            val userId = getUsuarioId() ?: return@launch
            val tareaActualizada = tarea.copy(estaHecha = true)
            
            // 1. Actualizar en el celular
            tareaDao.actualizar(tareaActualizada)
            
            // 2. Actualizar en internet (Nube)
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                firestore.collection("usuarios")
                    .document(firebaseUser.uid)
                    .collection("tareas")
                    .document(tarea.id.toString())
                    .update("estaHecha", true)
            }
            
            // 3. Sumar los puntos de experiencia
            val prefs = getApplication<Application>().getSharedPreferences("UserStats_$userId", Context.MODE_PRIVATE)
            val currentPoints = prefs.getInt("userPoints", 0)
            prefs.edit { putInt("userPoints", currentPoints + 5) }
        }
    }

    /**
     * Función antigua para buscar usuarios locales (se mantiene por compatibilidad).
     */
    suspend fun login(nombre: String, contrasena: String): Usuario? {
        return usuarioDao.login(nombre, contrasena)
    }

    /**
     * Guarda a un usuario nuevo en la memoria del celular.
     */
    suspend fun registrarUsuario(usuario: Usuario): Long {
        return usuarioDao.registrar(usuario)
    }
}
