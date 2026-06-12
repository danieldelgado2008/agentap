package com.example.agenta.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agenta.R
import com.example.agenta.models.Tarea

/**
 * Adaptador para el RecyclerView que muestra la lista de tareas.
 * Se encarga de vincular los datos de cada objeto [Tarea] con las vistas correspondientes.
 */
class AdaptadorTareas(
    private var listaTareas: List<Tarea>,
    private val onVerClick: (Tarea) -> Unit, // Callback para ver detalles
    private val onHechaClick: (Tarea) -> Unit // Callback para marcar como hecha
) : RecyclerView.Adapter<AdaptadorTareas.TareaViewHolder>() {

    // Almacena el filtro activo para decidir qué elementos visuales mostrar/ocultar
    private var filtroActual: String = "PROXIMAS"

    /**
     * Clase interna que contiene las referencias a las vistas de un solo elemento de la lista.
     */
    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMateria: TextView = view.findViewById(R.id.tvMateria)
        val tvTarea: TextView = view.findViewById(R.id.tvTarea)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val btnVerDetalle: Button = view.findViewById(R.id.btnVerDetalle)
    }

    /**
     * Crea una nueva vista (inflada desde item_tarea.xml) cuando el RecyclerView la necesita.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    /**
     * Vincula los datos de la tarea en la posición [position] con las vistas del [holder].
     */
    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = listaTareas[position]
        
        holder.tvMateria.text = tarea.materia
        holder.tvTarea.text = tarea.titulo
        holder.tvFecha.text = tarea.fechaEntrega
        holder.tvDescripcion.text = tarea.descripcion

        // Configurar acción del botón según el texto que tenga asignado
        holder.btnVerDetalle.setOnClickListener {
            if (holder.btnVerDetalle.text == "VER") {
                onVerClick(tarea)
            } else {
                onHechaClick(tarea)
            }
        }

        // Lógica visual dinámica: oculta o muestra detalles según el filtro seleccionado
        when (filtroActual) {
            "PROXIMAS", "HECHAS", "PASADAS" -> {
                holder.tvDescripcion.visibility = View.GONE
                holder.btnVerDetalle.visibility = View.VISIBLE
                holder.btnVerDetalle.text = "VER"
            }
            "TODAS" -> {
                holder.tvDescripcion.visibility = View.VISIBLE
                holder.btnVerDetalle.visibility = View.VISIBLE
                holder.btnVerDetalle.text = "VER"
            }
        }
    }

    override fun getItemCount() = listaTareas.size

    /**
     * Actualiza la lista de datos del adaptador y notifica al RecyclerView para refrescarse.
     */
    fun updateList(nuevaLista: List<Tarea>, filtro: String) {
        this.listaTareas = nuevaLista
        this.filtroActual = filtro
        notifyDataSetChanged()
    }
}
