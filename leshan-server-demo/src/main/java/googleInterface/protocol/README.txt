La directory "protocol" implementa i metodi per la gestione delle richieste al servizio

------ GOOGLEMESSAGEHANDLER ------

  classe utilizzata per prelevare le informazioni dalle richieste ricevute e organizzandole
  in modo funzionale per il programmatore. I messaggi vengono suddivisi per tipologia e le variabili
  del messaggio mantenute per poter essere lette facilmente

------ OAUTHPROTOCOL ------

//  la classe si occupa di gestire le credenziali utente secondo il protocollo oAuth2
//  i suoi compiti principali sono permettere l'autenticazione di un utente al servizio
//  tramite una pagina di login e creare,mantenere e aggiornare le sue credenziali di access delegation


------ SMARTHOMEPROTOCOL ------

// classe per implementare metodi statici per gestire le richieste smart al servizio
//  la classe si occupa di gestire le richieste di SYNC, RESYNC, QUERY, EXECUTE E DISCONNECT