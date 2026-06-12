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
 * Esta es la pantalla donde ves todas tus tareas en una lista.
 * Aquí puedes buscar tareas por nombre, filtrarlas para ver las que ya pasaron
 * o las que ya terminaste, y marcar las que vas completando.
 */
class FragmentoTareas : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: AdaptadorTareas? = null
    private lateinit var viewModel: VistaModeloTareas

    // Guarda qué filtro está tocando el usuario (Próximas, Pasadas, etc.)
    private var filtroActivo: String = "PROXIMAS"
    // Guarda lo que el usuario escribe en el buscador
    private var currentQuery: String = ""

    // Herramienta para entender las fechas escritas como "día/mes/año"
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Cargamos el diseño visual de la lista
        val view = inflater.inflate(R.layout.fragmento_tareas, container, false)
        // Conectamos con el cerebro de datos compartido
        viewModel = ViewModelProvider(requireActivity())[VistaModeloTareas::class.java]

        // Preparamos el contenedor de la lista (RecyclerView)
        recyclerView = view.findViewById(R.id.rvTareas)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        // El "Adaptador" es el que dibuja cada tarea en la lista.
        // Aquí le decimos qué hacer cuando el usuario toca los botones.
        adapter = AdaptadorTareas(
            listOf(),
            onVerClick = { tarea ->
                // Al tocar una tarea, la guardamos y nos vamos a ver sus detalles
                viewModel.tareaSeleccionada = tarea
                findNavController().navigate(R.id.action_FragmentoTareas_to_FragmentoDetalleTarea)
            },
            onHechaClick = { tareaMarcada ->
                // Al tocar el botón de completar, avisamos al cerebro de datos
                viewModel.marcarComoTerminada(tareaMarcada)
                Toast.makeText(context, "¡Buen trabajo! Tarea completada", Toast.LENGTH_SHORT).show()
                recargarListaSegunFiltro() 
            }
        )
        recyclerView?.adapter = adapter

        // Si las tareas cambian en la base de datos, refrescamos la lista automáticamente
        viewModel.listaTareas.observe(viewLifecycleOwner) { _ ->
            recargarListaSegunFiltro()
        }

        // Botón con el signo "+" para crear una nueva tarea
        val btnAgregar = view.findViewById<ImageButton>(R.id.btnAgregar)
        btnAgregar?.setOnClickListener {
            viewModel.tareaSeleccionada = null // Limpiamos si había alguna seleccionada
            findNavController().navigate(R.id.action_FragmentoTareas_to_FragmentoNuevaTarea)
        }

        // Buscador y botones de filtro de la parte superior
        val btnTodas = view.findViewById<Button>(R.id.btnTodasTareas)
        val btnPasadas = view.findViewById<Button>(R.id.btnTareasPasadas)
        val btnTerminadas = view.findViewById<Button>(R.id.btnTareasTerminadas)
        val svBuscador = view.findViewById<SearchView>(R.id.svBuscador)

        // Esto hace que la lista se filtre mientras vas escribiendo en el buscador
        svBuscador?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                recargarListaSegunFiltro()
                return true
            }
        })

        // Configuramos qué pasa al tocar cada botón de filtro
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
     * Decide qué tareas mostrar dependiendo de qué botón de filtro esté activado.
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
     * Muestra solo tareas pendientes que vencen hoy o en el futuro.
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

                // Filtro: No hecha + fecha hoy o futura + que coincida con lo buscado
                !it.estaHecha && fechaParsed != null && (fechaParsed.isEqual(hoyActual) || fechaParsed.isAfter(hoyActual)) && matchesQuery
            }
            .sortedBy { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "PROXIMAS")
    }

    /**
     * Muestra tareas que no terminaste y cuya fecha de entrega ya pasó.
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
     * Muestra absolutamente todas las tareas que no se han marcado como terminadas.
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
     * Muestra únicamente las tareas que ya marcaste como "Hechas".
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
     * Convierte el texto de la fecha en algo que el celular pueda entender y comparar.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parsearFechaSegura(fechaStr: String): LocalDate? {
        return try {
            if (fechaStr.length == 5 && fechaStr.contains("/")) {
                // Si solo puso día/mes, asumimos que es del año 2026
                LocalDate.parse("$fechaStr/2026", formatter)
            } else {
                LocalDate.parse(fechaStr, formatter)
            }
        } catch (e: Exception) {
            null // Si el formato está mal, devolvemos nulo
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView = null
        adapter = null
    }
}
