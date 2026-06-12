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
 * Esta pantalla es el "Perfil" del usuario. 
 * Solo sirve para mostrar el nombre y el teléfono de la persona que está usando la app.
 */
class FragmentoPerfil : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Cargamos el diseño visual del perfil
        val view = inflater.inflate(R.layout.fragmento_perfil, container, false)

        // Buscamos los lugares donde vamos a poner el nombre y el teléfono
        val tvNombre = view.findViewById<TextView>(R.id.tvPerfilNombre)
        val tvTelefono = view.findViewById<TextView>(R.id.tvPerfilTelefono)

        // Sacamos la información que guardamos en la memoria del celular al registrarse
        val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
        val nombre = prefs.getString("userName", "No disponible")
        val telefono = prefs.getString("userPhone", "No disponible")

        // Los ponemos en los textos de la pantalla
        tvNombre.text = nombre
        tvTelefono.text = telefono

        return view
    }
}
