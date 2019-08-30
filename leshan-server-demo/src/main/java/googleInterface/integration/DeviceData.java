package googleInterface.integration;

import googleInterface.GoogleInterface;
import googleInterface.MyLogger;
import googleInterface.protocol.message.ExecuteGoogleRequest;
import googleInterface.protocol.message.ResponseCommand;
import googleInterface.protocol.message.SyncDevice;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.registration.Registration;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


//  archivio dei dispositivi.
//  classe per ottenere le informazioni dei device da inserire nelle risposta a google.
//  google richiede che le informazioni dei device registrati siano sempre disponibili anche quando i device
//  non sono attivi, tuttavia leshan non mantiene informazioni localmente, è quindi necessario salvare una copia dei dati

public class DeviceData{

    private static final MyLogger LOG = new MyLogger( DeviceData.class);

    private String agentUserId;
    private String errorCode;         //  OPZIONALE  indica situazioni di errore nel device
    private String debugString;       //  OPZIONALE  indica informazioni per il debug del device
    private static HashMap<String,Device> deviceData;   //  mantiene le informazioni dei device
    private static HashMap<String , String> registratedDevices;  //  associazione deviceID/registrationID dei device connessi


    // crea i propri archivi e vi deposita tutti i device salvati in locale

    public DeviceData( String err , String debug  ){

        Device savedDevice;

        deviceData = new HashMap<>();
        errorCode = err;
        debugString = debug;

        agentUserId = GoogleInterface.confData.getAgentUserId();  //  ID associato all'utente
        registratedDevices = new HashMap<>();

        try{

            ObjectInputStream savedDeviceData = new ObjectInputStream( new FileInputStream("leshan-server-demo/src/main/java/googleInterface/savedData/device.bin"));

            int numDevices = savedDeviceData.readInt();  //  number of saved devices
            for( int a = 0; a<numDevices; a++ ) {

                savedDevice = (Device)savedDeviceData.readObject();
              //  savedDevice.setServer( GoogleInterface.leshanServer ); //  necessita di un istanza del server istanziato
                savedDevice.setOffline();  //  all'avvio del server li settiamo offline, quando si registreranno torneranno online
                deviceData.put( savedDevice.id , savedDevice );
                LOG.info( "load device from /savedData/devices.bin ---> " + savedDevice.id );

            }

            savedDeviceData.close();

        }catch( IOException | ClassNotFoundException e ) {

            LOG.error("an error has occured trying to build serialized devices " + e.getMessage());

        }

    }


    //  UTILIZZATA DA INTERFACCIA GOOGLE
    //  fornisce la lista delle informazioni dei device da inserire nella risposta a SYNC
    //  ogni classe device mantiene le informazioni necessarie, la funzione scorre tutti i device
    //  e preleva la loro risposta a richiesta sync

    public SyncDevice[] getSyncDevices(){

        SyncDevice[] devicesResponse = new SyncDevice[ deviceData.size() ];
        Iterator<Device> deviceIterator = deviceData.values().iterator();

        for( int a = 0; deviceIterator.hasNext(); a++ )
            devicesResponse[a] = deviceIterator.next().getSyncInformation();

        LOG.info( "Sync information response builded");
        return devicesResponse;

    }


    //  UTILIZZATA DA INTERFACCIA GOOGLE
    //  fornisce le informazioni dei device richiesti da fornire in risposta a QUERY
    //  ogni classe device mantiene una classe per ogni action abilitata,
    //  ogni device si occuperà in maniera trasparente di richiede a ogni sua action
    //  la risposta da fornire a query

    public  synchronized HashMap<String , Object> getQueryDevices( List<String> device ){

        HashMap<String , Object> ret = new HashMap<>();

        for( String id: device )  //  fornisco le informazioni solo dei device richiesti
            if( deviceData.containsKey( id ))
                ret.put( id , deviceData.get( id ).getQueryInformation());

        LOG.info( "query information response builded");
        return ret;

    }


    //  FUNZIONE UTILIZZATA DAL SERVER LESHAN QUANDO UN NUOVO DEVICE SI REGISTRA

