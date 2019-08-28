package googleInterface.protocol.message;


//  classe POJO per convertire risultati come risposta alla richiesta di EXECUTE

import java.util.HashMap;

public class ResponseCommand {

    private String[] ids;
    private String status;
    private String errorCode;
    private String debugString;
    private HashMap<String , Object> states;

    public ResponseCommand( String s , String id ){

        ids = new String[1];
        ids[0] = id;
        status = s;
        states = new HashMap<>();

        if( s.compareTo("DISCONNECT") == 0 )
            errorCode = "Device offline";
    }

    public String[] getIds(){ return ids; }

    public void setIds( String[] i ){ ids = i; }

    public String getStatus(){ return status; }

    public void setStatus( String s ){ status = s; }

    public String getErrorCode(){ return errorCode; }

    public void setErrorCode( String e ){ errorCode = e; }

    public String getDebugString(){ return debugString; }

    public void setDebugString( String e ){ debugString = e; }

    public HashMap<String , Object> getStates(){ return states; }

    public void setStates( HashMap<String , Object> s ){ states = s; }

    public void merge( ResponseCommand r ){
        String[] id;
        if( r == null ) return;
        id = ids;
        ids = new String[ id.length + r.getIds().length];
        for( int a = 0; a< id.length; a++ )
            ids[a] = id[a];
        for( int a = 0; a<r.getIds().length; a++ )
            ids[a+id.length] = r.getIds()[a];
        states.putAll( r.getStates());
    }

}
