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
 * Fragmento para gestionar la configuración personalizada del usuario.
 * Permite cambiar el color de fondo de la aplicación y realizar el cierre de sesión.
 */
class FragmentoConfiguracion : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragmento_configuracion, container, false)

        // Obtener preferencias compartidas para guardar la configuración visual
        val prefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // Configurar los botones de selección de color para el fondo
        view.findViewById<View>(R.id.colorWhite).setOnClickListener { setBgColor("#FFFFFF", prefs) }
        view.findViewById<View>(R.id.colorLightBlue).setOnClickListener { setBgColor("#E3F2FD", prefs) }
        view.findViewById<View>(R.id.colorLightPink).setOnClickListener { setBgColor("#FCE4EC", prefs) }
        view.findViewById<View>(R.id.colorLightGreen).setOnClickListener { setBgColor("#E8F5E9", prefs) }

        // Configuración del botón de cerrar sesión
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // 1. Limpiar los datos del usuario en SharedPreferences para invalidar la sesión
            val userPrefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
            userPrefs.edit { clear() }

            // 2. Regresar a LoginActivity y limpiar la pila de actividades para evitar volver atrás
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }

    /**
     * Guarda el color seleccionado en SharedPreferences para persistencia visual.
     */
    private fun setBgColor(color: String, prefs: android.content.SharedPreferences) {
        prefs.edit { putString("backgroundColor", color) }
        Toast.makeText(context, "Color guardado. Reinicia para aplicar.", Toast.LENGTH_SHORT).show()
    }
}
