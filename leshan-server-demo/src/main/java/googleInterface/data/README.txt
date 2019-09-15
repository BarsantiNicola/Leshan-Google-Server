Sottopackage googleinterface.data
	
	//  CLASSI CONTENUTE

	- AuthCode:  classe per la gestione delle autenticazione dell'utente. Mantiene i tokens dell'utente( Authorization Grant Code, AuthToken, RefreshToken)
		     associandogli un timestamp per definirne la validità. Ad ogni accesso per prelevare i tokens verifica la loro validità ed eventualmente
		     li elimina(se scaduti). La classe fornisce metodi per creare i tokens e ottenerli su richiesta. La classe viene salvata localmente nel file
		     googleinterface/savedData/AuthData.bin

	- GoogleConfiguration:  classe POJO per il mantenimento delle informazioni necessarie all'impostazione dell'interfaccia e della comunicazione con Google.
				File dei dati di configurazione: googleinterface/google_configuration/conf.json 
										

