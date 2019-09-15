Package principale GoogleInterface

	//  CLASSI CONTENUTE

	-GoogleInterface: classe per la creazione di un interfaccia per la ricezione delle richieste del servizio 
	                  Google Smart Home e l'autenticazione degli utenti. 
                          La classe genera cinque interfacce:

		            - /smart: gestione delle richieste del servizio Google Smart Home

		            - /oauth: gestione delle richieste di access delegation per le credenziali dell'utente

                            - /token: gestione del rinfresco delle credenziali secondo il modello oauth

                            - /login: fornisce una pagina html contenente un form per l'autenticazione utente

                            - /load:  interfaccia di appoggio a /login per il caricamento di componenti esterni necessari 
                                      alla pagina(file js, css, immagini etc..) Tutti i contenuti utilizzati dalla pagina html
                                      sono accessibili dall'interfaccia inserendoli nella cartella http_data/page_component
 		                      (nella pagina html inserire come uri della risorsa "/load/NOMERISORSA"

	-MyLogger: classe di appoggio per la gestione dell'output, tutte le classi utilizzano una sua istanza
	           che colore di azzurro l'output per renderlo distinguibile da quello generato dal server Leshan.

	// DIRECTORY CONTENUTE
	
	[google_configuration]: mantiene le impostazioni per l'interfaccia di integrazione come il timer
	                        di validità dei token, il nome utente accettato dall'interfaccia,
			        le porte utilizzate dal servizio e la API KEY per raggiungere il servizio terzo.
        
	[http_data]: mantiene la pagina html del login e i componenti utilizzati per la sua elaborazione.
	
	[https_keystore]: mantiene i certificati utilizzati per la comunicazione https. I certificati per essere utilizzabili
			  devono essere autenticati.
	
	[saved_data]: mantiene i dati salvati localmente delle credenziali di autenticazione di oauth2 e dei dispositivi
		      dell'utente rispettivamente nei file authcode.json e devices.json

	//  SOTTOPACKAGE

	- data:  sottopackage di supporto, mantiene le classi per mantenere i tokens e le informazioni caricate dal file 
		di configurazione google_configuration/conf.json 
	
	- integration: sottopackage che gestisce la conversione e integrazione dei dispositivi Leshan secondo il 
		modello di Google Smart Home. Il package mantiene le strutture per definire i dispositivi e realizzare
		l'architettura necessaria alla loro comunicazione e integrazione nel server Leshan

	- protocol: sottopackage per la gestione delle richieste all'interfaccia GoogleInterface. Il protocollo 
		implementa handler statici per gestire le varie richieste effettuate dal servizio Google Smart Home

	 