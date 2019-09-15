Il file conf.json va editato con i parametri forniti da google e quelli di personalizzazione dell'interfaccia.
  Parametri contenuti:

        - SmarthomeEndpoint     Indirizzo IPv4 per l'interfaccia per Google
        - SmarthomePort         Porta utilizzata per ricevere i messaggi dell'interfaccia per Google
        - SmarthomeSecurePort   Porta utilizzata per ricevere i messaggi dell'interfaccia per Google utilizzando il servizio sicuro(https)
	- SmartHomeRefreshTime: Durata in secondi della validità dei Tokens utente.        
        - GoogleClientId:       Identificatore dell'utente sul servizio Google, unico ID accettato dal servizio
	- RequestSyncEndPoint:  Path utilizzato da Google per inoltrare ReSync al servizio
	- GoogleAPIKey:         Identifica il servizio in GoogleSmartHome, da passare come variabile a RequesySyncEndPoint
	- ServiceUsername:      Nome utente richiesto nella fase di login
	- ServicePassword:      Password dell'utente richiesta nella fase di login
	- AgentUserId:          Identificatore dell'utente sul servizio

     IL FILE conf.json VIENE UTILIZZATO DALLA CLASSE GoogleConfiguration DEL PACKAGE GOOGLEINTERFACE.DATA
