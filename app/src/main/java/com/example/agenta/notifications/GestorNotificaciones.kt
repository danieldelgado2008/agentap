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
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

object GestorNotificaciones {

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    fun programarNotificaciones(context: Context, tarea: Tarea) {
        val hoy = LocalDate.now()
        val fechaEntrega = parsearFechaSegura(tarea.fechaEntrega) ?: return

        val diasDiferencia = ChronoUnit.DAYS.between(hoy, fechaEntrega)

        if (diasDiferencia >= 7) {
            programarAviso(context, tarea, 7, "Tu tarea '${tarea.titulo}' vence en una semana")
        }

        if (diasDiferencia >= 1) {
            programarAviso(context, tarea, 1, "Mañana se entrega '${tarea.titulo}'")
        }

        if (diasDiferencia >= 0) {
            programarAviso(context, tarea, 0, "¡Hoy se entrega '${tarea.titulo}'!")
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun programarAviso(context: Context, tarea: Tarea, diasAntes: Long, mensaje: String) {
        val hoy = LocalDateTime.now()
        val fechaEntrega = parsearFechaSegura(tarea.fechaEntrega) ?: return
        val horaAviso = LocalTime.of(8, 0)

        val momentoAviso = fechaEntrega.minusDays(diasAntes).atTime(horaAviso)

        if (momentoAviso.isAfter(hoy)) {
            val delaySegundos = ChronoUnit.SECONDS.between(hoy, momentoAviso)
            
            val data = Data.Builder()
                .putString("TITULO_TAREA", "Recordatorio de Tarea")
                .putString("MENSAJE_NOTIFICACION", mensaje)
                .putInt("ID_TAREA", tarea.id + (diasAntes * 1000).toInt())
                .build()

            val request = OneTimeWorkRequestBuilder<NotificacionTareaWorker>()
                .setInitialDelay(delaySegundos, TimeUnit.SECONDS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
