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
 * Esta clase es el "Dibujante" de la lista de tareas.
 * Su trabajo es tomar la información de cada tarea y acomodarla en los cuadritos 
 * que ves cuando abres la aplicación (vincular datos con diseño).
 */
class AdaptadorTareas(
    private var listaTareas: List<Tarea>,
    private val onVerClick: (Tarea) -> Unit, // Qué pasa al tocar "VER"
    private val onHechaClick: (Tarea) -> Unit // Qué pasa al tocar el botón de completar
) : RecyclerView.Adapter<AdaptadorTareas.TareaViewHolder>() {

    // Guarda qué tipo de tareas estamos viendo ahora mismo (Próximas, Todas, etc.)
    private var filtroActual: String = "PROXIMAS"

    /**
     * Esta clase interna guarda las referencias a las etiquetas de texto 
     * y botones de cada cuadrito de tarea.
     */
    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMateria: TextView = view.findViewById(R.id.tvMateria)
        val tvTarea: TextView = view.findViewById(R.id.tvTarea)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val btnVerDetalle: Button = view.findViewById(R.id.btnVerDetalle)
    }

    /**
     * Esta función crea el "molde" o cuadrito vacío de la tarea.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    /**
     * Aquí es donde "rellenamos" el cuadrito con la información real de una tarea.
     */
    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = listaTareas[position]
        
        // Ponemos los textos en sus etiquetas correspondientes
        holder.tvMateria.text = tarea.materia
        holder.tvTarea.text = tarea.titulo
        holder.tvFecha.text = tarea.fechaEntrega
        holder.tvDescripcion.text = tarea.descripcion

        // Configuramos qué hace el botón al tocarlo
        holder.btnVerDetalle.setOnClickListener {
            if (holder.btnVerDetalle.text == "VER") {
                onVerClick(tarea)
            } else {
                onHechaClick(tarea)
            }
        }

        // Aquí decidimos qué partes del cuadrito mostrar según el filtro que elegimos
        when (filtroActual) {
            "PROXIMAS", "HECHAS", "PASADAS" -> {
                holder.tvDescripcion.visibility = View.GONE // Ocultamos la descripción larga
                holder.btnVerDetalle.visibility = View.VISIBLE
                holder.btnVerDetalle.text = "VER"
            }
            "TODAS" -> {
                holder.tvDescripcion.visibility = View.VISIBLE // Mostramos todo el texto
                holder.btnVerDetalle.visibility = View.VISIBLE
                holder.btnVerDetalle.text = "VER"
            }
        }
    }

    /**
     * Le dice a la lista cuántas tareas tiene que dibujar en total.
     */
    override fun getItemCount() = listaTareas.size

    /**
     * Esta función se usa para cambiar las tareas que se muestran cuando filtras o buscas algo.
     */
    fun updateList(nuevaLista: List<Tarea>, filtro: String) {
        this.listaTareas = nuevaLista
        this.filtroActual = filtro
        notifyDataSetChanged() // Le avisa a la pantalla que debe redibujar la lista
    }
}
