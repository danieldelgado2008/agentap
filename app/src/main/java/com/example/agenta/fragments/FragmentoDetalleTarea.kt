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
import com.example.agenta.models.VistaModeloTareas

/**
 * Fragmento que muestra los detalles de una tarea seleccionada.
 * Permite al usuario ver la información y marcar la tarea como completada.
 */
class FragmentoDetalleTarea : Fragment() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Reutiliza el layout fragmento_detalle_tarea (compartido con Nueva Tarea)
        val view = inflater.inflate(R.layout.fragmento_detalle_tarea, container, false)
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        val etMateria = view.findViewById<EditText>(R.id.etMateria)
        val etNombreTarea = view.findViewById<EditText>(R.id.etNombreTarea)
        val etFechaEntrega = view.findViewById<EditText>(R.id.etFechaEntrega)
        val etEspecificaciones = view.findViewById<EditText>(R.id.etEspecificaciones)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarTarea)

        // Cargar los datos de la tarea seleccionada desde el ViewModel
        val tarea = viewModel.tareaSeleccionada
        if (tarea != null) {
            etMateria.setText(tarea.materia)
            etNombreTarea.setText(tarea.titulo)
            etFechaEntrega.setText(tarea.fechaEntrega)
            etEspecificaciones.setText(tarea.descripcion)
            // Cambiar el texto del botón para reflejar la acción de completar
            btnGuardar.text = "Marcar como Terminada"
        }

        btnGuardar.setOnClickListener {
            if (tarea == null) {
                Toast.makeText(context, "Usa la pantalla de nueva tarea", Toast.LENGTH_SHORT).show()
            } else {
                // Marcar como hecha en la base de datos a través del ViewModel
                viewModel.marcarComoTerminada(tarea)
                Toast.makeText(context, "¡Tarea completada!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Volver a la lista
            }
        }

        return view
    }
}
