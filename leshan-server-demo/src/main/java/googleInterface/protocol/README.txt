
Sottopackage googleInterface.protocol

Il package implementa gli handler per la gestione delle richieste al servizio suddivise in richieste
di access delegation e richieste Smart Home.

	//  CLASSI UTILIZZATE

	 - GoogleMessageHandler: classe estesa da HttpHandler viene utilizzata per prelevare le informazioni dalle richieste ricevute e organizzandole
  	   in modo funzionale per il programmatore. I messaggi vengono suddivisi per tipologia e le variabili
  	   del messaggio mantenute per poter essere lette facilmente

	 -  OAuthProtocol: la classe si occupa di gestire le credenziali utente secondo il protocollo oAuth2
                           i suoi compiti principali sono permettere l'autenticazione di un utente al servizio
                           tramite una pagina di login e creare,mantenere e aggiornare le sue credenziali di access delegation
	
	  - SmartHomeProtocol: classe per implementare metodi statici per gestire le richieste smart al servizio
                               la classe si occupa di gestire le richieste di SYNC, RESYNC, QUERY, EXECUTE E DISCONNECT

	//  SOTTOPACKAGE

	-message: il package mantiene le classi POJO per tradurre in classi e viceversa i messaggi GSON della 
		  comunicazione con Google Smart Home.