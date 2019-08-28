package googleInterface.integration;

import googleInterface.MyLogger;
import googleInterface.integration.actions.Trait;
import googleInterface.protocol.message.*;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.response.ObserveResponse;
import java.io.Serializable;
import java.util.HashMap;



// classe per il mantenimento di un dispositivo, collega le interfaccia Google a quella Leshan,
// Google accede alla classe per richieste di SYNC,QUERY,EXECUTE.
// Leshan comunica alla classe gli aggiornamenti delle informazioni relative al protocollo Observe/Notify

public class Device implements Serializable {

    private static final MyLogger LOG = new MyLogger( Device.class);

    final String id;                    //  Identificatore univoco del dispositivo
    private boolean isAlive;            //  ONLINE/OFFLINE
    private final LeshanDevice device;  //  permette di interagire con il dispositivo attraverso Leshan
    private Trait[] traits;             //  actions del dispositivo
    private HashMap<String, Trait> searchTraits;    //  associazioni comandi/Trait per EXECUTE
    private HashMap<String, Trait> observeTraits;   //  associazione RisorseLeshan/Trait per Observe
    private SyncDevice syncInformation = null;     //  risposta a SYNC precompilata


    public Device( Device dev ){

        this( dev.device );

    }

    public Device( LeshanDevice dev ){

        //  è già stata verificata l'instanziabilità del dispositivo
        //  le risorse necessarie vengono esamite nel costruttore di LeshanDevice

        device = dev ;
        id = device.getId();
        searchTraits = new HashMap<>();
        observeTraits = new HashMap<>();
        isAlive = true;

        String[] actions;
        LeshanResource[] leshanObjects;

        traits = LeshanGoogleConverter.getTraitsClass( device ); //  si ottengono le actions del dispositivo

        //  realizzazione dei collegamenti per i meccanismi di observe/notify e esecuzione dei comandi

        //  per observe/notify, ogni notify è associata a una risorsa
        //  attraverso un HashMap si realizza un associazione tra le risorse ed i Trait che le utilizzano
        //  arrivando una notify al dispositivo, si valuta il trait che usa la risorsa e gli si inoltra
        //  per le action funziona nello stesso modo, ogni trait associa i comandi che può ricevere,
        //  alla ricezione di un comando al dispositivo si valuta quale trait implementa il comando e gli si inoltra

        for( Trait t:traits ) {

            actions = LeshanGoogleConverter.getCommands( t.getTrait() );
            leshanObjects = t.getObjectsID();
            for( String action: actions )
                searchTraits.put( action, t );
            for( LeshanResource lObj: leshanObjects )
                observeTraits.put( lObj.toString() , t );

        }

        //  per importare il meccanismo observe/notify anche alle informazioni non associate a nessun Trait
        //  ma legate al dispositivo(le informazioni di SYNC)
        //  inserendo null, le notify vengono ridirette verso LeshanDevice che mantiene la cache delle risorse
        //  utilizzate dalla SYNC

        leshanObjects = device.getObjectsID();
        for( LeshanResource lObj: leshanObjects )
            observeTraits.put( lObj.toString() , null );

        device.startObservation();  //  si avvia la osservazione delle risorse per la SYNC

        LOG.info( "new device for google interface created");

    }


    //// COMUNICAZIONE CON GOOGLE

    //  fornisce le informazioni per le richieste SYNC,
    //  la risposta è già formattata e basta inserirla nel campo devices della SyncResponse

    SyncDevice getSyncInformation(){

        if( !isAlive ) return syncInformation;  //  se il device è offline si invia l'ultima copia creata

        syncInformation = new SyncDevice();
        String[] t = new String[ traits.length ];
        String[] myName = new String[1];
        myName[0] = "My Lamp";//device.getRegistration().getEndpoint();
        DeviceNames names = new DeviceNames( "LESHAN SMART " + device.getType() , null , myName );

        for( int a = 0; a<traits.length;a++)
            t[a] = traits[a].getTrait();

        syncInformation.setId( id );
        syncInformation.setType( "action.devices.types." + device.getType() );
        syncInformation.setDeviceInfo( device.getDeviceInfo() );
        syncInformation.setName( names );
        syncInformation.setTraits( t );
        syncInformation.setAttributes( getAttributes() );

        LOG.info( "rebuild sync information");

        return syncInformation;

    }


    //  preleva gli attributi, informazioni aggiuntive deI Trait da inserire nelle risposte SYNC

    public HashMap<String , Object> getAttributes(){

        HashMap<String , Object> ret = new HashMap<>();

        for( Trait trait : traits )
            ret.putAll( trait.getAttribute());

        return ret;

    }


    //  fornisce le informazioni disponibili nel dispositivo per le richieste QUERY,

    HashMap< String , Object > getQueryInformation(){

        HashMap<String, Object> ret = new HashMap<>();
        ret.put( "online" , isAlive );
        //  si scorrono tutti i trait e si concatenano le loro risposte
        for( Trait trait: traits )
            ret.putAll( trait.queryInformation() );

        LOG.info( "build query information" );
        return ret;

    }


    //  metodo per impartire un comando a un trait del dispositivo

    ResponseCommand getExecution( ExecuteGoogleRequest command ){

        ResponseCommand response = new ResponseCommand( "SUCCESS" , id );

        if( !isAlive ) return new ResponseCommand("OFFLINE", id );

        if( searchTraits.containsKey( command.getCommand() ))  //  si cerca il trait destinatario del comando

            //  se il comando va a buon fine si ccaricano i trait interessati e si prelevano gli status
            if( searchTraits.get( command.getCommand() ).handleRequest( command.getCommand() , command.getParams()))
                response.getStates().putAll( searchTraits.get( command.getCommand()).queryInformation() );
            else
                return new ResponseCommand( id ,  "ERROR");

        return response;
    }

    ////  METODI UTILIZZATI DA LESHAN PER AGGIORNARE I DISPOSITIVI


    //  fornisce alla classe gli aggiornamenti delle funzioni a seguito di una observe
    public void putObservation( ObserveResponse message ){
        LeshanResource resource = new LeshanResource( message.getObservation().getPath().toString() );

        //  observeTraits mantiene le associazioni tra le risorse osservate e i trait che le hanno richieste
        if( observeTraits.containsKey( resource.toString())) {
            Trait t = observeTraits.get(resource.toString());
            if (t == null)
                device.putObservation(resource, ((LwM2mSingleResource) message.getContent()).getValue());
            else
                t.putObservation(resource, ((LwM2mSingleResource) message.getContent()).getValue());
        }
    }
    ////  METODI DI UTILITA'

    //  necessario a DeviceData per reinserire il server dopo aver prelevato i device salvati

   /* void setServer( LwM2mServer server ){
        device.setServer( server );
    }*/

    //  necessario a DeviceData per aggiornare i dispositivi
    void setOffline(){ isAlive = false; }


}
