package com.example.agenta.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agenta.R
import com.example.agenta.models.Tarea

class AdaptadorTareas(
    private var listaTareas: List<Tarea>,
    private val onVerClick: (Tarea) -> Unit,
    private val onHechaClick: (Tarea) -> Unit
) : RecyclerView.Adapter<AdaptadorTareas.TareaViewHolder>() {

    private var filtroActual: String = "PROXIMAS"

    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMateria: TextView = view.findViewById(R.id.tvMateria)
        val tvTarea: TextView = view.findViewById(R.id.tvTarea)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val btnVerDetalle: Button = view.findViewById(R.id.btnVerDetalle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = listaTareas[position]
        
        holder.tvMateria.text = tarea.materia
        holder.tvTarea.text = tarea.titulo
        holder.tvFecha.text = tarea.fechaEntrega
        holder.tvDescripcion.text = tarea.descripcion

        holder.btnVerDetalle.setOnClickListener {
            if (holder.btnVerDetalle.text == "VER") {
                onVerClick(tarea)
            } else {
                onHechaClick(tarea)
            }
        }

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

    fun updateList(nuevaLista: List<Tarea>, filtro: String) {
        this.listaTareas = nuevaLista
        this.filtroActual = filtro
        notifyDataSetChanged()
    }
}
