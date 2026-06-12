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

/**
 * Fragmento para el registro de nuevos usuarios.
 * Crea una cuenta en la base de datos y guarda información de contacto en preferencias.
 */
class FragmentoRegistro : Fragment() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        // Manejar el botón de atrás del sistema para cerrar la actividad y volver al Login Activity real
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
                // Registrar el usuario en la base de datos Room
                lifecycleScope.launch {
                    val nuevoUsuario = Usuario(nombre = nombre, contrasena = contrasena)
                    viewModel.registrarUsuario(nuevoUsuario)

                    // Guardar datos adicionales en SharedPreferences para la pantalla de Perfil
                    val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                    prefs.edit {
                        putString("userName", nombre)
                        putString("userEmail", email)
                        putString("userPhone", telefono)
                    }

                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    // Navegar a la lista de tareas después del registro exitoso
                    findNavController().navigate(R.id.action_FragmentoRegistro_to_FragmentoTareas)
                }
            } else {
                Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
