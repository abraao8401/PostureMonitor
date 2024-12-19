import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.core.app.NotificationCompat
import com.example.teste_conexao.R
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import kotlin.math.sqrt

class PostureMonitor(private val context: Context) {

    private val _isPostureGood = mutableStateOf(false)  // MutableState para controle de postura
    val isPostureGood: State<Boolean> get() = _isPostureGood  // Exposição como State imutável

    private var wasPostureBad = false

    // Valor de limiar de magnitude para determinar se a postura é adequada ou não
    private val postureThreshold = 15000

    // Instância do Firestore
    private val firestore = FirebaseFirestore.getInstance()

    // Processar dados recebidos via MQTT
    fun processSensorData(data: String) {
        try {
            // Converte os dados JSON recebidos
            val jsonData = JSONObject(data)
            val acX = jsonData.getInt("AcX")
            val acY = jsonData.getInt("AcY")
            val acZ = jsonData.getInt("AcZ")
            val gyX = jsonData.getInt("GyX")
            val gyY = jsonData.getInt("GyY")
            val gyZ = jsonData.getInt("GyZ")

            // Calcula a magnitude do vetor de aceleração (AcX, AcY, AcZ)
            val magnitude = sqrt((acX * acX + acY * acY + acZ * acZ).toDouble())

            // Determina se a postura é boa com base na magnitude
            val isGoodPosture = magnitude <= postureThreshold

            // Se a postura for inadequada e o alerta não foi enviado
            if (!isGoodPosture && !wasPostureBad) {
                wasPostureBad = true
                showPostureAlertNotification()
            }

            // Se a postura estiver boa, reinicia a lógica
            if (isGoodPosture) {
                wasPostureBad = false
            }

            // Atualiza o estado de postura
            _isPostureGood.value = isGoodPosture

            // Envia os dados do sensor para o Firestore
            sendSensorDataToFirebase(acX, acY, acZ, gyX, gyY, gyZ)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showPostureAlertNotification() {
        // Exibe a notificação quando a postura for ruim
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

    /**
     * Envia um conjunto de dados do sensor para a coleção "sensorData" no Firestore.
     * @param acX, acY, acZ, gyX, gyY, gyZ Dados do sensor.
     */
    private fun sendSensorDataToFirebase(acX: Int, acY: Int, acZ: Int, gyX: Int, gyY: Int, gyZ: Int) {
        // Cria um mapa de dados do sensor
        val data = hashMapOf(
            "AcX" to acX,
            "AcY" to acY,
            "AcZ" to acZ,
            "GyX" to gyX,
            "GyY" to gyY,
            "GyZ" to gyZ,
            "timestamp" to System.currentTimeMillis()
        )

        // Envia os dados para a coleção "sensorData" no Firestore
        firestore.collection("sensorData")
            .add(data)
            .addOnSuccessListener {
                Log.d("Firebase", "Dados do sensor enviados com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Erro ao enviar dados para o Firestore", e)
            }
    }

    fun onResume() {
        
    }

    fun onPause() {
       
    }
}
