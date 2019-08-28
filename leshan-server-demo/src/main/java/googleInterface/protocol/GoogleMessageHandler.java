package googleInterface.protocol;

import java.io.*;
import java.util.*;
import java.net.URLDecoder;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import googleInterface.GoogleInterface;
import googleInterface.MyLogger;
import googleInterface.protocol.message.*;
import org.apache.http.Consts;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.nio.charset.StandardCharsets;


//  classe utilizzata per prelevare le informazioni dalle richieste ricevute e organizzandole
//  in modo funzionale per il programmatore. I messaggi vengono suddivisi per tipologia e le variabili
//  del messaggio mantenute per poter essere lette facilmente

public class GoogleMessageHandler implements HttpHandler {

    enum MessageType{ AUTH , TOKEN , SMART , LOGIN  , LOAD }  //  TIPO DI RICHIESTA
    enum SmartType{ SYNC , QUERY , EXECUTE , DISCONNECT , OTHER }  //  SOTTOTIPI DI SMART

    private static final MyLogger LOG = new MyLogger( GoogleMessageHandler.class);

    private HttpExchange httpExchangeMessage;  //  messaggio originale
    private String URI;                        //  URI destinataria
    private String body;                       //  contenuto del body del messaggio
    Message smartRequest;                      //  contenuto filtrato per il protocollo smarthome
    private String method;                     //  GET/POST
    private MessageType type;                  //  tipo della richiesta
    private SmartType smartType;               //  eventuale sottotipo della richiesta
    private HashMap<String , String > variablesMap;  //  mappa delle variabili del messaggio


    //  metodo eseguito alla ricezione di un messaggio
    //  il messaggio viene campionato in un tipo e le sue variabile estratte
    @Override
    public void handle( HttpExchange message ) {

        StringBuilder build = new StringBuilder();
        String uri;

        httpExchangeMessage = message;        //  si mantiene per operare sul messaggio di risposta
        variablesMap = new HashMap<>();
        method = httpExchangeMessage.getRequestMethod();  // GET o POST

        //  si preleva l'URI e il body decodificando la codifica WEB
        try {

            URI = URLDecoder.decode( message.getRequestURI().toString(), StandardCharsets.UTF_8.toString() );

        }catch( UnsupportedEncodingException ex ) {

            throw new RuntimeException( ex.getCause());

        }


        try ( InputStreamReader reader = new InputStreamReader( httpExchangeMessage.getRequestBody(), Consts.UTF_8 )) {

            char[] buffer = new char[256];
            int read;

            while (( read = reader.read(buffer)) != -1) build.append( buffer, 0, read );
            body = URLDecoder.decode( build.toString(), StandardCharsets.UTF_8.toString() );

        } catch ( IOException e ) {

            LOG.error( "an error was occured while reading the oauth body request " + e.getMessage() );
            return;

        }

        //  il messaggio viene campionato in un tipo

        if ( URI.indexOf( "/oauth" ) == 0 ) {

            uri = URI.replaceFirst("/oauth", "" );
            type = MessageType.AUTH;

        } else if ( URI.indexOf( "/token" ) == 0 ) {

            uri = URI.replaceFirst("/token", "" );
            type = MessageType.TOKEN;

        } else if ( URI.indexOf( "/smarthome" ) == 0 ) {

            uri = URI.replaceFirst("/smarthome", "" );
            type = MessageType.SMART;

        } else if ( URI.indexOf( "/login" ) == 0 ) {

            uri = URI.replaceFirst("/login", "" );
            type = MessageType.LOGIN;

        } else if ( URI.indexOf( "/page_component" ) == 0 ) {

            uri = URI.replaceFirst("/page_component", "" );
            type = MessageType.LOAD;

        } else {

            LOG.error( "error, the message request isn't valid" );
            return;

        }

        //  si estraggono le variabili dal messaggio

        if ( method.compareToIgnoreCase("post") == 0) {


            if( type == MessageType.SMART) {

                variableGSONExtract( body );
                //  si preleva anche il campo authorization dall'header
                variablesMap.put( "Authorization", message.getRequestHeaders().get( "Authorization" ).get(0).replaceFirst("Bearer ", "" ));

            }else
                variableURIExtract( body );  //  mantiene le variabili nel body ma con codifica URI(?VAR=ASD&..)(per il form del login)
        }else
            variableURIExtract( uri );

        //  gestione della richiesta inoltrandola al metodo appropriato
        //  blocco di sincronizzazione per avere la mutua esclusione tra le varie richiestwe

        synchronized( GoogleInterface.requestLock ){

            switch ( type ) {

                case AUTH:

                    LOG.info( "[OAUTH] Ricevuto richiesta: " + URI );
                    OAuthProtocol.handleAuthMessage( this );
                    LOG.info( "[OAUTH] Gestione richiesta completata" );
                    break;

                case TOKEN:

                    LOG.info( "[TOKEN] Ricevuto richiesta: " + URI );
                    OAuthProtocol.handleTokenMessage( this );
                    LOG.info( "[TOKEN] Gestione richiesta completata" );
                    break;

                case SMART:

                    LOG.info( "[SMARTHOME] Ricevuta richiesta " );
                    SmarthomeProtocol.handleSmarthomeMessage( this );
                    LOG.info( "[SMARTHOME] Gestione richiesta completata" );
                    break;

                case LOGIN:

                    LOG.info( "[LOGIN] Ricevuto richiesta: " + URI );
                    OAuthProtocol.handleLoginMessage( this );
                    LOG.info( "[LOGIN] Gestione richiesta completata" );
                    break;

                case LOAD:

                    LOG.info( "[LOAD] Ricevuto richiesta: " + URI );
                    OAuthProtocol.handleLoadMessage( this );
                    LOG.info( "[LOAD] Gestione richiesta completata" );
                    break;

                default:
                    LOG.info( "[ERROR]Ricevuta richiesta non gestita dall'interfaccia: " + message.getRequestURI() );

            }

        }
    }

