package com.example.teste_conexao

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teste_conexao.ui.theme.TesteconexaoTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import androidx.compose.ui.platform.LocalContext

// Classe de dados do sensor
data class SensorData(
    val AcX: Int = 0,
    val AcY: Int = 0,
    val AcZ: Int = 0,
    val GyX: Int = 0,
    val GyY: Int = 0,
    val GyZ: Int = 0,
    val timestamp: Long = 0,
    val postureStatus: String = ""
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            TesteconexaoTheme {
                // Exibe o relatório de dados históricos
                ReportScreen()
            }
        }
    }
}

@Composable
fun ReportScreen() {
    val sensorDataList = remember { mutableStateListOf<SensorData>() }
    val context = LocalContext.current // Acesse o contexto local

    // Buscar os dados históricos do Firestore
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("sensorData")
            .orderBy("processedAt", Query.Direction.DESCENDING) // Ordenar por data
            .get()
            .addOnSuccessListener { querySnapshot ->
                sensorDataList.clear() // Limpa a lista antes de adicionar novos dados
                querySnapshot.documents.forEach { document ->
                    val sensorData = document.toObject(SensorData::class.java)
                    sensorData?.let { sensorDataList.add(it) }
                }
            }
            .addOnFailureListener {
                // Usando LocalContext.current para criar o Toast dentro de um contexto Composable
                Toast.makeText(context, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
    }

    // Exibição do relatório com os dados históricos
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Relatório de Dados Históricos", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Exibe os dados em uma lista
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(sensorDataList) { data ->
                ReportItem(sensorData = data)
            }
        }
    }
}

@Composable
fun ReportItem(sensorData: SensorData) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Timestamp: ${sensorData.timestamp}")
        Text(text = "AcX: ${sensorData.AcX}, AcY: ${sensorData.AcY}, AcZ: ${sensorData.AcZ}")
        Text(text = "GyX: ${sensorData.GyX}, GyY: ${sensorData.GyY}, GyZ: ${sensorData.GyZ}")
        Text(text = "Postura: ${sensorData.postureStatus}")
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TesteconexaoTheme {
        ReportScreen()
    }
}
