package com.example.agenta.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.agenta.R
import com.example.agenta.models.Usuario
import com.example.agenta.models.VistaModeloTareas
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Esta es la pantalla de "Inicio de Sesión". 
 * Su función principal es dejar que el usuario entre a su cuenta con su correo y contraseña.
 * Si el usuario ya entró antes, esta pantalla lo detecta y lo manda directo a sus tareas.
 */
class LoginActivity : AppCompatActivity() {

    // El "cerebro" que maneja los datos de las tareas y usuarios
    private lateinit var viewModel: VistaModeloTareas
    // Herramientas para validar la cuenta en internet (Firebase)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // PASO 1: Revisar si el usuario ya tiene una sesión abierta en Google.
        // Si ya está logueado en Firebase, lo mandamos directo a la MainActivity.
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cerramos esta pantalla para que no pueda volver atrás al login
            return
        }

        // Si no hay sesión, mostramos el diseño de la pantalla de login
        setContentView(R.layout.activity_login)
        viewModel = ViewModelProvider(this)[VistaModeloTareas::class.java]

        // PASO 2: Personalización visual.
        // Aplicamos el color de fondo elegido por el usuario en los ajustes.
        val prefsSettings = getSharedPreferences("Settings", MODE_PRIVATE)
        val colorHex = prefsSettings.getString("backgroundColor", "#FFFFFF") ?: "#FFFFFF"
        try {
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.parseColor(colorHex))
        } catch (_: IllegalArgumentException) {
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.WHITE)
        }

        // Referencias a los cuadritos de texto y botones de la pantalla
        val etEmail = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val btnNuevaSesion = findViewById<Button>(R.id.btnNuevaSesion)

        // PASO 3: ¿Qué pasa cuando le das clic a "Iniciar Sesión"?
        btnIniciarSesion.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Solo intentamos entrar si escribió algo en ambos campos
            if (email.isNotEmpty() && contrasena.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        // 1. Intentar validar el correo y clave en la nube de Google
                        val result = auth.signInWithEmailAndPassword(email, contrasena).await()
                        val firebaseUid = result.user?.uid ?: ""

                        // 2. Traer su nombre y teléfono desde la base de datos de internet
                        val userDoc = firestore.collection("usuarios").document(firebaseUid).get().await()
                        val usuarioNube = userDoc.toObject(Usuario::class.java)

                        // 3. Guardar los datos en el celular para que la app sepa quién eres
                        val idLocal: Int = if (usuarioNube != null) {
                            viewModel.registrarUsuario(usuarioNube).toInt()
                        } else {
                            1 // ID genérico por si falla
                        }

                        val prefs = getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                        prefs.edit {
                            putString("userEmail", email)
                            if (usuarioNube != null) {
                                putString("userName", usuarioNube.nombre)
                                putString("userPhone", usuarioNube.telefono)
                            }
                            putInt("currentUserId", idLocal)
                        }

                        // 4. Entrar a la app principal
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        
                    } catch (e: Exception) {
                        // Si algo sale mal (clave mal, sin internet, etc.), avisamos al usuario
                        Toast.makeText(this@LoginActivity, "Error: Correo o clave incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, escribe tu correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        // PASO 4: Botón para ir a crear una cuenta nueva
        btnNuevaSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("goToRegister", true) // Un mensaje especial para que sepa que debe ir a Registro
            startActivity(intent)
        }
    }
}
