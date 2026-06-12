package com.example.agenta.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.agenta.R
import com.example.agenta.activities.LoginActivity

/**
 * Esta es la pantalla de "Ajustes" o "Configuración".
 * Aquí el usuario puede personalizar su app (cambiar el color) o salir de su cuenta.
 */
class FragmentoConfiguracion : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Cargamos el diseño de la configuración
        val view = inflater.inflate(R.layout.fragmento_configuracion, container, false)

        // Preparamos el acceso a la memoria de ajustes del celular
        val prefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // Configuramos los botones de colores para que guarden el color al tocarlos
        view.findViewById<View>(R.id.colorWhite).setOnClickListener { setBgColor("#FFFFFF", prefs) }
        view.findViewById<View>(R.id.colorLightBlue).setOnClickListener { setBgColor("#E3F2FD", prefs) }
        view.findViewById<View>(R.id.colorLightPink).setOnClickListener { setBgColor("#FCE4EC", prefs) }
        view.findViewById<View>(R.id.colorLightGreen).setOnClickListener { setBgColor("#E8F5E9", prefs) }

        // cerrar sesion boton
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Borramos toda la información del usuario en este celular
            val userPrefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
            userPrefs.edit { clear() }

            // Lo mandamos de regreso a la pantalla de Inicio de Sesión
            val intent = Intent(requireContext(), LoginActivity::class.java)
            // Esto hace que no pueda darle a "atrás" para volver a entrar
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }

    /**
     * Guarda el color que eligió el usuario para que la app se vea de ese color al abrirla.
     */
    private fun setBgColor(color: String, prefs: android.content.SharedPreferences) {
        prefs.edit { putString("backgroundColor", color) }
        Toast.makeText(context, "Color guardado. Reinicia la app para ver el cambio.", Toast.LENGTH_SHORT).show()
    }
}
