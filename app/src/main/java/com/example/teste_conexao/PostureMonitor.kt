package com.example.teste_conexao

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import android.app.NotificationManager

class PostureMonitor(private val sensorManager: SensorManager, private val context: Context) : SensorEventListener {

    val isPostureGood: MutableState<Boolean> = mutableStateOf(false)
    private var accelerometer: Sensor? = null
    private var wasPostureBad = false // Controle se a postura já foi alertada

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val (x, y, z) = it.values

                val isGood = Math.abs(z) > Math.abs(x) && Math.abs(z) > Math.abs(y)
                isPostureGood.value = isGood

                // Se a postura estiver inadequada e o alerta ainda não foi enviado, envia notificação
                if (!isGood && !wasPostureBad) {
                    wasPostureBad = true
                    showPostureAlertNotification()
                }

                // Se a postura voltou a ser adequada, redefine o controle
                if (isGood) {
                    wasPostureBad = false
                }
            }
        }
    }

    private fun showPostureAlertNotification() {
        val notificationId = 1
        val channelId = "postura_notificacoes"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.alerta) // Ícone de alerta
            .setContentTitle("Atenção à Postura")
            .setContentText("Sua postura está inadequada. Corrija para evitar problemas de saúde.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun onResume() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun onPause() {
        sensorManager.unregisterListener(this)
    }
}
