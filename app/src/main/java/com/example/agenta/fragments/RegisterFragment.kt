package com.example.agenta.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.agenta.R

class RegisterFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val etNombre = view.findViewById<EditText>(R.id.etRegisterNombre)
        val etEmail = view.findViewById<EditText>(R.id.etRegisterEmail)
        val etTelefono = view.findViewById<EditText>(R.id.etRegisterTelefono)
        val etContrasena = view.findViewById<EditText>(R.id.etRegisterContrasena)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val email = etEmail.text.toString()
            val telefono = etTelefono.text.toString()
            val contrasena = etContrasena.text.toString()

            if (nombre.isNotEmpty() && email.isNotEmpty() && telefono.isNotEmpty() && contrasena.isNotEmpty()) {
                // Guardar info del usuario
                val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("userName", nombre)
                    putString("userEmail", email)
                    putString("userPhone", telefono)
                    apply()
                }

                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_firstFragment)
            } else {
                Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}