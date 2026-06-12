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

/**
 * Actividad encargada del inicio de sesión de los usuarios.
 * Valida las credenciales contra la base de datos y gestiona el estado de la sesión.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificación de Auto-Login: si ya existe un ID de usuario guardado, saltar a la MainActivity
        val userPrefs = getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
        if (userPrefs.contains("currentUserId")) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza esta actividad para que no regrese al Login al presionar atrás
            return
        }

        setContentView(R.layout.activity_login)

        // Obtener el ViewModel para acceder a la base de datos
        viewModel = ViewModelProvider(this)[VistaModeloTareas::class.java]

        // Aplicar personalización de color de fondo desde las preferencias
        val prefsSettings = getSharedPreferences("Settings", MODE_PRIVATE)
        val colorHex = prefsSettings.getString("backgroundColor", "#FFFFFF") ?: "#FFFFFF"
        try {
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.parseColor(colorHex))
        } catch (_: IllegalArgumentException) {
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.WHITE)
        }

        // Referencias a las vistas del formulario
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val btnNuevaSesion = findViewById<Button>(R.id.btnNuevaSesion)

        // Acción del botón Iniciar Sesión
        btnIniciarSesion.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                // Ejecutar búsqueda en la DB de forma asíncrona usando Coroutines
                lifecycleScope.launch {
                    val user = viewModel.login(usuario, contrasena)
                    if (user != null) {
                        // Si el usuario existe, guardar sus datos y navegar a la app principal
                        val prefs = getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                        prefs.edit {
                            putInt("currentUserId", user.id)
                            putString("userName", user.nombre)
                            putString("userPhone", user.telefono)
                        }

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Mostrar error si las credenciales no coinciden
                        Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción para ir a la pantalla de Registro
        btnNuevaSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("goToRegister", true) // Flag para que MainActivity sepa que debe mostrar el Registro
            startActivity(intent)
        }
    }
}
