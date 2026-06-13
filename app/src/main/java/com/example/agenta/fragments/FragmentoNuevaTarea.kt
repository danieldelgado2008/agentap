package com.example.agenta.fragments

import android.app.DatePickerDialog
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
import java.util.Calendar
import java.util.Locale

/**
 * Esta pantalla sirve para "Anotar una nueva tarea". 
 * Es el formulario donde escribes qué tienes que hacer y para cuándo.
 */
class FragmentoNuevaTarea : Fragment() {

    private lateinit var viewModel: VistaModeloTareas

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Cargamos el diseño de la pantalla de detalles que sirve para crear y para ver
        val view = inflater.inflate(R.layout.fragmento_detalle_tarea, container, false)
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        // Buscamos los espacios donde el usuario va a escribir
        val etMateria = view.findViewById<EditText>(R.id.etMateria)
        val etNombreTarea = view.findViewById<EditText>(R.id.etNombreTarea)
        val etFechaEntrega = view.findViewById<EditText>(R.id.etFechaEntrega)
        val etEspecificaciones = view.findViewById<EditText>(R.id.etEspecificaciones)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarTarea)

        //  Configurar el calendario al tocar el campo de fecha
        // Hacemos que el teclado no aparezca y en su lugar salga el calendario
        etFechaEntrega.isFocusable = false
        etFechaEntrega.setOnClickListener {
            mostrarCalendario(etFechaEntrega)
        }

        // click en guardar
        btnGuardar.setOnClickListener {
            val materia = etMateria.text.toString().trim()
            val nombreTarea = etNombreTarea.text.toString().trim()
            val fecha = etFechaEntrega.text.toString().trim()
            val especificaciones = etEspecificaciones.text.toString().trim()

            // Necesitamos saber a qué usuario le pertenece esta tarea
            val userId = viewModel.getUsuarioId()

            // Validamos: obligatorio poner materia y nombre de la tarea
            if (materia.isNotEmpty() && nombreTarea.isNotEmpty() && userId != null) {
                // Creamos la "ficha" de la tarea con toda la información puesta
                val nuevaTarea = Tarea(
                    usuarioId = userId,
                    materia = materia,
                    titulo = nombreTarea,
                    fechaEntrega = if (fecha.isNotEmpty()) fecha else "Sin fecha",
                    descripcion = especificaciones
                )
                
                // Le mandamos la tarea al cerebro para que la guarde en la base de datos
                viewModel.agregarTarea(nuevaTarea)

                Toast.makeText(context, "Tarea guardada con éxito", Toast.LENGTH_SHORT).show()
                
                // Volvemos automáticamente a la lista de tareas
                findNavController().popBackStack() 
            } else {
                Toast.makeText(context, "Por favor, escribe al menos la materia y el nombre", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    /**
     * Muestra una ventana con un calendario para que el usuario elija el día.
     */
    private fun mostrarCalendario(editText: EditText) {
        val calendario = Calendar.getInstance()
        val anio = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val selectorFecha = DatePickerDialog(requireContext(), { _, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
            // El mes empieza en 0, así que le sumamos 1
            val mesFormateado = String.format(Locale.getDefault(), "%02d", mesSeleccionado + 1)
            val diaFormateado = String.format(Locale.getDefault(), "%02d", diaSeleccionado)
            
            // Ponemos la fecha en el cuadrito de texto
            val fechaFinal = "$diaFormateado/$mesFormateado/$anioSeleccionado"
            editText.setText(fechaFinal)
        }, anio, mes, dia)

        selectorFecha.show()
    }
}
