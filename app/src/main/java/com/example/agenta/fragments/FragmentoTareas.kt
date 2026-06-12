package com.example.agenta.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.example.agenta.R
import com.example.agenta.adapters.AdaptadorTareas
import com.example.agenta.models.VistaModeloTareas
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Fragmento principal que muestra la lista de tareas del usuario.
 * Incluye funcionalidades de filtrado, búsqueda y navegación para agregar o ver tareas.
 */
class FragmentoTareas : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: AdaptadorTareas? = null
    private lateinit var viewModel: VistaModeloTareas

    private var filtroActivo: String = "PROXIMAS"
    private var currentQuery: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragmento_tareas, container, false)
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        recyclerView = view.findViewById(R.id.rvTareas)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        // Configurar el adaptador con los callbacks de clic
        adapter = AdaptadorTareas(
            listOf(),
            onVerClick = { tarea ->
                // Guardar la tarea seleccionada en el ViewModel para que el fragmento detalle la lea
                viewModel.tareaSeleccionada = tarea
                findNavController().navigate(R.id.action_FragmentoTareas_to_FragmentoDetalleTarea)
            },
            onHechaClick = { tareaMarcada ->
                // Acción rápida para marcar como terminada
                viewModel.marcarComoTerminada(tareaMarcada)
                Toast.makeText(context, "¡Tarea completada!", Toast.LENGTH_SHORT).show()
                recargarListaSegunFiltro()
            }
        )
        recyclerView?.adapter = adapter

        // Observar cambios en la base de datos para actualizar la UI automáticamente
        viewModel.listaTareas.observe(viewLifecycleOwner) { _ ->
            recargarListaSegunFiltro()
        }

        // Configurar botón para agregar nueva tarea
        val btnAgregar = view.findViewById<ImageButton>(R.id.btnAgregar)
        btnAgregar?.setOnClickListener {
            viewModel.tareaSeleccionada = null // Limpiar selección previa
            findNavController().navigate(R.id.action_FragmentoTareas_to_FragmentoNuevaTarea)
        }

        // Configurar botones de filtrado y barra de búsqueda
        val btnTodas = view.findViewById<Button>(R.id.btnTodasTareas)
        val btnPasadas = view.findViewById<Button>(R.id.btnTareasPasadas)
        val btnTerminadas = view.findViewById<Button>(R.id.btnTareasTerminadas)
        val svBuscador = view.findViewById<SearchView>(R.id.svBuscador)

        svBuscador?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                recargarListaSegunFiltro()
                return true
            }
        })

        btnPasadas?.setOnClickListener {
            filtroActivo = "PASADAS"
            recargarListaSegunFiltro()
        }
        btnTodas?.setOnClickListener {
            filtroActivo = "TODAS"
            recargarListaSegunFiltro()
        }
        btnTerminadas?.setOnClickListener {
            filtroActivo = "HECHAS"
            recargarListaSegunFiltro()
        }

        return view
    }

    /**
     * Aplica el filtrado y ordenamiento de la lista según el estado actual.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun recargarListaSegunFiltro() {
        when (filtroActivo) {
            "PROXIMAS" -> mostrarProximas()
            "PASADAS" -> mostrarPasadas()
            "TODAS" -> mostrarTodas()
            "HECHAS" -> mostrarHechas()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarProximas() {
        val hoyActual = LocalDate.now()
        val tareas = viewModel.listaTareas.value ?: listOf()
        val filtradas = tareas
            .filter {
                val fechaParsed = parsearFechaSegura(it.fechaEntrega)
                val matchesQuery = it.titulo.contains(currentQuery, true) ||
                        it.materia.contains(currentQuery, true) ||
                        it.descripcion.contains(currentQuery, true)

                !it.estaHecha && fechaParsed != null && !fechaParsed.isBefore(hoyActual) && matchesQuery
            }
            .sortedBy { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "PROXIMAS")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarPasadas() {
        val hoyActual = LocalDate.now()
        val tareas = viewModel.listaTareas.value ?: listOf()
        val filtradas = tareas
            .filter {
                val fechaParsed = parsearFechaSegura(it.fechaEntrega)
                val matchesQuery = it.titulo.contains(currentQuery, true) ||
                        it.materia.contains(currentQuery, true) ||
                        it.descripcion.contains(currentQuery, true)

                !it.estaHecha && fechaParsed != null && fechaParsed.isBefore(hoyActual) && matchesQuery
            }
            .sortedBy { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "PASADAS")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarTodas() {
        val hoyActual = LocalDate.now()
        val tareas = viewModel.listaTareas.value ?: listOf()
        val filtradas = tareas
            .filter {
                val matchesQuery = it.titulo.contains(currentQuery, true) ||
                        it.materia.contains(currentQuery, true) ||
                        it.descripcion.contains(currentQuery, true)

                // En "Todas" mostramos las pendientes independientemente de la fecha
                !it.estaHecha && matchesQuery
            }
            .sortedBy { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "TODAS")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarHechas() {
        val tareas = viewModel.listaTareas.value ?: listOf()
        val filtradas = tareas
            .filter {
                val matchesQuery = it.titulo.contains(currentQuery, true) ||
                        it.materia.contains(currentQuery, true) ||
                        it.descripcion.contains(currentQuery, true)

                it.estaHecha && matchesQuery
            }
            .sortedBy { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "HECHAS")
    }

    /**
     * Intenta convertir un String de fecha a un objeto LocalDate de forma robusta.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parsearFechaSegura(fechaStr: String): LocalDate? {
        return try {
            if (fechaStr.length == 5 && fechaStr.contains("/")) {
                LocalDate.parse("$fechaStr/2026", formatter)
            } else {
                LocalDate.parse(fechaStr, formatter)
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView = null
        adapter = null
    }
}
