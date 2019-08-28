package googleInterface.integration.actions;

import googleInterface.integration.LeshanDevice;
import googleInterface.integration.LeshanResource;


import java.util.HashMap;

public class Brightness extends Trait {

    private Integer brightness;
    private LeshanResource BRIGHTNESS_RESOURCE;

    Brightness( String action , LeshanResource[] resources, LeshanDevice dev ){

        super( action ,resources, dev );

        //  ATTENZIONE AL TIPO DELLE VARIABILI, le funzioni Leshan sono poco robuste
        //  sui tipi delle variabili, essendoci vari risorse alias, prima di definire le variabili
        //  analizzare tutti i tipi delle risorse coinvolte nella variabili e agire di conseguenza.

        double val = (Double)device.readResource( BRIGHTNESS_RESOURCE );
        brightness = (int)val;

        startObservation();

    }

    @Override
    public HashMap<String , Object> queryInformation(){

        HashMap<String, Object> ret = new HashMap<>();

        ret.put( "brightness" , brightness );
        return ret;

    }

    @Override
    public boolean handleRequest( String command , HashMap< String , Object > params ) {

        if (command.compareTo("action.devices.commands.BrightnessAbsolute") != 0) return false;

        Integer var = ((Double)params.get("brightness")).intValue();

        //  c'è un pò di confusione nella documentazione, segna che richiede Float o Integer(a seconda delle risorse),
        //  ma sono riuscito a farlo funzionare solo con Integer(le risorse che richiedono Float non lo accettano,
        //  vogliono un Integer)

        if( device.writeResource( BRIGHTNESS_RESOURCE , var )){

            brightness = var;
            return true;

        }else
            return false;

    }

    @Override
    public void putObservation( LeshanResource resourceId , Object value ){

        long val;

        if( resourceId.getRID() == BRIGHTNESS_RESOURCE.getRID() ){

                val = (long)value;
                brightness = (int)val;

        }
    }

    @Override
    public void startObservation(){

        device.addObservation( BRIGHTNESS_RESOURCE );

    }

    @Override
    public HashMap<String , Object> getAttribute(){ return new HashMap<>(); }

    @Override
    public void setVariables( LeshanResource[] resources ){

        if( resources.length != 1 ){

            LOG.error( "Error, number of variables " + resources.length + ". Only 1 variable allowed");
            return;

        }

        BRIGHTNESS_RESOURCE = resources[0];

    }

}
