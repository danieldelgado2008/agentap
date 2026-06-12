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

        adapter = AdaptadorTareas(
            listOf(),
            onVerClick = { tarea ->
                viewModel.tareaSeleccionada = tarea
                findNavController().navigate(R.id.action_FragmentoTareas_to_FragmentoDetalleTarea)
            },
            onHechaClick = { tareaMarcada ->
                viewModel.marcarComoTerminada(tareaMarcada)
                Toast.makeText(context, "¡Tarea completada!", Toast.LENGTH_SHORT).show()
                recargarListaSegunFiltro()
            }
        )
        recyclerView?.adapter = adapter

        viewModel.listaTareas.observe(viewLifecycleOwner) { _ ->
            recargarListaSegunFiltro()
        }

        val btnAgregar = view.findViewById<ImageButton>(R.id.btnAgregar)
        btnAgregar?.setOnClickListener {
            viewModel.tareaSeleccionada = null
            findNavController().navigate(R.id.action_FragmentoTareas_to_FragmentoNuevaTarea)
        }

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

                !it.estaHecha && fechaParsed != null && (fechaParsed.isEqual(hoyActual) || fechaParsed.isAfter(hoyActual)) && matchesQuery
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
            .sortedByDescending { parsearFechaSegura(it.fechaEntrega) }

        adapter?.updateList(filtradas, "PASADAS")
    }

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
