
Il package di integrazione "googleinterface" è inserito all'interno del modulo Leshan-server-demo. La classe principale è GoogleInterface che crea un'interfaccia verso Google e gestisce le sue richieste. Al termine dell'inizializzazione del Leshan-Server-Demo viene istanziato un oggetto GoogleInterface mettendo in esecuzione il protocollo sviluppato nel package.

La classe principale del server è LeshanServerDemo.java

Per connettersi alla Leshan Demo UI visitare http://localhost:8080  

Il package è sviluppato in 3 sottopackage:

	-data: mantiene le strutture dati utilizzate dal 		  	programma come i dati degli AuthToken o i file di    	configurazione.

	-protocol: mantiene i protocolli utilizzati dal servizio,    
     ovvero la gestione di Oauth2 e di Google Smart Home

	-integration: gestisce la conversione dei dispositivi e 	le loro interazioni con il server LwM2M.

Per registrare un nuovo dispositivi bisogna utilizzare il modulo Leshan-Client-Demo, la classe princiapale del modulo è LeshanClientDemo.java

//////  UTILIZZO NGROK

Per testare velocemente il server può essere utilizzato il file batch ngrok_start disponibile in questa directory. Il batch esegue il programma ngrok.exe creando un tunnel verso la porta 80(porta di default definita nel file di configurazione googleinterface/google_configuration/conf.json). 
Ngrok fornirà due uri identiche, copiare la uri https e utilizzarla su GoogleActions per i link di collegamento contenuti in Develop/Actions(aggiungendo /smart) e Develop/Account Linking/OAuthClientInformation(aggiungendo rispettivamente /oauth e /token). Avviato il test Google inoltrerà le richieste ai link forniti, che il software di tunnel ngrok instraderà all'interfaccia 80 del computer su cui è in esecuzione. I DATI SONO IN CHIARO NON ACCETTA COMUNICAZIONI HTTPS SENZA PAGARE ABBONAMENTO PREMIUM, Ngrok è solo una soluzione adatta al testing.