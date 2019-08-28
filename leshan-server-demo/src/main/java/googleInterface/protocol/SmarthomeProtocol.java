package googleInterface.protocol;

import com.google.gson.Gson;
import googleInterface.GoogleInterface;
import googleInterface.MyLogger;
import googleInterface.protocol.message.*;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


// classe per implementare metodi statici per gestire le richieste smart al servizio
//  la classe si occupa di gestire le richieste di SYNC, RESYNC, QUERY, EXECUTE E DISCONNECT

class SmarthomeProtocol {

    private static final MyLogger LOG = new MyLogger( SmarthomeProtocol.class );


    // eseguito ad ogni richiesta smart, verifica la validità dell'authToken e inoltra
    // la richiesta al metodo appropriato
    static void handleSmarthomeMessage( GoogleMessageHandler message ) {

        ////  verifica autenticazione

          //  authToken presente e valido
        if ( !GoogleInterface.authData.control() ) {

            LOG.info( "the given authtoken for the request is invalid" );
            sendError( message , 2 );
            return;

        }

          //  l'utente non ha fornito authtoken
        if( message.getVariable(  "Authorization" ).length() == 0 ){

            LOG.info( "[WARNING]unauthorized attempt to access without an authtoken" );
            sendError( message , 1 );
            return;

        }

          //  verifica dell'authToken
        if( message.getVariable( "Authorization" ).compareTo(GoogleInterface.authData.getAuthToken()) != 0) {

            LOG.info( "[WARNING] UNAUTHORIZED ATTEMPT TO ACCESS WITH AN INVALID AUTHTOKEN" );
            sendError( message ,0 );
            return;

        }

        //  si inoltra la richiesta al metodo appropriato
        switch( message.getSmartType() ){

            case SYNC:

                LOG.info( "Synchronization request received" );
                handleSync( message );
                return;

            case QUERY:

                LOG.info( "Query request received" );
                handleQuery ( message );
                return;

            case EXECUTE:

                LOG.info( "Exec request received" );
                handleExec( message );
                return;

            case DISCONNECT:

                LOG.info( "Disconnection request received" );
                handleExit( message );
                return;

            default:

                LOG.info( "Unable to manage the request, the request " + message.getSmartType() + " is unknown" );

        }

    }


    //  metodo per gestire le richieste di SYNC

    private static void handleSync( GoogleMessageHandler message ) {

        //  si crea il messaggio di risposta che contiene già impostato il requestId
        SyncResponse response = ( (SyncRequest)message.smartRequest ).getResponseMessage();

        OutputStream messageBody = message.getResponseBody();
        String gsonResponse;
        Gson gson = new Gson();

        //  si inserisce la risposta per la richiesta di SYNC
        response.getPayload().setDevices( GoogleInterface.deviceData.getSyncDevices());

        gsonResponse = gson.toJson( response );
        message.sendResponseHeaders(200 , gsonResponse.length() );

        try {

            messageBody.write( gsonResponse.getBytes() );
            messageBody.close();
            LOG.info( "sended sync response:\n" + gsonResponse );

        } catch (IOException e) {

            LOG.error("an error has occured while sending sync response " + e.getMessage() );
            return;

        }

        //  se sono presenti zero dispositivi registrati, Google si scollega dal servizio
        //  dobbiamo quindi impedire ulteriori accessi invalidando l'autenticazione
        if( GoogleInterface.deviceData.numDevices() == 0 ) {

            LOG.info( "Forcing disconnection from google from zero device synchronization");
            GoogleInterface.authData.delete();

        }

    }


    //  metodo per gestire le richieste di QUERY

    private static void handleQuery( GoogleMessageHandler message ) {

        OutputStream bodyMessage = message.getResponseBody();

        QueryResponse response = ( (QueryRequest)message.smartRequest ).getResponseMessage();
        QueryRequestInput[] queryInputs = ( (QueryRequest)message.smartRequest ).getInput();

        List<String> targetIds = new ArrayList<>();
        Gson gson = new Gson();
        String gsonResponse;
        QueryDevice[] targetDevices;

        //  prelevo tutti dispositivi target della richieste
        for( QueryRequestInput queryRequest: queryInputs ){  //  possono essere presenti più richieste impilate
            targetDevices = queryRequest.getPayload().getDevices();  // ogni richiesta può avere molti target
            for( QueryDevice targetDevice: targetDevices )
                targetIds.add( targetDevice.getId() );
        }

        //  prelevo le risposte dei dispositivi target
        response.getPayload().setDevices( GoogleInterface.deviceData.getQueryDevices( targetIds ));
        gsonResponse = gson.toJson( response );

        message.sendResponseHeaders(200 , gsonResponse.length() );

        try{

            bodyMessage.write( gsonResponse.getBytes() );
            bodyMessage.close();
            LOG.info( "query response sended:\n" + gsonResponse );

        }catch( IOException e ) {

            LOG.error("an error has occured while sending the query response" + e.getMessage());
            return;

        }
    }


