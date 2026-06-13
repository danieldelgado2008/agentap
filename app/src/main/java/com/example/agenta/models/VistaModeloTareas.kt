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
 */
class VistaModeloTareas(application: Application) : AndroidViewModel(application) {

    // Herramientas para hablar con la base de datos interna del celular
    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()
    private val tareaDao = db.tareaDao()
    
    // Herramientas de Firebase para la nube
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Aquí guardamos el ID del usuario que está usando la app ahora mismo
    private val _usuarioIdActual = MutableLiveData<Int>()
    
    /**
     * Esta es la lista de tareas que se muestra en pantalla.
     *  cada vez que cambiamos de usuario, esta lista se actualiza sola
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
        // Cada vez que entramos, intentamos traer las tareas de la nube por si cambió de celular
        sincronizarConNube()
    }

    /**
     * Nos dice el ID del usuario que tiene la sesión abierta.
     */
    fun getUsuarioId(): Int? = _usuarioIdActual.value

    /**
     * Toma una tarea nueva, la guarda en la base de datos local y TAMBIÉN en la nube.
     */
    fun agregarTarea(nuevaTarea: Tarea) {
        viewModelScope.launch {
            // Guardar localmente
            val idLocal = tareaDao.insertar(nuevaTarea)
            val tareaConId = nuevaTarea.copy(id = idLocal.toInt())
            
            // Guardar en la nube de Firebase si el usuario tiene sesión de Google activa
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                firestore.collection("usuarios")
                    .document(firebaseUser.uid)
                    .collection("tareas")
                    .document(idLocal.toString())
                    .set(tareaConId)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                GestorNotificaciones.programarNotificaciones(getApplication(), tareaConId)
            }
        }
    }

    /**
     * Trae las tareas guardadas en internet hacia este celular.
     */
    private fun sincronizarConNube() {
        val firebaseUser = auth.currentUser ?: return
        
        viewModelScope.launch {
            try {
                // Pedimos a Firebase todas las tareas de este usuario
                val result = firestore.collection("usuarios")
                    .document(firebaseUser.uid)
                    .collection("tareas")
                    .get()
                    .await()
                
                for (doc in result.documents) {
                    val tareaNube = doc.toObject(Tarea::class.java)
                    if (tareaNube != null) {
                        // Las guardamos en el celular para que se vean en la lista
                        tareaDao.insertar(tareaNube)
                    }
                }
            } catch (_: Exception) {
                // Si no hay internet, simplemente no sincroniza
            }
        }
    }

    /**
     * Cuando terminas una tarea, la marca como "Hecha" tanto en el celular como en internet.
     */
    fun marcarComoTerminada(tarea: Tarea) {
        viewModelScope.launch {
            val userId = getUsuarioId() ?: return@launch
            val tareaActualizada = tarea.copy(estaHecha = true)
            tareaDao.actualizar(tareaActualizada)
            
            // Actualizar en la nube
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                firestore.collection("usuarios")
                    .document(firebaseUser.uid)
                    .collection("tareas")
                    .document(tarea.id.toString())
                    .update("estaHecha", true)
            }
            
            val prefs = getApplication<Application>().getSharedPreferences("UserStats_$userId", Context.MODE_PRIVATE)
            val currentPoints = prefs.getInt("userPoints", 0)
            prefs.edit { putInt("userPoints", currentPoints + 5) }
        }
    }

    suspend fun login(nombre: String, contrasena: String): Usuario? {
        return usuarioDao.login(nombre, contrasena)
    }

    suspend fun registrarUsuario(usuario: Usuario): Long {
        return usuarioDao.registrar(usuario)
    }
}
