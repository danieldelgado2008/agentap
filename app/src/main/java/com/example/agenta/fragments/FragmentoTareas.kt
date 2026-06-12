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
 * Fragmento encargado de mostrar la lista de tareas del usuario.
 * Permite filtrar tareas (Próximas, Pasadas, Todas, Hechas) y buscarlas por texto.
 */
class FragmentoTareas : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: AdaptadorTareas? = null
    private lateinit var viewModel: VistaModeloTareas

    // Estado del filtro de visualización actual
    private var filtroActivo: String = "PROXIMAS"
    // Texto de búsqueda actual en el SearchView
    private var currentQuery: String = ""

    // Formateador para manejar las fechas de entrega de las tareas
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(R.layout.fragmento_tareas, container, false)
        // Obtener el ViewModel compartido de la actividad
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        // Configuración del RecyclerView
        recyclerView = view.findViewById(R.id.rvTareas)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        // Inicializar el adaptador con las funciones de clic
        adapter = AdaptadorTareas(
            listOf(),
            onVerClick = { tarea ->
                // Guardar tarea seleccionada y navegar al detalle
                viewModel.tareaSeleccionada = tarea
                findNavController().navigate(R.id.action_FragmentoTareas_to_FragmentoDetalleTarea)
            },
            onHechaClick = { tareaMarcada ->
                // Marcar tarea como completada en la DB
                viewModel.marcarComoTerminada(tareaMarcada)
                Toast.makeText(context, "¡Tarea completada!", Toast.LENGTH_SHORT).show()
                recargarListaSegunFiltro() // Refrescar la vista actual
            }
        )
        recyclerView?.adapter = adapter

        // Observar cambios en la lista de tareas de la DB
        viewModel.listaTareas.observe(viewLifecycleOwner) { _ ->
            recargarListaSegunFiltro()
        }

        // Botón para agregar una nueva tarea
        val btnAgregar = view.findViewById<ImageButton>(R.id.btnAgregar)
        btnAgregar?.setOnClickListener {
            viewModel.tareaSeleccionada = null // Limpiar selección previa
            findNavController().navigate(R.id.action_FragmentoTareas_to_FragmentoNuevaTarea)
        }

        // Referencias a los botones de filtrado y el buscador
        val btnTodas = view.findViewById<Button>(R.id.btnTodasTareas)
        val btnPasadas = view.findViewById<Button>(R.id.btnTareasPasadas)
        val btnTerminadas = view.findViewById<Button>(R.id.btnTareasTerminadas)
        val svBuscador = view.findViewById<SearchView>(R.id.svBuscador)

        // Configuración de la lógica de búsqueda en tiempo real
        svBuscador?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                recargarListaSegunFiltro()
                return true
            }
        })

        // Configuración de los listeners para los botones de filtro
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
     * Aplica la lógica de filtrado según el botón seleccionado.
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

    /**
     * Filtra y muestra solo las tareas pendientes con fecha de hoy o futura.
     */
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

                !it.estaHecha && fechaParsed != null && (fechaParsed.isEqual(hoyActual) || fechaParsed.isAfter(hoyActual)) && matchesQuery
            }
            .sortedBy { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "PROXIMAS")
    }

    /**
     * Filtra y muestra tareas pendientes cuya fecha de entrega ya pasó.
     */
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
            .sortedByDescending { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "PASADAS")
    }

    /**
     * Muestra todas las tareas pendientes, sin importar la fecha.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarTodas() {
        val hoyActual = LocalDate.now()
        val tareas = viewModel.listaTareas.value ?: listOf()
        val filtradas = tareas
            .filter {
                val fechaParsed = parsearFechaSegura(it.fechaEntrega)
                val matchesQuery = it.titulo.contains(currentQuery, true) ||
                        it.materia.contains(currentQuery, true) ||
                        it.descripcion.contains(currentQuery, true)
                !it.estaHecha && (fechaParsed == null || !fechaParsed.isBefore(hoyActual)) && matchesQuery
            }
            .sortedBy { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "TODAS")
    }

    /**
     * Muestra las tareas que ya han sido marcadas como completadas.
     */
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
            .sortedByDescending { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "HECHAS")
    }

    /**
     * Intenta convertir un String de fecha a LocalDate manejando posibles errores de formato.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parsearFechaSegura(fechaStr: String): LocalDate? {
        return try {
            if (fechaStr.length == 5 && fechaStr.contains("/")) {
                LocalDate.parse("$fechaStr/2026", formatter) // Año por defecto para fechas cortas
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