    //  metodo per gestire le richieste di EXECUTE

    private static void handleExec(GoogleMessageHandler message) {

        OutputStream bodyMessage = message.getResponseBody();
        ResponseCommand[] responseComm;
        List<ResponseCommand> list = new ArrayList<>();
        Iterator<ResponseCommand> it;

        ExecResponse response = ((ExecRequest)message.smartRequest).getResponseMessage();
        ExecRequestInput[] execRequests = ((ExecRequest) message.smartRequest).getInputs();
        ExecRequestCommand[] commands;
        Gson gson = new Gson();
        String gsonResponse;
        ExecDevice[] devices;
        ExecuteGoogleRequest[] requestedCommands;

        //  gestisco singolarmente le richieste di esecuzione
        for( ExecRequestInput request: execRequests ){  //  esamino tutte le richieste
            commands = request.getPayload().getCommands();  //  prelevo la lista di comandi della richiesta

            // ogni comando continene un insieme di azioni e di dispositivi
            for( ExecRequestCommand command: commands ) {
                devices = command.getDevices();  //  prelevo i dispositivi interessati
                requestedCommands = command.getExecution();  // prelevo le azioni
                for( ExecuteGoogleRequest singleCommand: requestedCommands ) //  eseguo le azioni sui dispositivi
                    for ( ExecDevice device: devices )
                        list.add( GoogleInterface.deviceData.getExecution( device.getId() , singleCommand ));
            }

        }

        //  per semplificare tutte le risposte le ho listate,
        //  per ottimizzare è possibile compattarle in un numero minore di messaggi
        //  (ad esempio 10 messaggi di errore di 10 dispositivi, potrebbero essere compattati
        //  in un unico messaggio di errore che contiene la lista dei 10 dispositivi)

        it = list.iterator();
        responseComm = new ResponseCommand[ list.size() ];
        for( int a = 0; it.hasNext(); a++ )
            responseComm[a] = it.next();

        response.getPayload().setCommands( responseComm );
        gsonResponse = gson.toJson( response );
        message.sendResponseHeaders(200 , gsonResponse.length() );

        try {

            bodyMessage.write( gsonResponse.getBytes() );
            bodyMessage.close();
            LOG.info( "sended exec response:\n" + gsonResponse );

        } catch ( IOException e ) {

            LOG.info( "an error has occured while sending the exec response message\n" + e.getMessage() );
            return;

        }

    }


    //  metodo per gestire le richieste di disconnessione

    private static void handleExit( GoogleMessageHandler message ) {

        OutputStream messageBody = message.getResponseBody();

        String response = "{ \"requestId\": \"" + message.getVariable( "requestId" ) +"\" }";
        GoogleInterface.authData.delete();  //  elimino l'autenticazione dell'utente
        message.sendResponseHeaders(200, response.length());  // dobbiamo solo inviare 200(OK)

        try {

            messageBody.write( response.getBytes() );
            messageBody.close();

        } catch (IOException e) {

            LOG.error("an error has occured while sending the disconnection request's response");

        }
    }


    //  metodo per centralizzare la gestione degli errori

    private static void sendError( GoogleMessageHandler message , int type ) {

        OutputStream messageBody = message.getResponseBody();
        String response;

        switch( type ) {

            case 0:

                response = "invalid auth  " + message.getVariable( "Authorization" );
                message.sendResponseHeaders(403 , response.length() );
                break;
            case 1:
                response = "missing auth headers";
                message.sendResponseHeaders( 401 , response.length() );
                break;

            case 2:
                response = "Token " + message.getVariable( "Authorization" ) + " expired";
                message.sendResponseHeaders( 401 , response.length() );
                break;
            default:
                response = "bad request format";

        }

        try{

            messageBody.write( response.getBytes() );
            messageBody.close();

        }catch( IOException e ) {

            LOG.info( "an error has occured while managing an error response: " + e.getMessage() );

        }

    }

}
