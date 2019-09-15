package googleInterface;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.*;
import javax.net.ssl.*;
import java.security.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import googleInterface.data.AuthServer;
import googleInterface.data.GoogleConfiguration;
import googleInterface.integration.LeshanGoogleConverter;
import googleInterface.protocol.GoogleMessageHandler;
import googleInterface.integration.DeviceData;
import org.eclipse.leshan.server.LwM2mServer;


//  interfaccia per la comunicazione con Google

//  implementa un server http(o https) e implementa le interfacce necessarie per la comunicazione
//  con Google

////  ATTENZIONE

//  L'INTERFACCIA HTTPS NECESSITA DI UN CERTIFICATO RSA VALIDO PER COMUNICARE CON GOOGLE.
//  NELLA REALIZZAZIONE DEL SERVER E' STATA UTILIZZATA L'INTERFACCIA HTTP COLLEGANDOLA AL RELAY-SERVICE
//  NGROK, L'INTERFACCIA HTTPS NON E' MAI STATA UTILIZZATA E NECESSITA DI ALCUNE AGGIUNTE
//  PER RENDERLA FUNZIONANTE(AD ES. INSERIRE LUNGHEZZA COME CONTENT-LENGTH NELL'HEADER)
//  APPARTE QUESTO LE FUNZIONI E LE INTERFACCE SONO COMPATIBILI

////

public class GoogleInterface{

    private static final MyLogger LOG = new MyLogger( GoogleInterface.class );

    private HttpsServer httpSecureInterface = null;
    private HttpServer  httpInterface = null;

    //  DATI DELL'APPLICAZIONE
    public static AuthServer authData;                        //  user's oauth2 tokens information
    public static GoogleConfiguration confData;            //  user's google jwt.json credentials information
    public static DeviceData deviceData;                    //  manager for user's devices
    public static LwM2mServer leshanServer;                 //  pointer to interact with the leshan lwm2m server
    public static final Object requestLock = new Object();  //  used for thread synchronization

    public GoogleInterface( LwM2mServer server ){

        ObjectMapper om = new ObjectMapper();
        Path pathCredenziali = Paths.get( "leshan-server-demo/src/main/java/googleInterface/google_configuration/conf.json" );
        //  file contenente le credenziali utilizzate per contattare Google

        leshanServer = server;
        new LeshanGoogleConverter();  //  classa statica da inizializzare

        try {

            confData = om.readValue(Files.readAllBytes(pathCredenziali), GoogleConfiguration.class );

        }catch( IOException ie ){

            LOG.error( "unable to load the google credentials configuration file\n" + ie.getMessage() );
            System.exit( -1 );

        }

        LOG.info( "server data configuration loaded from google_configuration/conf.json" );

        //  si caricano le informazioni salvate dell'utente
        authData = AuthServer.getInstance();  //  richiede che credentials.confInit() sia stato eseguito
        deviceData = new DeviceData( null , null );

        startInterface();

    }

    //  crea le interfacce e le pone in ascolto secondo il protocollo HTTP

    private void startInterface(){

        try {

            httpInterface = HttpServer.create( new InetSocketAddress( confData.getSmartHomeEndpoint() , confData.getSmartHomePort() ), 0 );
            LOG.info( "google interface created.." );

        }catch( IOException e ){

            LOG.error( "binding http server error\n" + e.getMessage() );
            System.exit( -1 );

        }

        httpInterface.createContext("/oauth", new GoogleMessageHandler() );
        httpInterface.createContext("/token", new GoogleMessageHandler() );
        httpInterface.createContext("/smarthome", new GoogleMessageHandler() );
        httpInterface.createContext( "/login" , new GoogleMessageHandler() );
        httpInterface.createContext( "/page_component" , new GoogleMessageHandler() );
        httpInterface.setExecutor( null ); // creates a default executor

        httpInterface.start();

        LOG.info( "google interface active at " + confData.getSmartHomeEndpoint() + ":" + confData.getSmartHomePort() );

    }

    //  crea le interfacce e le pone in ascolto secondo il protocollo HTTPS

