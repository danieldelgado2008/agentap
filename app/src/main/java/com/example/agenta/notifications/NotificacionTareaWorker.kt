package com.example.agenta.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.agenta.R

class NotificacionTareaWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val titulo = inputData.getString("TITULO_TAREA") ?: "Recordatorio de Tarea"
        val mensaje = inputData.getString("MENSAJE_NOTIFICACION") ?: "Tienes una tarea pendiente"
        val notificationId = inputData.getInt("ID_TAREA", 1)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(applicationContext, "CANAL_TAREAS")
            .setSmallIcon(R.drawable.ic_logo_app)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
        }

        return Result.success()
    }
}
