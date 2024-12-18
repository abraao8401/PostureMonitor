package com.example.teste_conexao

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teste_conexao.ui.theme.TesteconexaoTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

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
                Toast.makeText(context, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
    }

    // Exibição do relatório com os dados históricos
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)) // Cor de fundo da tela
            .padding(16.dp)
    ) {
        Text(
            text = "Relatório de Dados Históricos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2) // Azul escuro
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Exibe os dados em uma lista
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espaçamento entre os itens
        ) {
            items(sensorDataList) { data ->
                ReportItem(sensorData = data)
            }
        }
    }
}

@Composable
fun ReportItem(sensorData: SensorData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE)), // Cor de fundo clara
        elevation = CardDefaults.cardElevation(8.dp) // Sombra ao redor do card
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Timestamp: ${sensorData.timestamp}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20) // Verde escuro
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Aceleração",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD84315) // Laranja
            )
            Text(text = "AcX: ${sensorData.AcX}, AcY: ${sensorData.AcY}, AcZ: ${sensorData.AcZ}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Giroscópio",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD84315) // Laranja
            )
            Text(text = "GyX: ${sensorData.GyX}, GyY: ${sensorData.GyY}, GyZ: ${sensorData.GyZ}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Postura: ${sensorData.postureStatus}",
                color = if (sensorData.postureStatus == "Correta") Color(0xFF2E7D32) else Color(0xFFD32F2F), // Verde ou Vermelho
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TesteconexaoTheme {
        ReportScreen()
    }
}