    public synchronized void addDevice( Registration registration ){

        //  se un device è già registrato deve possedere una chiave in registratedDevices
        if( registratedDevices.containsKey( registration.getId())) {
            LOG.error( "error, an already added device is trying to be register");
            return;

        }

        try {
            //  LeshanDevice si occupa della comunicazione con il server leshan

            LeshanDevice newDev = new LeshanDevice( registration, GoogleInterface.leshanServer );

            //  ogni device mantiene un istanza del leshan device per accedere alle proprie informazioni
            Device data = new Device( newDev );
            //  si crea una nuova chiave per identificare il device alle richieste di query e exec
            registratedDevices.put( registration.getId() , data.id );

            //  se il device è già presente significa che il device si era già registrato precedentemente
            //  ed è già presente tra i device, solo che è offline
            if( deviceData.containsKey( data.id )){

                deviceData.replace( registratedDevices.get(registration.getId()) , data );
                LOG.info( "updated registered device " + registratedDevices.get( registration.getId()) + ": OFFLINE->ONLINE" );
                return;

            }
            //  altrimenti si inserisce il nuovo device
            deviceData.put( data.id , data );

            LOG.info( "new device " + registratedDevices.get( registration.getId()) + " registrated: ONLINE");
            saveDevice();                     //  aggiorno i dati locali
            GoogleInterface.requestSync();    //  aggiorno google home

        }catch( Exception e ){

            LOG.error("error while trying to manage device registration\n" + e.getMessage());

        }

    }


    //  UTILIZZATA DALL'INTERFACCIA GOOGLE
    //  fa eseguire un comando a un device e ne da in ritorno la risposta

    public synchronized ResponseCommand getExecution( String deviceId  , ExecuteGoogleRequest command ){

        if( deviceData.containsKey( deviceId ))
             return deviceData.get( deviceId ).getExecution( command  );
        return  null;

    }


    //  UTILIZZATA DA SERVER LESHAN
    //  la funzione gestisce il multiplexing dei dati a seguito di una observation.
    //  ogni action dei device mette in esecuzione delle observation per mantenere aggiornati i dati
    //  di query e exec. Utilizzando le associazioni tra registrationID e deviceId, la funzione
    //  determina a quale device appartiene la richiesta e gliela inoltra

    public synchronized static void observationController( Registration registration , ObserveResponse response ){

        if( !registratedDevices.containsKey( registration.getId() )) return;

        deviceData.get(registratedDevices.get(registration.getId())).putObservation( response );

    }


    //  PER INTERFACCIA RIMOZIONE MANUALE DEVICE GOOGLE

    public synchronized void removeDevice( String id ){

        if( !deviceData.containsKey( id )) return;

        deviceData.remove( id);
        saveDevice();
        GoogleInterface.requestSync();

    }


    //  Funzione richiamata da leshan quando un device si deregistra
    //  la funzione setta il device offline ed elimina la sua chiave dalle associazioni dei device online

    public synchronized void setOffline( Registration registration ){

        if( !registratedDevices.containsKey( registration.getId() )) return;

        deviceData.get( registratedDevices.get( registration.getId() )).setOffline();
        registratedDevices.remove( registration.getId());
        LOG.info( "device " + registratedDevices.get( registration.getId()) + " changed state: ONLINE->OFFLINE");

    }


    public synchronized int numDevices(){ return deviceData.size(); }


    //  funzione utilizzata da Leshan quando un dispositivo aggiorna le istanze che mantiene

    public synchronized void updateDevice( Registration registration ){

        if( !registratedDevices.containsKey( registration.getId()) ) return;

        //  sostituisco il dispositivi con una nuova copia
        //  LA NUOVA COPIA ISTANZIA NUOVE OBSERVE MA LESHAN è IN GRADO DI RISOLVERE IL PROBLEMA E MANTENERE
        //  UN'UNICA RICHIESTA
        deviceData.replace( registratedDevices.get( registration.getId()), new Device(  deviceData.get( registratedDevices.get( registration.getId()))));
        saveDevice();                    //  aggiorno i dati locali
        LOG.info( "device " + registratedDevices.get( registration.getId()) + " update information" );
        GoogleInterface.requestSync();   //  aggiorno google home

    }


    //  salva i device in un file binario per poterli ricaricare al successivo avvio del server

    private synchronized void saveDevice(){

        try {

            ObjectOutputStream deviceContainer = new ObjectOutputStream(new FileOutputStream( "leshan-server-demo/src/main/java/googleInterface/savedData/device.bin" ));
            Iterator<Device> deviceIterator = deviceData.values().iterator();

            deviceContainer.writeInt( deviceData.size());  //  si salva la dimensione

            while( deviceIterator.hasNext())
                deviceContainer.writeObject( deviceIterator.next() );

            deviceContainer.close();
            LOG.info( "device correctly saved" );

        }catch( IOException e ){

            LOG.error("error trying to save devices\n" + e.getMessage());

        }

    }

}
