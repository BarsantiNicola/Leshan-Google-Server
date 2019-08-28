package googleInterface.data;

import googleInterface.GoogleInterface;
import googleInterface.MyLogger;
import java.time.LocalDateTime;
import java.util.Random;
import java.io.*;


  //  oggetto per gestire le credenziali di access delegation dell'utente definito in conf.json
  //  il refresh time e l'utente sono definiti in google_configuration/conf.json file

public class AuthCode implements Serializable {

    private static final MyLogger LOG = new MyLogger( AuthCode.class);

    private String authCode;           // Authentication Grant Code dell'utente
    private String authToken;
    private String refreshToken;
    private LocalDateTime expiresAt;  //  validità dell'authtoken


    //  genera un nuovo authtoken vuoto

    private AuthCode(){

        authToken = null;
        refreshToken = null;
        expiresAt = null;
        authCode = randomStringGen();         //  enabled to given the refresh_code

    }


    //  funzione da utilizzare per ottenere un istanza oAuth, se presente localmente la carica altrimenti inizializza
    //  un'istanza vuota

    public static AuthCode getInstance(){

        try {

            ObjectInputStream authInput = new ObjectInputStream(new FileInputStream("leshan-server-demo/src/main/java/googleInterface/bin/auth.bin"));
            AuthCode ret = (AuthCode) authInput.readObject();
            authInput.close();
            LOG.info( "correctly loaded authentication data from bin/auth.bin");
            return ret;

        }catch( IOException | ClassNotFoundException e ){
            LOG.info( "unable to find a valid authentication binary file, load default setting");
            return new AuthCode();  //  se non è presente un salvataggio creo un contenitore vuoto da inizializzare
        }

    }


    //  salva le credenziali dell'utente nel file binario auth.bin

    private void saveInstance(){

        try{

            ObjectOutputStream authOutput = new ObjectOutputStream(new FileOutputStream("leshan-server-demo/src/main/java/googleInterface/bin/auth.bin"));
            authOutput.writeObject( this );
            authOutput.close();
            LOG.info( "authentication information correctly saved");

        }catch( IOException e ){

            LOG.error("an error has occured while trying to save authentication information: " + e.getMessage());
            System.exit( -1 );

        }
    }


    //  effettua un refresh dell'authToken, ricreandolo e aumentandone la validità

    public void update(){

        expiresAt = LocalDateTime.now().plusSeconds(GoogleInterface.confData.getSmartHomeRefreshTime());
        authToken = randomStringGen();
        LOG.info( "authentication information updated" );
        saveInstance();


    }


    //  verifica la validità dell'authToken, si occupa lui stesso di eliminarlo in caso sia scaduto

    public boolean control( ){

        if( expiresAt != null && expiresAt.isBefore( LocalDateTime.now() )){

            authToken = null;
            expiresAt = null;
            saveInstance();

        }

        return expiresAt != null;

    }


    //  crea nuove credenziali di access delegation per l'utente

    public void createTokens(){

        authToken = randomStringGen();
        refreshToken = randomStringGen();
        expiresAt = LocalDateTime.now().plusSeconds( GoogleInterface.confData.getSmartHomeSessionTime());

        LOG.info( "generated new tokens for authentication");
    }


    //  elimina le credenziali dell'utente

    public void delete(){

        authToken = null;
        refreshToken = null;
        expiresAt = null;
        saveInstance();
        LOG.info( "authentication information deleted");

    }


    //  fornisce l'authToken dell'utente

    public String getAuthToken(){

        //  se nessun authtoken è presente se ne fornisce uno erroneo per far fallire
        //  l'accesso(non dovrebbe verificarsi un accesso senza aver fatto prima control, ma come
        //  sicurezza aggiuntiva). Non si fornisce una stringa vuota perchè potrebbe divenire un problema
        //  di sicurezza.

        if( !control() )
            return "noToken10192";

        return authToken;

    }


    //  l'authentication grant code è sempre presente

    public String getAuthCode(){
        return authCode;
    }


    //  fornisce il refresh_Token dell'utente

    public String getRefreshToken(){

        //  se nessun refreshtoken è presente se ne fornisce uno erroneo per far fallire
        //  l'accesso(non dovrebbe verificarsi un accesso senza aver fatto prima control, ma come
        //  sicurezza aggiuntiva). Non si fornisce una stringa vuota perchè potrebbe divenire un problema
        //  di sicurezza.

        if( !control() )
            return "noToken101923";

        return refreshToken;

    }


    //  genera una stringa randomica di 36 bytes da utilizzare come authtoken/refreshtoken/authorization grant code

    private String randomStringGen(){

        Random rnd = new Random();
        char[] arr = new char[ 36 ];

        for ( int i=0; i<36; i++ ) {
            int n = rnd.nextInt (36);
            arr[i] = (char) (n < 10 ? '0'+n : 'a'+n-10);
        }

        return new String(arr);
    }

}
