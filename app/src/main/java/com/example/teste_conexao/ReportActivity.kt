package com.example.teste_conexao

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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

// Classe para os dados do resumo de postura
data class SensorDataSummary(
    val totalRegistros: Int = 0,
    val dataInicial: String = "",
    val dataFinal: String = "",
    val porcentagemPosturaCorreta: String = "",
    val porcentagemPosturaIncorreta: String = "",
    val duracaoTotal: String = "",
    val tempoPosturaCorreta: String = "",
    val tempoPosturaIncorreta: String = ""
)

class ReportActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            TesteconexaoTheme {
                ReportScreen()
            }
        }
    }
}

@Composable
fun ReportScreen() {
    val sensorDataList = remember { mutableStateListOf<SensorData>() }
    val sensorSummary = remember { mutableStateOf<SensorDataSummary?>(null) }
    val context = LocalContext.current // Acesse o contexto local

    // Buscar os dados históricos do Firestore
    LaunchedEffect(Unit) {
        // Buscar dados resumidos
        FirebaseFirestore.getInstance()
            .collection("sensorDataSummary")
            .document("latestReport")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val summary = documentSnapshot.toObject(SensorDataSummary::class.java)
                sensorSummary.value = summary
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erro ao carregar resumo", Toast.LENGTH_SHORT).show()
            }

        // Buscar os dados históricos de sensor
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

        // Exibe o resumo de postura
        sensorSummary.value?.let { summary ->
            PostureSummaryCard(summary)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Exibe os dados históricos em uma lista
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
fun PostureSummaryCard(summary: SensorDataSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(8.dp) // Sombra ao redor do card
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Resumo de Postura",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total de Registros: ${summary.totalRegistros}")
                Icon(Icons.Filled.Check, contentDescription = "Success", tint = Color(0xFF388E3C))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Data de Início: ${summary.dataInicial}")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Data de Fim: ${summary.dataFinal}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display de Postura
            PostureDetailRow(
                label = "Postura Correta",
                value = "${summary.porcentagemPosturaCorreta}%",
                isCorrect = true
            )

            PostureDetailRow(
                label = "Postura Incorreta",
                value = "${summary.porcentagemPosturaIncorreta}%",
                isCorrect = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Duração Total: ${summary.duracaoTotal}")
            Text(text = "Tempo de Postura Correta: ${summary.tempoPosturaCorreta}")
            Text(text = "Tempo de Postura Incorreta: ${summary.tempoPosturaIncorreta}")
        }
    }
}

@Composable
fun PostureDetailRow(label: String, value: String, isCorrect: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(
            text = value,
            color = if (isCorrect) Color(0xFF388E3C) else Color(0xFFD32F2F),
            fontWeight = FontWeight.Bold
        )
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
