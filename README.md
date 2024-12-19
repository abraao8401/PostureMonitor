# Posture Monitoring App

### Este projeto consiste em um aplicativo Android que monitora a postura do usuário em tempo real utilizando dados de sensores, como acelerômetro e giroscópio. O app captura esses dados e os envia para a nuvem para processamento e análise, além de fornecer notificações em tempo real sobre a postura do usuário e relatórios sobre o comportamento postural ao longo do tempo.
## Tecnologias Utilizadas

-  Android Studio (Kotlin): Utilizado para o desenvolvimento do aplicativo Android, garantindo uma experiência de usuário fluída e eficiente.
- Jetpack Compose: Framework moderno para estilização e criação de interfaces de usuário de maneira declarativa.
- Google Cloud Platform (GCP): A nuvem utilizada para armazenar dados, processar informações e gerar relatórios.
        Firebase Firestore: Banco de dados NoSQL usado para armazenar dados de sensores, informações sobre posturas e registros históricos.
        Firebase Functions: Funções em nuvem para processamento de dados, cálculo da postura e geração de relatórios.
- MQTT Broker Público (broker.hivemq.com): Utilizado para inscrição e publicação de tópicos. O aplicativo se inscreve no tópico arqbi/dados para receber os dados do acelerômetro e giroscópio em formato JSON.
    Acelerômetro e Giroscópio: Sensores no dispositivo Android usados para captar dados relacionados ao movimento e orientação do corpo do usuário.

## Fluxo do Aplicativo

- Captura de Dados: O aplicativo se inscreve no tópico MQTT arqbi/dados para capturar dados de sensores (acelerômetro e giroscópio) em tempo real.
- Processamento de Dados: Quando um novo conjunto de dados é recebido, ele é processado em tempo real:
- A magnitude da aceleração é calculada utilizando os dados do acelerômetro.
-  A postura do usuário é classificada como "adequada" ou "inadequada" com base em um critério predefinido.
- Notificações em Tempo Real: Sempre que o aplicativo detecta uma postura inadequada, o usuário recebe uma notificação informando sobre a correção necessária.
- Armazenamento no Firestore: Os dados dos sensores e o status da postura são armazenados no Firebase Firestore para análise posterior.
- Geração de Relatórios: Periodicamente, o Firebase Functions processa os dados armazenados no Firestore e gera um relatório contendo:
     - Número total de registros.
     - Porcentagem de posturas adequadas e inadequadas.
     -  Duração total do monitoramento.
 
  
![image](https://github.com/user-attachments/assets/64c8e532-9259-4264-98ca-cb43662e7ad5)  ![image](https://github.com/user-attachments/assets/32e0d063-3d04-4496-99f6-88f9de398366)


## Módulo de Notificações em Tempo Real

O aplicativo possui um sistema de notificações que alerta o usuário em tempo real sobre a sua postura. Sempre que a postura do usuário for detectada como inadequada, uma notificação será enviada para o dispositivo, solicitando que o usuário corrija sua posição para evitar problemas de saúde. As notificações são baseadas no processamento dos dados do acelerômetro e giroscópio.

![image](https://github.com/user-attachments/assets/8fcab2b3-3052-421c-9007-d162cf32dcf2)


## Módulo de Relatórios

O sistema também gera relatórios detalhados sobre o comportamento postural do usuário. O Firebase Functions processa todos os dados registrados no Firestore e gera um resumo com as seguintes informações:

    Quantidade total de registros.
    Porcentagem de posturas adequadas e inadequadas ao longo do tempo.
    Duração total do monitoramento, mostrando o tempo total em que o usuário foi monitorado.
    Tempo de postura adequada e inadequada.

Esses dados são armazenados na coleção sensorDataSummary no Firestore, onde os relatórios podem ser acessados a qualquer momento.

![image](https://github.com/user-attachments/assets/f3cf7561-1448-4470-9b2b-cf84e6d6be5b)



## Conclusão

O Posture Monitoring App é uma solução eficiente para monitoramento e melhoria da postura do usuário, com características de computação ubíqua. Utilizando sensores em dispositivos móveis, processamento em nuvem e notificações em tempo real, o aplicativo oferece uma experiência contínua e integrada ao cotidiano do usuário, sem interrupções. Ele monitora a postura de forma constante, fornecendo correções quando necessário, e gera relatórios sobre o progresso ao longo do tempo. Essa abordagem ubíqua permite que o usuário seja constantemente assistido, promovendo uma postura correta de maneira dinâmica, transparente e adaptada ao contexto, o que facilita a adoção de hábitos saudáveis sem exigir interações constantes com o sistema.
