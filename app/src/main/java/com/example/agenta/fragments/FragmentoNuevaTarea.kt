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

class FragmentoNuevaTarea : Fragment() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragmento_detalle_tarea, container, false)
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        val etMateria = view.findViewById<EditText>(R.id.etMateria)
        val etNombreTarea = view.findViewById<EditText>(R.id.etNombreTarea)
        val etFechaEntrega = view.findViewById<EditText>(R.id.etFechaEntrega)
        val etEspecificaciones = view.findViewById<EditText>(R.id.etEspecificaciones)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarTarea)

        btnGuardar.setOnClickListener {
            // Obtenemos los textos de los campos de entrada
            val materia = etMateria.text.toString().trim()
            val nombreTarea = etNombreTarea.text.toString().trim()
            val fecha = etFechaEntrega.text.toString().trim()
            val especificaciones = etEspecificaciones.text.toString().trim()

            // Obtenemos el ID del usuario actual desde el ViewModel
            val userId = viewModel.getUsuarioId()

            // Validamos que los campos obligatorios no estén vacíos y que haya un usuario logueado
            if (materia.isNotEmpty() && nombreTarea.isNotEmpty() && userId != null) {
                // Creamos el objeto Tarea ligado al userId
                val nuevaTarea = Tarea(
                    usuarioId = userId,
                    materia = materia,
                    titulo = nombreTarea,
                    fechaEntrega = if (fecha.isNotEmpty()) fecha else "Sin fecha",
                    descripcion = especificaciones
                )
                
                // Guardamos la tarea en la base de datos a través del ViewModel
                viewModel.agregarTarea(nuevaTarea)

                Toast.makeText(context, "Tarea '$nombreTarea' guardada con éxito", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Regresa a la lista de tareas
            } else {
                Toast.makeText(context, "Error al guardar tarea: asegúrate de llenar materia y nombre", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
