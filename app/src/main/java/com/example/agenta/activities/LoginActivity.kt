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
 * Esta es la pantalla de "Inicio de Sesión". 
 * Su función principal es dejar que el usuario entre a su cuenta con su nombre y contraseña.
 * Si el usuario ya entró antes, esta pantalla lo detecta y lo manda directo a sus tareas.
 */
class LoginActivity : AppCompatActivity() {

    //  maneja los datos de las tareas y usuarios
    private lateinit var viewModel: VistaModeloTareas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Revisar si el usuario ya tiene una sesión abierta en el celular.
        // Si ya hay un ID de usuario guardado, significa que no necesita volver a loguearse.
        val userPrefs = getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
        if (userPrefs.contains("currentUserId")) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cerramos esta pantalla para que no pueda volver atrás al login
            return
        }

        // Si no hay sesión, mostramos el diseño de la pantalla de login
        setContentView(R.layout.activity_login)

        // Conectamos con el ViewModel (el administrador de datos)
        viewModel = ViewModelProvider(this)[VistaModeloTareas::class.java]

        // Personalización visual.
        // Buscamos si el usuario eligió un color de fondo especial en la configuración.
        val prefsSettings = getSharedPreferences("Settings", MODE_PRIVATE)
        val colorHex = prefsSettings.getString("backgroundColor", "#FFFFFF") ?: "#FFFFFF"
        try {
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.parseColor(colorHex))
        } catch (_: IllegalArgumentException) {
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.WHITE)
        }

        // Referencias a los cuadritos de texto y botones de la pantalla
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val btnNuevaSesion = findViewById<Button>(R.id.btnNuevaSesion)

        // lo que pasa cuando le das clic a "Iniciar Sesión"
        btnIniciarSesion.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Solo intentamos entrar si escribió algo en ambos campos
            if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                // Le pedimos al ViewModel que busque en la base de datos si ese usuario existe
                lifecycleScope.launch {
                    val user = viewModel.login(usuario, contrasena)
                    if (user != null) {
                        //  Guardamos los datos del usuario en la memoria del teléfono
                        // para que la app sepa quién es mientras la usa.
                        val prefs = getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                        prefs.edit {
                            putInt("currentUserId", user.id)
                            putString("userName", user.nombre)
                            putString("userPhone", user.telefono)
                        }

                        // Nos vamos a la pantalla principal
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Si no lo encuentra, avisamos que algo está mal
                        Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, escribe tu usuario y contraseña", Toast.LENGTH_SHORT).show()
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
