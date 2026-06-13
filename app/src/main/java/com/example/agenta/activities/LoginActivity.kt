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
import com.example.agenta.models.VistaModeloTareas
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Esta es la pantalla de "Inicio de Sesión". 
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: VistaModeloTareas
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificación de Auto-Login de Firebase
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)
        viewModel = ViewModelProvider(this)[VistaModeloTareas::class.java]

        val prefsSettings = getSharedPreferences("Settings", MODE_PRIVATE)
        val colorHex = prefsSettings.getString("backgroundColor", "#FFFFFF") ?: "#FFFFFF"
        try {
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.parseColor(colorHex))
        } catch (_: IllegalArgumentException) {
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.WHITE)
        }

        val etEmail = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val btnNuevaSesion = findViewById<Button>(R.id.btnNuevaSesion)

        btnIniciarSesion.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (email.isNotEmpty() && contrasena.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        // 1. Intentar entrar con Firebase (Nube)
                        auth.signInWithEmailAndPassword(email, contrasena).await()

                        // 2. Guardar sesión local genérica para no romper la navegación actual
                        val prefs = getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                        prefs.edit {
                            putString("userEmail", email)
                            putInt("currentUserId", 1) 
                        }

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, "Error: Correo o clave incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, escribe tu correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        //  Botón para ir a crear una cuenta nueva
        btnNuevaSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("goToRegister", true) // Un mensaje especial para que sepa que debe ir a Registro
            startActivity(intent)
        }
    }
}