    private void startSecureInterface(){

        try {

            httpSecureInterface = HttpsServer.create( new InetSocketAddress( confData.getSmartHomeEndpoint() , confData.getSmartHomeSecurePort() ) , 0 );
            SSLContext sslContext = SSLContext.getInstance( "TLS" );

            // si inizializza il keyStore
            char[] password = "keyStorePassword".toCharArray(); //  PASSWORD DEL KEYSTORE
            KeyStore keystore = KeyStore.getInstance( "JKS" );
            FileInputStream keystoreData = new FileInputStream("leshan-server-demo/src/main/java/googleInterface/https_keystore/KeyStore.jks" );  //  locazione del keystore
            keystore.load( keystoreData , password );
            keystoreData.close();

            // si inizializza un Key Manager Factory
            KeyManagerFactory keyFactory = KeyManagerFactory.getInstance( "SunX509" );
            keyFactory.init( keystore, password );

            // si inizializza un trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
            tmf.init( keystore );

            // si inizializzano i parametri e il context
            sslContext.init( keyFactory.getKeyManagers(), tmf.getTrustManagers(), null );
            httpSecureInterface.setHttpsConfigurator( new HttpsConfigurator( sslContext ) {

                public void configure( HttpsParameters params ) {
                    try {

                        // inizializza SSL context
                        SSLContext context = getSSLContext();
                        SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth( false );
                        params.setCipherSuites( engine.getEnabledCipherSuites() );
                        params.setProtocols( engine.getEnabledProtocols() );

                        // imposto i parametri SSL
                        SSLParameters sslParameters = context.getSupportedSSLParameters();
                        params.setSSLParameters( sslParameters );

                    }catch ( Exception e ) {

                        LOG.error( "Error trying to inizialize SSL socket " + e.getMessage() );
                        System.exit( -1 );

                    }
                }});

            httpSecureInterface.createContext("/oauth", new GoogleMessageHandler() );
            httpSecureInterface.createContext("/token", new GoogleMessageHandler() );
            httpSecureInterface.createContext("/smarthome", new GoogleMessageHandler() );
            httpSecureInterface.createContext( "/login" , new GoogleMessageHandler() );
            httpSecureInterface.createContext( "/page_component" , new GoogleMessageHandler() );
            httpSecureInterface.setExecutor( null ); // creates a default executor

            httpSecureInterface.start();

            LOG.info( "google interface active at " + confData.getSmartHomeEndpoint() + ":" + confData.getSmartHomeSecurePort() );

        } catch ( Exception e ) {

            LOG.error( "binding https server error\n" + e.getMessage() );
            System.exit( -1 );

        }

    }

    //  metodo per effettuare una richiesta RESYNC a Google
    public static void requestSync() {
        //  si verifica che il client sia registrato controllando se è presente un authtoken
        if( !GoogleInterface.authData.control() ){

            LOG.info( "abort resync request,  no clients registrated");
            return;

        }

        String message = "{\n\"agentUserId\": \"" + confData.getAgentUserId()+"\"\n}";

        try {
            //  creo l'URL a cui inviare la richiesta RESYNC utilizzando una chiave API per raggiungere il mio servizio
            URL url = new URL(confData.getRequestSyncEndpoint()+ confData.getGoogleAPIkey() );
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setDoOutput( true );
            connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Content-Length" , String.valueOf( message.length()));
            connection.setRequestProperty( "Content-Type" , "application/json" );

            OutputStream bodyMessage = connection.getOutputStream();

            bodyMessage.write( message.getBytes() );
            bodyMessage.close();

            //  Google ricevuto il messaggio effettuerà una richiesta SYNC all'interfaccia.
            //  Al termine dello scambio dei messaggi se andato a buon fine ci invierà 200(OK)

            if( connection.getResponseCode() != 200 )
                LOG.error( "google send an error message, resync not successfull execute" );
            else
                LOG.info( "resync request successfull performed" );

        }catch( IOException e ) {

            LOG.error( "error making resync request " + e.getMessage() );

        }
    }
}
