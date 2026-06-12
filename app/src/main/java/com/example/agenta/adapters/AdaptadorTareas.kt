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
 * Adaptador para mostrar la lista de tareas en un RecyclerView.
 * Gestiona la visualización de cada fila (item_tarea) y los eventos de clic.
 */
class AdaptadorTareas(
    private var listaTareas: List<Tarea>,
    private val onVerClick: (Tarea) -> Unit, // Callback para ver detalles
    private val onHechaClick: (Tarea) -> Unit // Callback para marcar como terminada
) : RecyclerView.Adapter<AdaptadorTareas.TareaViewHolder>() {

    private var filtroActual: String = "PROXIMAS"

    /**
     * ViewHolder que mantiene las referencias a las vistas de cada elemento de la lista.
     */
    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMateria: TextView = view.findViewById(R.id.tvMateria)
        val tvTarea: TextView = view.findViewById(R.id.tvTarea)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val btnVerDetalle: Button = view.findViewById(R.id.btnVerDetalle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        // Inflar el diseño item_tarea.xml para cada fila
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = listaTareas[position]
        
        // Asignar los datos de la tarea a las vistas
        holder.tvMateria.text = tarea.materia
        holder.tvTarea.text = tarea.titulo
        holder.tvFecha.text = tarea.fechaEntrega
        holder.tvDescripcion.text = tarea.descripcion

        // Configurar la acción del botón según su etiqueta o estado
        holder.btnVerDetalle.setOnClickListener {
            if (holder.btnVerDetalle.text == "VER") {
                onVerClick(tarea)
            } else {
                onHechaClick(tarea)
            }
        }

        // Ajustar la visibilidad de los elementos según el filtro activo (Próximas, Hechas, etc.)
        when (filtroActual) {
            "PROXIMAS" -> {
                holder.tvDescripcion.visibility = View.GONE
                holder.btnVerDetalle.visibility = View.VISIBLE
                holder.btnVerDetalle.text = "VER"
            }
            "HECHAS" -> {
                holder.tvDescripcion.visibility = View.GONE
                holder.btnVerDetalle.visibility = View.VISIBLE
                holder.btnVerDetalle.text = "VER"
            }
            "PASADAS" -> {
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
     * Actualiza la lista de datos y el filtro, notificando al RecyclerView para refrescarse.
     */
    fun updateList(nuevaLista: List<Tarea>, filtro: String) {
        this.listaTareas = nuevaLista
        this.filtroActual = filtro
        notifyDataSetChanged()
    }
}
