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
 * Fragmento encargado del registro de nuevos usuarios en la aplicación.
 */
class FragmentoRegistro : Fragment() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtener el ViewModel compartido
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        // Manejar el botón de retroceso físico para cerrar la actividad si se está registrando
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
        // Inflar diseño del registro
        val view = inflater.inflate(R.layout.fragmento_registro, container, false)

        val etNombre = view.findViewById<EditText>(R.id.etRegisterNombre)
        val etTelefono = view.findViewById<EditText>(R.id.etRegisterTelefono)
        val etContrasena = view.findViewById<EditText>(R.id.etRegisterContrasena)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        // Acción del botón Registrar
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Validar que todos los campos estén llenos
            if (nombre.isNotEmpty() && telefono.isNotEmpty() && contrasena.isNotEmpty()) {
                lifecycleScope.launch {
                    // 1. Crear el objeto Usuario
                    val nuevoUsuario = Usuario(nombre = nombre, telefono = telefono, contrasena = contrasena)
                    
                    // 2. Guardar en la DB y obtener el ID generado
                    val id = viewModel.registrarUsuario(nuevoUsuario).toInt()
                    
                    // 3. Notificar al ViewModel sobre el nuevo usuario activo
                    viewModel.setUsuarioId(id)

                    // 4. Guardar datos en SharedPreferences para persistencia de la sesión
                    val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                    prefs.edit {
                        putString("userName", nombre)
                        putString("userPhone", telefono)
                        putInt("currentUserId", id)
                    }

                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    // 5. Navegar a la pantalla principal de tareas
                    findNavController().navigate(R.id.action_FragmentoRegistro_to_FragmentoTareas)
                }
            } else {
                Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
