package com.example.agenta.models

data class Tarea(
    val id: Int,
    val materia: String,
    val titulo: String,
    val fechaEntrega: String,
    val descripcion: String,
    var estaHecha: Boolean = false
)