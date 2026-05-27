package com.example.agenta.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agenta.R
import com.example.agenta.models.Tarea
import com.example.agenta.models.TareaViewModel

class SecondFragment : Fragment(R.layout.fragment_second) {

    private lateinit var viewModel: TareaViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TareaViewModel::class.java]

        val tvEncabezado = view.findViewById<TextView>(R.id.tvNuevaTareaTitulo)
        val etMateria = view.findViewById<EditText>(R.id.etMateria)
        val etNombreTarea = view.findViewById<EditText>(R.id.etNombreTarea)
        val etFechaEntrega = view.findViewById<EditText>(R.id.etFechaEntrega)
        val etEspecificaciones = view.findViewById<EditText>(R.id.etEspecificaciones)
        val btnAccion = view.findViewById<Button>(R.id.btnGuardarTarea)

        val tareaParaVer = viewModel.tareaSeleccionada

        if (tareaParaVer != null) {
            if (tvEncabezado != null) tvEncabezado.text = "Detalles de la tarea"

            etMateria?.setText(tareaParaVer.materia)
            etMateria?.isEnabled = false

            etNombreTarea?.setText(tareaParaVer.titulo)
            etNombreTarea?.isEnabled = false

            etFechaEntrega?.setText(tareaParaVer.fechaEntrega)
            etFechaEntrega?.isEnabled = false

            etEspecificaciones?.setText(tareaParaVer.descripcion)
            etEspecificaciones?.isEnabled = false

            if (tareaParaVer.estaHecha) {
                btnAccion?.visibility = View.GONE
            } else {
                btnAccion?.visibility = View.VISIBLE
                btnAccion?.text = "MARCAR COMO HECHA"
                btnAccion?.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")))

                btnAccion?.setOnClickListener {
                    viewModel.marcarComoTerminada(tareaParaVer)
                    Toast.makeText(context, "¡Tarea terminada! Ganaste puntos.", Toast.LENGTH_SHORT).show()
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("refresh", true)
                    findNavController().popBackStack()
                }
            }
        } else {
            if (tvEncabezado != null) tvEncabezado.text = "Nueva tarea"
            etMateria?.text?.clear()
            etMateria?.isEnabled = true

            etNombreTarea?.text?.clear()
            etNombreTarea?.isEnabled = true

            etFechaEntrega?.text?.clear()
            etFechaEntrega?.isEnabled = true

            etEspecificaciones?.text?.clear()
            etEspecificaciones?.isEnabled = true

            btnAccion?.text = "GUARDAR"
            btnAccion?.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#6200EE")))

            btnAccion?.setOnClickListener {
                val materia = etMateria?.text.toString().trim()
                val tituloTarea = etNombreTarea?.text.toString().trim()
                val fecha = etFechaEntrega?.text.toString().trim()
                val especificaciones = etEspecificaciones?.text.toString().trim()

                if (materia.isNotEmpty() && tituloTarea.isNotEmpty() && fecha.isNotEmpty()) {
                    val nuevaTarea = Tarea(
                        id = 0,
                        materia = materia,
                        titulo = tituloTarea,
                        fechaEntrega = fecha,
                        descripcion = especificaciones,
                        estaHecha = false
                    )
                    viewModel.agregarTarea(nuevaTarea)
                    Toast.makeText(context, "Tarea guardada exitosamente", Toast.LENGTH_SHORT).show()

                    findNavController().previousBackStackEntry?.savedStateHandle?.set("refresh", true)
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Llena los campos obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}