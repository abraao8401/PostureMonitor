package com.example.teste_conexao

import PostureMonitor
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext


@Composable
fun MainScreen(postureMonitor: PostureMonitor) {
    val context = LocalContext.current 

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFF6F00) 
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícone principal
            Image(
                painter = painterResource(id = R.drawable.ic_postura),
                contentDescription = "Ícone de Postura",
                modifier = Modifier.size(150.dp)
            )

            // Título
            Text(
                text = "Monitoramento Postural",
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )

            // Ícones de status (correto ou alerta)
            if (postureMonitor.isPostureGood.value) {
                Image(
                    painter = painterResource(id = R.drawable.correto),
                    contentDescription = "Postura Adequada",
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.alerta),
                    contentDescription = "Postura Inadequada",
                    modifier = Modifier.size(100.dp)
                )
            }

            // Mensagem de status com fundo arredondado
            val backgroundColor = if (postureMonitor.isPostureGood.value) Color(0xFF388E3C) else Color(0xFFD32F2F)
            Text(
                text = if (postureMonitor.isPostureGood.value) "Postura Adequada" else "Postura Inadequada",
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            )

            // Botão para ver o relatório
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    // Navega para a tela de relatório
                    val intent = Intent(context, ReportActivity::class.java)
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ver Relatório")
            }
        }
    }
}
