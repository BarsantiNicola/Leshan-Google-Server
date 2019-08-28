package googleInterface.integration.actions;

import googleInterface.integration.DeviceData;
import googleInterface.integration.LeshanDevice;
import googleInterface.integration.LeshanGoogleConverter;
import googleInterface.integration.LeshanResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//  classe per associare un trait alla codifica nelle risposte a google

public abstract class Trait implements Serializable {

    static final Logger LOG = LoggerFactory.getLogger(DeviceData.class);
    private final String trait;
    final LeshanDevice device;
    private HashMap<Integer,Object> myLeshanObjects;
    private LeshanResource[] myTraitObjects;

    Trait( String a , LeshanResource[] resources, LeshanDevice dev ) {

        trait = a;
        device = dev;
        myLeshanObjects = device.getTraitObjects( trait );
        myTraitObjects = getObjectsID();
        setVariables( resources );

    }


    //  fornisce una sottoclasse di Trait basandosi sul nome della action

    public static Trait createTrait( String name , LeshanResource[] resources , LeshanDevice dev ) throws Exception{

        String[] traits = LeshanGoogleConverter.getTraitsList();
        int traitIndex = -1;
        System.out.println("Trait: " + name );
        for( int a = 0; a<traits.length; a++ )
            if( traits[a].compareTo( name ) == 0 ){

                traitIndex = a;
                break;

            }

        if( traitIndex == -1 ) throw new Exception( "Error, the trait " + name + " isn't defined" );

        switch( traitIndex ) {

            case 0: return new Dock( name ,resources, dev );
            case 1: return new Scene( name ,resources, dev );
            case 2: return new TemperatureSetting( name ,resources, dev );
            case 3: return new LockUnlock( name ,resources, dev );
            case 4: return new RunCycle( name ,resources, dev );
            case 5: return new LightEffects( name ,resources, dev );
            case 6: return new OnOff( name ,resources, dev );
            case 7: return new Toggles( name ,resources, dev );
            case 8: return new ArmDisarm( name ,resources, dev );
            case 9: return new Modes( name ,resources, dev );
            case 10:return new ColorSetting( name ,resources, dev );
            case 11:return new StartStop( name ,resources, dev );
            case 12:return new CameraStream( name ,resources, dev );
            case 13:return new OpenClose( name ,resources, dev );
            case 14:return new Brightness( name ,resources, dev );
            case 15:return new TemperatureControl( name ,resources, dev );
            case 16:return new FanSpeed( name ,resources, dev );
            case 17:return new Timer( name ,resources, dev );
            case 18:return new Locator( name ,resources, dev );

        }
        LOG.error( "Error, No trait class found" );
        return null;

    }


    public String getTrait(){ return trait; }


    //  richiamata per compilare QUERY, deve fornire gli status del trait

    public abstract HashMap<String , Object> queryInformation();


    //  richiamata per l'esecuzione di un comando del Trait

    public abstract boolean handleRequest( String command , HashMap< String , Object > params );


    //  richiamata per gestire le NOTIFY fornite a seguito della modifica di una risorsa
    //  posta sotto osservazione

    public abstract void putObservation( LeshanResource id , Object value );


    // richiamata durante l'inizializzazione dei Trait permette di porre le risorse sotto osservazione

    public abstract void startObservation();


    //  richiamata durante la gestione della SYNC, fornisce attributi aggiuntivi necessari al Trait
    //  (vedere attributes di SYNC)

    public abstract HashMap<String , Object> getAttribute();


    //  necessaria per la gestione delle risorse, ogni Trait può lavorare su un insieme di risorse
    //  variabili(vedere meccanismo alias della conversione).
    //  per permettere una gestione slegata dal tipo di risorsa, è necessario associare le risorse
    //  alle variabili utilizzate dalle funzioni del Trait

    public abstract void setVariables( LeshanResource[] resources );


    // fornisce un albero di ricerca degli oggetti utilizzati dal Trait
    // non strettamente necessaria, permette una ricerca più efficiente delle risorse

    public HashMap<Integer,Object> getObjects(){

        return myLeshanObjects;

    }


    //  fornisce le risorse utilizzate dal Trait, necessaria a Device per compilare observeTrait

    public LeshanResource[] getObjectsID(){

        Iterator<Integer> objIterator;
        Iterator<Integer> instanceIterator;
        Iterator<Integer> resourceIterator;
        Iterator<LeshanResource> app;
        List<LeshanResource> ids = new ArrayList<>();
        Integer obj,instance;
        LeshanResource[] retValues;

        objIterator = myLeshanObjects.keySet().iterator();
        while( objIterator.hasNext() ){
            obj = objIterator.next();
            instanceIterator = ((HashMap<Integer,List<Integer>>)myLeshanObjects.get( obj )).keySet().iterator();
            while( instanceIterator.hasNext() ){
                instance = instanceIterator.next();
                resourceIterator = ((List<Integer>)((HashMap<Integer,List<Integer>>)myLeshanObjects.get( obj )).get( instance )).iterator();
                while( resourceIterator.hasNext() ){
                    ids.add( new LeshanResource( obj , instance , resourceIterator.next()));
                }
            }

        }
        retValues = new LeshanResource[ids.size()];
        app = ids.iterator();
        for( int a = 0; app.hasNext(); a++ )
            retValues[a] = app.next();
        return retValues;


    }


}
