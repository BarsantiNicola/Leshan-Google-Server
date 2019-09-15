
Il package di integrazione "googleinterface" � inserito all'interno del modulo Leshan-server-demo. La classe principale � GoogleInterface che crea un'interfaccia verso Google e gestisce le sue richieste. Al termine dell'inizializzazione del Leshan-Server-Demo viene istanziato un oggetto GoogleInterface mettendo in esecuzione il protocollo sviluppato nel package.

La classe principale del server � LeshanServerDemo.java

Per connettersi alla Leshan Demo UI visitare http://localhost:8080  

Il package � sviluppato in 3 sottopackage:

	-data: mantiene le strutture dati utilizzate dal 		  	programma come i dati degli AuthToken o i file di    	configurazione.

	-protocol: mantiene i protocolli utilizzati dal servizio,    
     ovvero la gestione di Oauth2 e di Google Smart Home

	-integration: gestisce la conversione dei dispositivi e 	le loro interazioni con il server LwM2M.

Per registrare un nuovo dispositivi bisogna utilizzare il modulo Leshan-Client-Demo, la classe princiapale del modulo � LeshanClientDemo.java

//////  UTILIZZO NGROK

Per testare velocemente il server pu� essere utilizzato il file batch ngrok_start disponibile in questa directory. Il batch esegue il programma ngrok.exe creando un tunnel verso la porta 80(porta di default definita nel file di configurazione googleinterface/google_configuration/conf.json). 
Ngrok fornir� due uri identiche, copiare la uri https e utilizzarla su GoogleActions per i link di collegamento contenuti in Develop/Actions(aggiungendo /smart) e Develop/Account Linking/OAuthClientInformation(aggiungendo rispettivamente /oauth e /token). Avviato il test Google inoltrer� le richieste ai link forniti, che il software di tunnel ngrok instrader� all'interfaccia 80 del computer su cui � in esecuzione. I DATI SONO IN CHIARO NON ACCETTA COMUNICAZIONI HTTPS SENZA PAGARE ABBONAMENTO PREMIUM, Ngrok � solo una soluzione adatta al testing.