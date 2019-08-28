package googleInterface.protocol;

import googleInterface.MyLogger;
import org.json.simple.JSONObject;
import googleInterface.GoogleInterface;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

//  la classe si occupa di gestire le credenziali utente secondo il protocollo oAuth2
//  i suoi compiti principali sono permettere l'autenticazione di un utente al servizio
//  tramite una pagina di login e creare,mantenere e aggiornare le sue credenziali di access delegation

class OAuthProtocol {

    private static final MyLogger LOG = new MyLogger( OAuthProtocol.class);

    //  metodo eseguito alla ricezione di una richiesta di autenticazione utente da Google
    static void handleAuthMessage( GoogleMessageHandler message ){

        //  nel messaggio di richiesta DEVE essere presente la variabile "response_type=code"
        //  altrimenti Google è impostato per utilizzare un metodo di autenticazione differente
        if ( message.getVariable( "response_type" ).compareToIgnoreCase( "code" ) != 0 ) {

            LOG.error( "error, the response_type of the request must be 'code'" );
            sendError( message , 0 );  //  ERRORE 500  response_type!=code
            return;

        }

        //  e' presente un unico utente e il suo client_id è mantenuto in conf.json
        //  solo quell'utente può accedere ai servizi
        if ( GoogleInterface.confData.getGoogleClientId().compareTo( message.getVariable( "client_id" )) != 0 ){

            LOG.error( "error, the client_id isn't valid" );
            sendError( message , 1 );  //  ERRORE 500  client id non riconosciuto
            return;

        }

        ////  DA FARE
        //  per semplificare non ho implementato le "sessioni" utente ( simili all'authtoken hanno una validità temporale
        //  limitata e permettono all'utente l'autenticazione senza la necessità di rieffettuare un login)
        //  ne la possibilità di saltare il login se un authtoken è già presente per l'utente.
        //  Al momento l'utente se scollega dal servizio DEVE SEMPRE rieffettuare un login, viene comunque
        //  mantenuta la possibilità di login multipli(l'accedere non "distrugge" le credenziali sostituendole
        //  con delle nuove altrimenti nel caso si utilizzino più applicazioni, registrare la successiva impedirebbe
        //  alle applicazione precedenti di accedere, avendo invalidato il loro authtoken)
        ////

        LOG.info("redirection of the user " + message.getVariable("client_id" ) + " to the authentication page");
        sendRedirect( message , getLoginRedirect( message.getVariable( "redirect_uri" ) , message.getVariable( "state" ) , null ));

    }

    //  metodo per gestire la pagina di login utente
    static void handleLoginMessage( GoogleMessageHandler message ){

        //  nel caso GET l'utente vuole ottenere la pagina html di login
        //  GET /login.html
        if( message.getMethod().compareToIgnoreCase( "get" ) == 0 ){

            LOG.info( "a user request the login page" );
            try{

                OutputStream messageBody = message.getResponseBody();

                byte[] byteResponse = Files.readAllBytes((Paths.get( "leshan-server-demo/src/main/java/googleInterface/http_data/login.html" )));
                message.sendResponseHeaders( 200 , byteResponse.length );

                messageBody.write( byteResponse );
                messageBody.close();


            }catch ( IOException e ){

                LOG.error( "html login page not found: " + e.getMessage() );
                sendError( message , 2 );

            }

            return;

        }

        //  altrimenti la richiesta e' POST e corrisponde all'invio delle credenziali immesse nel form di login

        //  se le credenziali sono valide si fornisce l'Authentication Grant Code per richiedere i tokens
        if( (message.getVariable( "username" ).compareTo( GoogleInterface.confData.getServiceUsername()) == 0 &&
                message.getVariable( "password" ).compareTo( GoogleInterface.confData.getServicePassword()) == 0 )){

            LOG.info( "Authentication valid, the user is authenticated" );
            //  se non sono presenti i token li creiamo altrimenti non li distruggiamo, invalideremmo
            //  precedenti autenticazioni
            if( !GoogleInterface.authData.control())
                GoogleInterface.authData.createTokens();

            sendRedirect( message , getAuthTokenGETFormatResponse( message.getVariable( "redirect_uri" ) , message.getVariable( "state" )));

        }else{

            //  se le credenziali non sono valide ridirigiamo l'utente alla pagina di login
            LOG.error( "Authentication invalid, the user will be resend to the login page" );
            sendRedirect( message , getLoginRedirect( message.getVariable( "redirect_uri" ) , message.getVariable( "state" ) , message.getVariable( "client_id" ) ));

        }

    }


