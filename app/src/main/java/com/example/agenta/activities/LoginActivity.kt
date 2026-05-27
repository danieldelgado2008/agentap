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
import com.example.agenta.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
val prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val colorHex = prefs.getString("backgroundColor", "#FFFFFF")
        findViewById<LinearLayout>(R.id.layoutLogin)?.setBackgroundColor(Color.parseColor(colorHex))

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val btnNuevaSesion = findViewById<Button>(R.id.btnNuevaSesion)

        btnIniciarSesion.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val contrasena = etContrasena.text.toString()

            if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnNuevaSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("goToRegister", true)
            startActivity(intent)
        }
    }
}