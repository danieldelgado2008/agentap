package com.example.agenta.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.agenta.R

/**
 * Worker que se ejecuta en segundo plano para mostrar la notificación real al usuario.
 */
class NotificacionTareaWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        // Recuperar los datos enviados por el GestorNotificaciones
        val tareaId = inputData.getInt("tareaId", 0)
        val titulo = inputData.getString("titulo") ?: "Recordatorio de Tarea"
        val mensaje = inputData.getString("mensaje") ?: "Tienes una tarea pendiente"

        // Mostrar la notificación en el sistema
        mostrarNotificacion(tareaId, titulo, mensaje)

        return Result.success()
    }

    /**
     * Construye y muestra la notificación física en el dispositivo.
     */
    private fun mostrarNotificacion(id: Int, titulo: String, mensaje: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(applicationContext, "CANAL_TAREAS")
            .setSmallIcon(R.mipmap.ic_launcher) // Icono de la notificación
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Se cierra al tocarla

        // Se requiere permiso de notificaciones para que esto funcione en Android 13+
        try {
            notificationManager.notify(id, builder.build())
        } catch (e: SecurityException) {
            // Manejar falta de permisos si es necesario
        }
    }
}
