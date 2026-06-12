package com.example.agenta.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.agenta.R
import com.example.agenta.models.VistaModeloTareas
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de sesión de la aplicación.
 * Permite a los usuarios ingresar sus credenciales o navegar a la pantalla de registro.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar el ViewModel para interactuar con la base de datos
        viewModel = ViewModelProvider(this)[VistaModeloTareas::class.java]

        // Configurar el color de fondo personalizado guardado en preferencias
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val colorHex = prefs.getString("backgroundColor", "#FFFFFF") ?: "#FFFFFF"
        try {
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.parseColor(colorHex))
        } catch (_: IllegalArgumentException) {
            // Si el color es inválido, usar blanco por defecto
            findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.WHITE)
        }

        // Obtener referencias de las vistas
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val btnNuevaSesion = findViewById<Button>(R.id.btnNuevaSesion)

        // Lógica del botón de inicio de sesión
        btnIniciarSesion.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val contrasena = etContrasena.text.toString()

            if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                // Ejecutar búsqueda del usuario en una corrutina (hilo secundario)
                lifecycleScope.launch {
                    val user = viewModel.login(usuario, contrasena)
                    if (user != null) {
                        // Credenciales válidas: navegar a la pantalla principal
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Cerrar LoginActivity para no volver atrás
                    } else {
                        // Credenciales inválidas
                        Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Navegar a la pantalla de registro
        btnNuevaSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Se envía un extra para que MainActivity sepa que debe ir directo al fragmento de registro
            intent.putExtra("goToRegister", true)
            startActivity(intent)
        }
    }
}
