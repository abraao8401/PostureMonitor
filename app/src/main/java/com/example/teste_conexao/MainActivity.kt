package com.example.teste_conexao

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.teste_conexao.ui.theme.TesteconexaoTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            TesteconexaoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Simular dados do sensor e enviá-los para o Firestore
        val sensorDataJson = """{"AcX": 384, "AcY": -92, "AcZ": 15008, "GyX": -131, "GyY": -125, "GyZ": 115}"""
        val sensorData = JSONObject(sensorDataJson)
        sendSensorDataToFirebase(sensorData)
    }

    /**
     * Envia um conjunto de dados do sensor para a coleção "sensorData" no Firestore.
     * @param sensorData Objeto JSONObject contendo os dados do sensor.
     */
    private fun sendSensorDataToFirebase(sensorData: JSONObject) {
        // Obtém a instância do Firestore
        val db = FirebaseFirestore.getInstance()

        // Cria o mapa de dados do sensor
        val data = hashMapOf(
            "AcX" to sensorData.getInt("AcX"),
            "AcY" to sensorData.getInt("AcY"),
            "AcZ" to sensorData.getInt("AcZ"),
            "GyX" to sensorData.getInt("GyX"),
            "GyY" to sensorData.getInt("GyY"),
            "GyZ" to sensorData.getInt("GyZ"),
            "timestamp" to System.currentTimeMillis()
        )

        // Envia os dados para a coleção "sensorData"
        db.collection("sensorData")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Dados do sensor adicionados com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Erro ao adicionar dados do sensor", e)
            }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        TesteconexaoTheme {
            Greeting("Android")
        }
    }
}
