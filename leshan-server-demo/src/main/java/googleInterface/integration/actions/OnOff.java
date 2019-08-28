package googleInterface.integration.actions;

import googleInterface.MyLogger;
import googleInterface.integration.LeshanDevice;
import googleInterface.integration.LeshanResource;

import java.util.HashMap;

public class OnOff extends Trait {

    private boolean on;
    private LeshanResource ON_RESOURCE;
    private MyLogger LOG = new MyLogger( OnOff.class );

    OnOff( String action , LeshanResource[] resources , LeshanDevice dev ){

        super( action , resources , dev );

        on = (boolean)device.readResource( ON_RESOURCE );

    }

    @Override
    public HashMap<String , Object> queryInformation(){

        HashMap<String , Object> ret = new HashMap<>();
        ret.put( "on" , on );
        return ret;
    }

    @Override
    public void putObservation( LeshanResource resourceId , Object value ){

        if( resourceId.getRID() == ON_RESOURCE.getRID() )
            on = (boolean) value;

    }

    @Override
    public void startObservation(){

        device.addObservation( ON_RESOURCE );

    }

    @Override
    public boolean handleRequest( String command , HashMap< String , Object > params ){

        if( command.compareTo("action.devices.commands.OnOff") != 0 ) return false;


        if( params.containsKey("on") && device.writeResource( ON_RESOURCE , params.get( "on" ))){

            on = (boolean)params.get( "on" );
            return true;

        }

        return false;

    }


    @Override
    public HashMap<String , Object> getAttribute(){ return new HashMap<>(); }


    @Override
    public void setVariables( LeshanResource[] resources ){

        if( resources.length != 1 ){

            LOG.error( "Error, number of variables " + resources.length + ". Only 1 variable allowed");
            return;

        }

        ON_RESOURCE = resources[0];

    }

}
