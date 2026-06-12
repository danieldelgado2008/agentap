package com.example.agenta.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agenta.R
import com.example.agenta.models.Tarea
import com.example.agenta.models.VistaModeloTareas

/**
 * Fragmento para la creación de una nueva tarea.
 * Permite ingresar materia, título, fecha y descripción.
 */
class FragmentoNuevaTarea : Fragment() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño (reutiliza fragmento_detalle_tarea para edición/creación)
        val view = inflater.inflate(R.layout.fragmento_detalle_tarea, container, false)
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        val etMateria = view.findViewById<EditText>(R.id.etMateria)
        val etNombreTarea = view.findViewById<EditText>(R.id.etNombreTarea)
        val etFechaEntrega = view.findViewById<EditText>(R.id.etFechaEntrega)
        val etEspecificaciones = view.findViewById<EditText>(R.id.etEspecificaciones)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarTarea)

        // Lógica para guardar la tarea
        btnGuardar.setOnClickListener {
            val materia = etMateria.text.toString().trim()
            val nombreTarea = etNombreTarea.text.toString().trim()
            val fecha = etFechaEntrega.text.toString().trim()
            val especificaciones = etEspecificaciones.text.toString().trim()

            // Obtener el ID del usuario actualmente logueado desde el ViewModel
            val userId = viewModel.getUsuarioId()

            // Validar que los campos críticos no estén vacíos
            if (materia.isNotEmpty() && nombreTarea.isNotEmpty() && userId != null) {
                // Crear el objeto Tarea vinculado al usuario actual
                val nuevaTarea = Tarea(
                    usuarioId = userId,
                    materia = materia,
                    titulo = nombreTarea,
                    fechaEntrega = if (fecha.isNotEmpty()) fecha else "Sin fecha",
                    descripcion = especificaciones
                )
                
                // Persistir la tarea en la base de datos
                viewModel.agregarTarea(nuevaTarea)

                Toast.makeText(context, "Tarea '$nombreTarea' guardada con éxito", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Regresar a la lista de tareas
            } else {
                Toast.makeText(context, "Error al guardar tarea: asegúrate de llenar materia y nombre", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
