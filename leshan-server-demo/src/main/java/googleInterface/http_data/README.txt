La directory contiene i file utilizzati per supportare un piccolo web server per l'autenticazione dell'utente.
login.html è la pagina per il login che viene presentata dal server, qualunque file/immagine/oggetto venga utilizzato dalla pagina deve
essere inserito in page_component( è presente una routine che carica gli oggetti locali richiesti dalla pagina page_component )

ATTENZIONE: il web server mantiene solo lo stretto necessario a mostrare una pagina html eseguire javascript e caricare eventuali oggetti

               L'INTERFACCIA DI LOGIN VIENE MOSTRATA DALLA CLASSE googleinterface.protocol.OauthProtocol NELL'INTERFACCIA /LOGIN