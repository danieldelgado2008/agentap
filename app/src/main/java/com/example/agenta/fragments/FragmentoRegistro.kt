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

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Esta pantalla sirve para que los nuevos usuarios creen su cuenta.
 */
class FragmentoRegistro : Fragment() {

    private lateinit var viewModel: VistaModeloTareas
    private val auth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Conectamos con el cerebro de datos
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        // Si el usuario presiona el botón  de "atrás" en el celular,
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
        val etEmail = view.findViewById<EditText>(R.id.etRegisterEmail)
        val etTelefono = view.findViewById<EditText>(R.id.etRegisterTelefono)
        val etContrasena = view.findViewById<EditText>(R.id.etRegisterContrasena)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        // al dar click en registrar
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Solo registramos si no dejó ningún campo vacío
            if (nombre.isNotEmpty() && email.isNotEmpty() && contrasena.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        // 1. Crear usuario en Firebase Auth (Internet)
                        val result = auth.createUserWithEmailAndPassword(email, contrasena).await()
                        val firebaseUid = result.user?.uid ?: ""

                        // 2. Crear la ficha del usuario con su nuevo ID de internet
                        val nuevoUsuario = Usuario(
                            uid = firebaseUid,
                            nombre = nombre,
                            email = email,
                            telefono = telefono,
                            contrasena = contrasena
                        )
                        
                        // 3. Guardar localmente
                        val idLocal = viewModel.registrarUsuario(nuevoUsuario).toInt()
                        viewModel.setUsuarioId(idLocal)

                        // 4. Guardar datos en la memoria del celular
                        val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                        prefs.edit {
                            putString("userName", nombre)
                            putString("userEmail", email)
                            putInt("currentUserId", idLocal)
                        }

                        Toast.makeText(context, "¡Cuenta creada en la nube!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_FragmentoRegistro_to_FragmentoTareas)
                        
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Por favor, completa todos los datos para continuar", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