    //  metodo per gestire lo scambio dei tokens del server oAuth2
    static void handleTokenMessage( GoogleMessageHandler message ){

        String grant_type = message.getVariable( "grant_type" );  //  tipo di richiesta( authcode o refreshcode)
        String code = message.getVariable( "code" );  //  se authcode mantiene l'authorization grant code
        String refreshCode = message.getVariable( "refresh_token" );  //  se refreshcode mantiene il refreshtoken
        String response;

        if( (grant_type.compareToIgnoreCase( "authorization_code" ) == 0 && code.compareTo( GoogleInterface.authData.getAuthCode() ) == 0 )||
                (grant_type.compareToIgnoreCase( "refresh_token" )) == 0 && refreshCode.compareTo( GoogleInterface.authData.getRefreshToken() ) == 0 ){

            try {

                OutputStream messageBody = message.getResponseBody();
                //  se abbiamo ricevuto un refreshToken creiamo nuove credenziali e aggiornaimo la scadenza
                //  indietro forniamo solo l'authtoken per una questione di sicurezza
                if( grant_type.compareToIgnoreCase( "refresh_token" ) == 0 ) {

                    LOG.info( "refresh of the tokens");
                    GoogleInterface.authData.update();

                    response = getRefreshTokenPOSTFormatResponse();


                }else {

                    //  se abbiamo ricevuto l'authorization grant code creiamo nuove credenziali e aggiorniamo
                    //  la scadenza, in dietro forniamo l'authtoken e il refreshtoken

                    LOG.info("confirmation of the tokens, send refresh_token for user tokens refresh");
                    GoogleInterface.authData.update();
                    response = getAuthTokenPOSTFormatResponse();
                }


                message.getResponseHeaders().add( "Content-Type" , "application/json" );
                message.sendResponseHeaders( 200 , response.length() );

                messageBody.write( response.getBytes() );
                messageBody.close();

            }catch( IOException e ){

                LOG.error( "Error in the manage of the request token " + e.getMessage() );

            }

            return;

        }

        LOG.error( "Error grant type not supported: " + grant_type );
        System.out.println( GoogleInterface.authData.getRefreshToken() + " : " + code );
        sendError( message , 2 );  //  errore grant_type non supportato

    }

    //  metodo per caricare componenti aggiuntive nella pagina di login(immagini, file js etc)
    static void handleLoadMessage( GoogleMessageHandler message ){

        try {

            OutputStream messageBody = message.getResponseBody();

            byte[] byteResponse = Files.readAllBytes(( Paths.get( "leshan-server-demo/src/main/java/googleInterface/http_data" + message.getRequestURI() )));
            message.sendResponseHeaders( 200 , byteResponse.length );

            messageBody.write( byteResponse );
            messageBody.close();

        }catch( IOException e ){

            LOG.error( "[LOAD]html page resource http_data " + message.getRequestURI() +" not found\n" + e.getMessage() );
            sendError( message ,3 );

        }
    }

    //  gestione degli errori generati dal protocollo oAuth2
    private static void sendError( GoogleMessageHandler message , int tipo ){

        OutputStream os = message.getResponseBody();
        String response;

        switch( tipo ) {

            case 0:

                response = "response_type  " + message.getVariable("response_type" ) + " must be equal to 'code'";
                message.sendResponseHeaders( 500 , response.length() );
                break;

            case 1:

                response = " invalid client_id " + message.getVariable("client_id" );
                message.sendResponseHeaders( 500, response.length() );
                break;

            case 2:

                response= "grant_type " + message.getVariable("grant_type" ) + " not supported";
                message.sendResponseHeaders( 400 , response.length() );
                break;

            case 3:

                response= "resource " + message.getVariable("redirect_uri" ) + " not found";
                message.sendResponseHeaders( 404 , response.length() );
                break;

            case 4:

                response= "invalid authtoken request";
                message.sendResponseHeaders( 400 , response.length() );
                break;

            default:

                response="bad format error";
                message.sendResponseHeaders( 500 , response.length() );

        }

        try {

            os.write(response.getBytes());
            os.close();

        }catch( IOException e ){

            LOG.error("an error has occured during management of an error response " + e.getMessage());
            System.exit( -1 );

        }

    }


    ////  FUNZIONI DI FORMATTAZIONE DEI MESSAGGI

    //  USATA DALL'INTERFACCIA DI LOGIN PER FORNIRE L'AUTHENTICATION GRANT CODE
    private static String getAuthTokenGETFormatResponse( String redirect ,  String state ){

        String authcode = GoogleInterface.authData.getAuthCode();

        if( redirect.length() != 0 || state.length() != 0)
            return redirect + "?code=" + authcode +"&state=" + state;

        return "";

    }

    //  USATA DALL'INTERFACCIA TOKEN PER FORNIRE AUTHTOKEN E REFRESHTOKEN

    private static String getAuthTokenPOSTFormatResponse(){

        JSONObject auth = new JSONObject();

        auth.put( "token_type" , "bearer" );
        auth.put( "access_token" , GoogleInterface.authData.getAuthToken() );
        auth.put( "refresh_token" , GoogleInterface.authData.getRefreshToken() );
        auth.put( "expires_in" , GoogleInterface.confData.getSmartHomeRefreshTime() );

        return auth.toJSONString();

    }

    //  USATA DALL'INTERFACCIA TOKEN PER FORNIRE AUTHTOKEN

    private static String getRefreshTokenPOSTFormatResponse(){

        JSONObject obj = new JSONObject();

        obj.put( "token_type" , "bearer" );
        obj.put( "access_token" , GoogleInterface.authData.getAuthToken() );
        obj.put( "expires_in" , GoogleInterface.confData.getSmartHomeRefreshTime() );

        return obj.toJSONString();

    }

    //  USATA DA LOGIN PER CREARE UNA REDIREZIONE ALLA PAGINA LOGIN
    private static String getLoginRedirect( String redirect , String state , String client ){

        if( client != null )  //  REDIREZIONE DA FORNIRE ALL'UTENTE IN CASO DI LOGIN ERRATO
            return "/login?error=true&client_id=" + client + "&redirect_uri=" + redirect + "&redirect=/oauth&state=" + state;

        return "/login?" + "redirect_uri=" + redirect + "&state=" + state;  //  URI DA FORNIRE A GOOGLE

    }


    //  metodo per redirigere l'utente su una nuova pagina
    private static void sendRedirect( GoogleMessageHandler message , String newPage ) {

        try {

            message.getResponseHeaders().add( "Location" , newPage );
            message.sendResponseHeaders(303 , 0 );
            message.getResponseBody().close();

        }catch( IOException e ){

            LOG.error("an error has occured during the redirection of the user: " + e.getMessage() );

        }


    }



}





