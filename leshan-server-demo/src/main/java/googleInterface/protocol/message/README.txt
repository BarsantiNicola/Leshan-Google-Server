
La directory mantiene tutti classi POJO per implementare i messaggi utilizzati dal Google Smart Home Protocol
Le classi principali sono SyncRequest,SyncResponse,QueryRequest,QueryResponse,ExecRequest,ExecResponse.
Le altre classi sono utilizzate per compilare i vari campi delle classi principali

Altre importanti classi sono SyncDevice che mantiene le informazioni formattate di un singolo dispositivo
e ExecDevice che mantiene le risposte ai comandi dei singoli dispositivi
La classe QueryDevice non è implementata a causa dell'alta variabilità delle risposte ho ritenuto più
semplice gestire tramite un HashMap che mantiene le risposte.

La classe CustomData è definita ma non implementata, è definita dallo standard Google per inserire informazioni
aggiuntive per i dispositivi.