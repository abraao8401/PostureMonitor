package com.example.teste_conexao

import PostureMonitor
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.teste_conexao.ui.theme.TesteconexaoTheme
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.MqttException

class MainActivity : ComponentActivity() {

    private lateinit var postureMonitor: PostureMonitor
    private lateinit var mqttClient: MqttClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Permissão de notificação negada", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o PostureMonitor
        postureMonitor = PostureMonitor(applicationContext) // Agora está inicializado

        // Conexão MQTT
        val serverUri = "tcp://broker.hivemq.com:1883"
        val clientId = MqttClient.generateClientId()
        mqttClient = MqttClient(serverUri, clientId, null)

        val options = MqttConnectOptions()
        options.isCleanSession = true

        try {
            mqttClient.connect(options)
            mqttClient.subscribe("arqubi/dados")  // Inscreve-se no tópico onde os dados do sensor são enviados
            mqttClient.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    message?.let {
                        val data = it.toString()
                        // Processa os dados recebidos
                        postureMonitor.processSensorData(data) // Agora não dará erro
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    Toast.makeText(applicationContext, "Conexão perdida", Toast.LENGTH_SHORT).show()
                }

                override fun deliveryComplete(token: org.eclipse.paho.client.mqttv3.IMqttDeliveryToken?) {
                    // Não necessário para o tópico de inscrição
                }
            })
        } catch (e: MqttException) {
            Toast.makeText(this, "Erro ao conectar ao broker MQTT", Toast.LENGTH_SHORT).show()
        }

        // Solicitar permissão de notificação
        requestNotificationPermission()

        // Criar canal de notificação
        createNotificationChannel()

        setContent {
            TesteconexaoTheme {
                MainScreen(postureMonitor) // Agora o postureMonitor está inicializado
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "postura_notificacoes"
            val channelName = "Notificações de Postura"
            val channelDescription = "Notificações para alertar sobre a postura"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
}
