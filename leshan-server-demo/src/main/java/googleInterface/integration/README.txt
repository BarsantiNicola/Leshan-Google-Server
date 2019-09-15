SottoPackage googleInterface.integration

Il package si occupa della conversione dei client LwM2M in dispositivi compatibili con Google Smart Home

	//  CLASSI UTILIZZATE

	- Device: la classe mantiene un dispositivo compatibile con il servizio Google Smart Home. Un device contiene
		  le informazioni per le richieste di Sync e un insieme di classi estese da Trait per gestire le action supportate.
		  Il device si occupa di risponde alle richieste di Sync e attraverso i Trait compilare le risposte alle richieste di Query.
		  Il device inoltre instrada le notify di Leshan e i comandi dell'utente al Trait appropriato per la loro gestione.

	- DeviceData: la classe implementa un archivio di dispositivi Device. La classe riceve le notify provenienti da Leshan e le richieste effettuate
		      dall'utente ai Device appropriati e unisce ed elabora i messaggi ricevuti per creare il messaggio da inviare al servizio Google Smart Home.

	- LeshanDevice:la classe implementa un ponte con il server Leshan. Viene utilizzata dalle classi Device e Trait per eseguire operazioni
		       di observe, discover, read, write, execute sui client LwM2M.

	- LeshanResource: classe per fornire le risorse da leggere a LeshanDevice. Ogni risorsa è composta da un OID( Object ID)
			  e opzionalmente da un istanza e un RID( Resource ID). La classe permette di lavorare sulle risorse in maniera semplice
		          e convertire la risorsa secondo il formato richiesto dai client LwM2M.

	//  CLASSI POJO PER CARICARE I FILE JSON

	- TraitConvertion: classe POJO, mantiene le associazioni tra un action e i comandi utilizzati(necessaria a Device per instradare successivamente i comandi
                              al Trait appropriato. Mantiene LeshanResourceConvertion per ogni risorsa necessaria a implementare la action(Trait) nel Device.

	- LeshanResourceConvertion: classe POJO, mantiene una risorsa obbligatoria per l'implementazione di un Trait. La classe mantiene la risorsa principale e un insieme
                                    di alias definiti tramite ResourceConvertion. La classe mantiene anche un eventuale state associato alla risorsa.


	- ResourceConvertion: mantiene una risorsa definendola come OID e RID (le compatibilità dell'azione non coinvolge il numero di istanze della risorsa che può essere variabile)

	//  DIRECTORY CONTENUTE

	[convertion]: mantiene i file necessari alla conversione dei client LwM2M nei dispositivi compatibili con Google Smart Home

		- device.json: mantiene le associazioni tra il tipo di un dispositivo e le actions definite consigliate da Google
		- traits.json: mantiene le informazioni tra le action e le risorse che devono essere presenti nel client LwM2M per poterle implementare.
			       Le singole risorse obbligatorie hanno a disposizione un insieme di alias, risorse alternative per rendere compatibile l'azione.
			       Sono presenti anche i comandi disponibili per quell'azione(utilizzati da Device per determinare Trait a cui inoltrare il comando)
			       e gli states(non obbligatorio, inserito per poter centralizzare le informazioni utilizzate dal programma in un file senza disperderle nel codice)

	//  SOTTOPACKAGE 

		- actions: package per la gestione delle actions Google. Fornisce le classe necessarie a gestire tutte le actions Google. 
	