    //  metodo per estrarre le variabili in codifica URI  ( uri?var=value&var2=value2.. )
    private void variableURIExtract( String container ){

        if ( container.length() == 0 ) return;

        container = container.replace("?", "" );

        String[] variables = container.split("&");
        String[] nameValue;

        for ( String variable : variables )
            if( variable.contains( "=" ) ) {

                nameValue = variable.split( "=" );
                variablesMap.put(nameValue[0], nameValue[1]);

            }else
                variablesMap.put( variable , "" );


    }

    //  estrae variabili mantenute in formato GSON e le inserisce nella variablesMap
    private void variableGSONExtract( String container ) {

        JSONObject gsonMessage;
        Iterator<Map.Entry> variables;
        Iterator inputs;

        try {

            gsonMessage = (JSONObject) new JSONParser().parse( container );

        } catch ( ParseException e ) {

            LOG.error( "error whle parsing smarthome request: " + e.getMessage() );
            return;

        }

        variablesMap.put( "requestId" , (String)gsonMessage.get( "requestId" ));

        //  search the smart message variables in the intent field

        //   message{
        //          requestId: ..
        //          inputs:{
        //                  intent:{
        //                          variables....
        //   }}}

        inputs = ((JSONArray)gsonMessage.get( "inputs" )).iterator();
        while ( inputs.hasNext() ) {

            variables = ((Map)inputs.next()).entrySet().iterator();

            while ( variables.hasNext() ) {

                Map.Entry pair = variables.next();
                if ( ((String) pair.getKey()).compareToIgnoreCase( "intent" ) == 0 )
                    variablesMap.put( "intent", (String) pair.getValue() );

            }
        }

        //  il tipo secondario del messaggio è definito dal campo intent
        Gson gson = new Gson();
        String intent = variablesMap.get( "intent" ).replace("action.devices." , "" );

        if ( intent.compareToIgnoreCase( "SYNC" ) == 0 ){

            smartType = SmartType.SYNC;
            smartRequest = gson.fromJson( container, SyncRequest.class );
            return;
        }

        if ( intent.compareToIgnoreCase( "QUERY" ) == 0 ) {

            smartType = SmartType.QUERY;
            smartRequest = gson.fromJson( container, QueryRequest.class );
            return;

        }

        if ( intent.compareToIgnoreCase( "EXECUTE" ) == 0 ) {

            smartType = SmartType.EXECUTE;
            smartRequest = gson.fromJson( container, ExecRequest.class );
            return;
        }

        if ( intent.compareToIgnoreCase( "DISCONNECT" ) == 0 ) {

            smartType = SmartType.DISCONNECT;
            smartRequest = null;
            return;
        }

        smartType = SmartType.OTHER;
        smartRequest = null;

    }


    @Override
    public String toString(){

        String ret = "Method: " + method +"\nType: " + type + ":" + smartType +"\nBody: " + body+"\nHeaders:";
        Set<String> attributi = httpExchangeMessage.getRequestHeaders().keySet();

        for( String attr : attributi ){
            for( String attr2: httpExchangeMessage.getRequestHeaders().get(attr))
                ret +="\n\t[" + attr + "]  Value: " + attr2;
        }

        ret += "\nVariables: ";
        for( String index : variablesMap.keySet()){
            ret += "\n\t[" + index + "]:  " + variablesMap.get( index );
        }

        return ret;
    }


    //  metodo per conoscere la presenza della variabile "id" nel messaggio

    private boolean isPresent( String id ){ return variablesMap.containsKey( id ) && ( variablesMap.get( id ).length()!=0 ); }


    //  metodo per prelevare la variabile "key" dal massaggio

    String getVariable( String key ){

        if( isPresent( key ) )
            return variablesMap.get( key );
        else
            return "";

    }


    //  funzione di utilità per definire lo status della risposta e la sua lunghezza nel messaggio HTTP
    //  di risposta

    void sendResponseHeaders( int rCode , int length ) {

        try {
            httpExchangeMessage.sendResponseHeaders(rCode, length);
        } catch (IOException e) {
            LOG.error("an error has occured while trying to send response " + e.getMessage());
            System.exit(-1);
        }
    }

    Headers getResponseHeaders(){ return httpExchangeMessage.getResponseHeaders(); }

    OutputStream getResponseBody(){ return httpExchangeMessage.getResponseBody(); }

    String getMethod(){ return method; }

    SmartType getSmartType(){ return smartType; }

    String getRequestURI(){ return URI; }

}
