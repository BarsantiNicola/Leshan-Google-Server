package googleInterface.integration.actions;

import googleInterface.integration.LeshanDevice;
import googleInterface.integration.LeshanResource;


import java.util.HashMap;

public class LockUnlock extends Trait {

    LockUnlock( String action , LeshanResource[] resources, LeshanDevice dev ){

        super( action , resources, dev );
    }

    public HashMap<String , Object> queryInformation(){ return null; }

    public boolean handleRequest( String command , HashMap< String , Object > params ){ return true; }

    public void putObservation(LeshanResource resourceId , Object value ){}

    public void startObservation(){}

    public void setVariables( LeshanResource[] resources ){}

    @Override
    public HashMap<String , Object> getAttribute(){ return null; }

}
