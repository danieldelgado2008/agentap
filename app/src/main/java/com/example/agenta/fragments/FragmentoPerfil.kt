package com.example.agenta.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.agenta.R

/**
 * Fragmento que muestra el perfil del usuario.
 * Recupera la información guardada durante el registro.
 */
class FragmentoPerfil : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragmento_perfil, container, false)

        val tvNombre = view.findViewById<TextView>(R.id.tvPerfilNombre)
        val tvEmail = view.findViewById<TextView>(R.id.tvPerfilEmail)
        val tvTelefono = view.findViewById<TextView>(R.id.tvPerfilTelefono)

        // Cargar datos del usuario desde SharedPreferences
        val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
        tvNombre.text = prefs.getString("userName", "Usuario")
        tvEmail.text = prefs.getString("userEmail", "Sin email")
        tvTelefono.text = prefs.getString("userPhone", "Sin teléfono")

        return view
    }
}
