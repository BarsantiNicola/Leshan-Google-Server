package googleInterface.integration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import googleInterface.MyLogger;
import googleInterface.integration.actions.Trait;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


//  si occupa della conversione dei dispositivi Leshan in Google.
//  implementa metodi per trovare le azioni di un dispositivo partendo dalle sue risorse
//  conoscere i comandi e gli "state"(nome dei campi per gli status) associati a un azione

public class LeshanGoogleConverter {

    private static final MyLogger LOG = new MyLogger( LeshanGoogleConverter.class);

    private static HashMap<String , String[]> googleLeshanDevice;  //  actions disponibili per ogni tipo di dispositivo
    private static HashMap<String , TraitConvertion> googleLeshanTrait;  //  risorse necessarie per realizzare un action

    public LeshanGoogleConverter(){

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Type type;

        try{

            type = new TypeToken<HashMap<String, String[]>>(){}.getType();
            googleLeshanDevice = gson.fromJson(new String( Files.readAllBytes( Paths.get( "leshan-server-demo/src/main/java/googleInterface/integration/convertion/device.json"))) , type );
            LOG.info( "correctly load device/actions association data from convertion/device.json");

            type = new TypeToken<HashMap<String, TraitConvertion>>(){}.getType();
            googleLeshanTrait = gson.fromJson(new String( Files.readAllBytes( Paths.get( "leshan-server-demo/src/main/java/googleInterface/integration/convertion/traits.json"))) , type );
            LOG.info( "correctly load trait/OID association data from convertion/traits.json");

        }catch( IOException e ){

            LOG.error("an error as occurred while trying to load convertion information data " + e.getMessage());
            System.exit( -1 );

        }

    }


    //  fornisce i comandi disponibili per il trait(action) selezionato

    static String[] getCommands( String trait ){

        String[] noItem = { "" };
        if( googleLeshanTrait.containsKey( trait ))
            return googleLeshanTrait.get( trait ).getCommands();
        return noItem;

    }


    //  fornisce tutti una lista di tutti i trait disponibili
    //  utilizzata dalla funzione Trait.createTrait
    public static String[] getTraitsList(){

        String[] ret = new String[ googleLeshanTrait.size()];
        Set<String> traits = googleLeshanTrait.keySet();
        int pos = 0;

        for( String t: traits )
            ret[pos++] = t;

        return ret;

    }


    //  fornisce una lista di Trait compatibili con il dispositivo
    private static String[] getTraits( LeshanDevice dev ){

        List<String> retList = new ArrayList<>();
        String[] traits = googleLeshanDevice.get( dev.getType() );
        String[] ret;
        Iterator<String> i;
        for( String trait: traits ) System.out.println("Trait: " + trait );
        if( traits != null )
            for( String t : traits ) {
                LeshanResourceConvertion[] R = googleLeshanTrait.get(t).getResources();
                if (dev.haveObjects(R))
                    retList.add(t);

            }


        ret = new String[ retList.size()];
        i = retList.iterator();
        for( int a = 0; i.hasNext(); a++ )
            ret[a] = i.next();
        return ret;
    }

    private static LeshanResource[] getTraitResources( LeshanDevice dev, String trait ){


        List<LeshanResource> resourceList = dev.getLeshanObjects( googleLeshanTrait.get( trait ).getResources());
        Iterator<LeshanResource> resourceIterator = resourceList.iterator();
        LeshanResource[] ret = new LeshanResource[ resourceList.size() ];

        for( int a = 0; resourceIterator.hasNext(); a++ )
            ret[a] = resourceIterator.next();

        return ret;

    }


    //  fornisce le istanze delle classi Trait necessarie al dispositivo
    static Trait[] getTraitsClass( LeshanDevice dev ){

        Trait[] ret;
        List<Trait> list = new ArrayList<>();
        Iterator<Trait> i;

        String[] traits = getTraits( dev );


        for( String trait: traits )

            try {

                list.add( Trait.createTrait( trait , getTraitResources( dev , trait ), dev ));

            }catch( Exception e ){

                LOG.error("an error as occured while trying to associate actions to the device " + dev.getId() +"\n" + e.getMessage());

            }

        i = list.iterator();
        ret = new Trait[ list.size() ];

        for( int a =0; i.hasNext(); a++)
            ret[a] = i.next();
        LOG.info( "device " + dev.getId() + " actions association created");
        return ret;

    }

    //  fornisce lo state associato al trait
    static LeshanResourceConvertion[] getStates( String trait ){
        return googleLeshanTrait.get( trait ).getResources();
    }
}
