package com.example.agenta.notifications

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.agenta.models.Tarea
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Objeto encargado de programar notificaciones de recordatorio para las tareas.
 * Utiliza WorkManager para ejecutar tareas en segundo plano de forma eficiente.
 */
object GestorNotificaciones {

    @RequiresApi(Build.VERSION_CODES.O)
    fun programarNotificaciones(context: Context, tarea: Tarea) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaTarea: LocalDate
        try {
            // Intentar parsear la fecha de la tarea
            fechaTarea = if (tarea.fechaEntrega.length == 5) {
                LocalDate.parse("${tarea.fechaEntrega}/2026", formatter)
            } else {
                LocalDate.parse(tarea.fechaEntrega, formatter)
            }
        } catch (e: Exception) {
            return // Si la fecha tiene un formato inválido, no programar nada
        }

        // Programar una notificación para el día anterior a la entrega a las 9 AM
        val fechaNotificacion = fechaTarea.minusDays(1).atTime(LocalTime.of(9, 0))
        val ahora = LocalDateTime.now()

        // Solo programar si la fecha del recordatorio es futura
        if (fechaNotificacion.isAfter(ahora)) {
            val delay = java.time.Duration.between(ahora, fechaNotificacion).toMillis()
            
            // Datos que se pasarán al Worker cuando se ejecute
            val data = Data.Builder()
                .putInt("tareaId", tarea.id)
                .putString("titulo", "Recordatorio: ${tarea.titulo}")
                .putString("mensaje", "Mañana vence tu tarea de ${tarea.materia}")
                .build()

            // Crear la solicitud de trabajo con un retardo (delay)
            val workRequest = OneTimeWorkRequestBuilder<NotificacionTareaWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("notificacion_${tarea.id}")
                .build()

            // Encolar el trabajo en WorkManager
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
