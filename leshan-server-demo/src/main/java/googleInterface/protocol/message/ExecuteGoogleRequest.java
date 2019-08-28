package googleInterface.protocol.message;


import java.util.HashMap;

//  classe POJO per mantenere i comandi e i parametri necessari per una richiesta di execute

public class ExecuteGoogleRequest {

    private String command;
    private HashMap<String , Object> params;


    public String getCommand(){ return command; }

    public void setCommand( String c ){ command = c; }

    public HashMap<String, Object> getParams(){ return params; }

    public void setParams( HashMap<String, Object> p ){ params = p; }

}
