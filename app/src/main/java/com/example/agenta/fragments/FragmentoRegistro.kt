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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Esta pantalla sirve para que los nuevos usuarios creen su cuenta.
 * Al registrarse, se crea un perfil en la nube de Google para poder entrar desde cualquier celular.
 */
class FragmentoRegistro : Fragment() {

    private lateinit var viewModel: VistaModeloTareas
    // Herramientas para crear cuentas y guardar perfiles en internet
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Conectamos con el cerebro de datos
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        // Si el usuario presiona el botón físico de "atrás" en el celular,
        // cerramos la app para evitar que regrese a una pantalla de login vacía.
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
        // Cargamos el diseño visual de la pantalla de registro
        val view = inflater.inflate(R.layout.fragmento_registro, container, false)

        // Buscamos los espacios donde el usuario va a escribir sus datos
        val etNombre = view.findViewById<EditText>(R.id.etRegisterNombre)
        val etEmail = view.findViewById<EditText>(R.id.etRegisterEmail)
        val etTelefono = view.findViewById<EditText>(R.id.etRegisterTelefono)
        val etContrasena = view.findViewById<EditText>(R.id.etRegisterContrasena)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        // PASO 1: ¿Qué pasa al tocar el botón "Registrarse"?
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Solo registramos si no dejó ningún campo vacío
            if (nombre.isNotEmpty() && email.isNotEmpty() && contrasena.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        // 1. Crear el usuario en la nube de Google (Authentication)
                        val result = auth.createUserWithEmailAndPassword(email, contrasena).await()
                        val firebaseUid = result.user?.uid ?: ""

                        // 2. Preparar la "ficha" del usuario con sus datos completos
                        val nuevoUsuario = Usuario(
                            uid = firebaseUid,
                            nombre = nombre,
                            email = email,
                            telefono = telefono,
                            contrasena = contrasena
                        )
                        
                        // 3. Guardar el perfil en la base de datos de internet (Cloud Firestore)
                        firestore.collection("usuarios").document(firebaseUid).set(nuevoUsuario).await()

                        // 4. Guardar una copia en la memoria interna del celular
                        val idLocal = viewModel.registrarUsuario(nuevoUsuario).toInt()
                        viewModel.setUsuarioId(idLocal)

                        // 5. Guardar la sesión activa para que no tenga que volver a loguearse
                        val prefs = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                        prefs.edit {
                            putString("userName", nombre)
                            putString("userEmail", email)
                            putString("userPhone", telefono) 
                            putInt("currentUserId", idLocal)
                        }

                        Toast.makeText(context, "¡Cuenta creada en la nube con éxito!", Toast.LENGTH_SHORT).show()
                        
                        // 6. Mandarlo directo a ver sus tareas
                        findNavController().navigate(R.id.action_FragmentoRegistro_to_FragmentoTareas)
                        
                    } catch (e: Exception) {
                        // Si algo falla (ej: el correo ya existe), avisamos por qué
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
