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
 * Fragmento para crear una nueva tarea.
 * Recopila los datos ingresados por el usuario y los guarda en la base de datos.
 */
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
            val materia = etMateria.text.toString().trim()
            val nombreTarea = etNombreTarea.text.toString().trim()
            val fecha = etFechaEntrega.text.toString().trim()
            val especificaciones = etEspecificaciones.text.toString().trim()

            // Validar campos obligatorios
            if (materia.isNotEmpty() && nombreTarea.isNotEmpty()) {
                // Crear objeto Tarea con ID 0 (Room lo generará automáticamente)
                val nuevaTarea = Tarea(
                    id = 0,
                    materia = materia,
                    titulo = nombreTarea,
                    fechaEntrega = if (fecha.isNotEmpty()) fecha else "Sin fecha",
                    descripcion = especificaciones
                )
                // Guardar en base de datos mediante el ViewModel
                viewModel.agregarTarea(nuevaTarea)

                Toast.makeText(context, "Tarea '$nombreTarea' guardada con éxito", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Regresar a la pantalla anterior
            } else {
                Toast.makeText(context, "Por favor, llena los campos obligatorios (Materia y Tarea)", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
