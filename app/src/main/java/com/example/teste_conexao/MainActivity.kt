package com.example.teste_conexao

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.teste_conexao.MainScreen
import com.example.teste_conexao.PostureMonitor
import com.example.teste_conexao.ui.theme.TesteconexaoTheme

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var postureMonitor: PostureMonitor

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Permissão de notificação negada", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o SensorManager e o PostureMonitor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        postureMonitor = PostureMonitor(sensorManager, this)

        // Solicita a permissão de notificação
        requestNotificationPermission()

        // Cria o canal de notificação
        createNotificationChannel()

        setContent {
            TesteconexaoTheme {
                MainScreen(postureMonitor)
            }
        }
    }


    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        postureMonitor.onResume() // Registra o listener do acelerômetro
    }

    override fun onPause() {
        super.onPause()
        postureMonitor.onPause() // Desregistra o listener do acelerômetro
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "postura_notificacoes"
            val channelName = "Notificações de Postura"
            val channelDescription = "Notificações para alertar sobre a postura"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            // Registrar o canal
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
