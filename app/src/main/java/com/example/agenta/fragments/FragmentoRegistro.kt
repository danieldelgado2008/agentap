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
 * Esta pantalla sirve para que los nuevos usuarios creen su cuenta.
 * Pide nombre, teléfono y contraseña, y los guarda para siempre en el celular.
 */
class FragmentoRegistro : Fragment() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Conectamos con el cerebro de datos
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        // Si el usuario presiona el botón físico de "atrás" en el celular, 
        // cerramos la app para que no regrese a la pantalla de login vacía.
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
        // Cargamos el diseño de la pantalla de registro
        val view = inflater.inflate(R.layout.fragmento_registro, container, false)

        val etNombre = view.findViewById<EditText>(R.id.etRegisterNombre)
        val etTelefono = view.findViewById<EditText>(R.id.etRegisterTelefono)
        val etContrasena = view.findViewById<EditText>(R.id.etRegisterContrasena)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        // ¿Qué pasa al tocar "Registrar"?
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Solo registramos si no dejó ningún campo vacío
            if (nombre.isNotEmpty() && telefono.isNotEmpty() && contrasena.isNotEmpty()) {
                lifecycleScope.launch {
                    // 1. Creamos la "ficha" del nuevo usuario
                    val nuevoUsuario = Usuario(nombre = nombre, telefono = telefono, contrasena = contrasena)
                    
                    // 2. Le decimos al cerebro que lo guarde en la base de datos
                    val id = viewModel.registrarUsuario(nuevoUsuario).toInt()
                    
                    // 3. Activamos a este usuario como el actual
                    viewModel.setUsuarioId(id)

                    // 4. Guardamos sus datos en la memoria del celular para que no tenga que volver a loguearse
                    val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                    prefs.edit {
                        putString("userName", nombre)
                        putString("userPhone", telefono)
                        putInt("currentUserId", id)
                    }

                    Toast.makeText(context, "¡Bienvenido, $nombre!", Toast.LENGTH_SHORT).show()
                    
                    // 5. Lo mandamos directo a ver sus tareas
                    findNavController().navigate(R.id.action_FragmentoRegistro_to_FragmentoTareas)
                }
            } else {
                Toast.makeText(context, "Por favor, completa todos los datos para continuar", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
