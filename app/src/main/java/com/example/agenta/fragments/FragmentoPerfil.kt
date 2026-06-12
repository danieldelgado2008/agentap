package com.example.agenta.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.agenta.R

class FragmentoPerfil : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragmento_perfil, container, false)

        val tvNombre = view.findViewById<TextView>(R.id.tvPerfilNombre)
        val tvTelefono = view.findViewById<TextView>(R.id.tvPerfilTelefono)

        val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
        val nombre = prefs.getString("userName", "No disponible")
        val telefono = prefs.getString("userPhone", "No disponible")

        tvNombre.text = nombre
        tvTelefono.text = telefono

        return view
    }
}
