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
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agenta.R
import com.example.agenta.models.Usuario
import com.example.agenta.models.VistaModeloTareas
import kotlinx.coroutines.launch

class FragmentoRegistro : Fragment() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

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
        val view = inflater.inflate(R.layout.fragmento_registro, container, false)

        val etNombre = view.findViewById<EditText>(R.id.etRegisterNombre)
        val etTelefono = view.findViewById<EditText>(R.id.etRegisterTelefono)
        val etContrasena = view.findViewById<EditText>(R.id.etRegisterContrasena)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val telefono = etTelefono.text.toString()
            val contrasena = etContrasena.text.toString()

            if (nombre.isNotEmpty() && telefono.isNotEmpty() && contrasena.isNotEmpty()) {
                lifecycleScope.launch {
                    // Crea un objeto Usuario con los datos ingresados
                    val nuevoUsuario = Usuario(nombre = nombre, contrasena = contrasena)
                    
                    // Registra el usuario en la DB y obtiene el ID generado
                    val id = viewModel.registrarUsuario(nuevoUsuario).toInt()
                    
                    // Establece el ID en el ViewModel para que las tareas funcionen inmediatamente
                    viewModel.setUsuarioId(id)

                    // Guarda los datos del usuario en SharedPreferences
                    val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                    prefs.edit {
                        putString("userName", nombre)
                        putString("userPhone", telefono)
                        putInt("currentUserId", id) // Guardamos el ID para persistencia
                    }

                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_FragmentoRegistro_to_FragmentoTareas)
                }
            } else {
                Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
