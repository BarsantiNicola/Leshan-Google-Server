La gestione della comunicazione dei certificati RSA avviene tramite due servizi il keystore lato server e il truststore lato client.
Il keystore mantiene solamente le chiavi rsa necessarie alla comunicazione https di un server al contrario del truststore che mantiene
le chiavi delle comunicazioni in atto.


   IL FILE KEYSTORE.JKS E' UTILIZZATO DALLA CLASSE googleinterface.GoogleInterface PER INIZIALIZZARE INTERFACCIA HTTPS



   -per realizzare il keystore utilizzare questo codice di esempio in cui si utilizza il servizio keytool di java/bin

   1)  Creare un server keystore

        keytool -genkey -alias DOMINIO -keyalg RSA -keystore KeyStore.jks -keysize 2048

   2) Generare un file csr del keystore per autenticare il certificato con GlobalSign Certificates e ottenere un certificato crt( csr autenticato )

        keytool -certreq -alias DOMINIO -keystore KeyStore.jks -file DOMINIO.csr

   3) Aggiungere al keystore dei domini root e intermediate(da scaricare dal sito GlobalSign Certificates )

        keytool -import -trustcacerts -alias root -file root.crt -keystore KeyStore.jks

        keytool -import -trustcacerts -alias intermediate -file intermediate.crt -keystore KeyStore.jks

    4) Una volta ottenuto il certificato da Global Sign inserire anche questo nel keystore( non è essenziale ma obbligatorio per avere una connessione riconosciuta sicura)

        keytool -import -trustcacerts -alias mydomain -file DOMINIO.crt -keystore KeyStore.jks

    5) Inserire il file KeyStore.jks nella cartella( eventualmente rinominarlo )

