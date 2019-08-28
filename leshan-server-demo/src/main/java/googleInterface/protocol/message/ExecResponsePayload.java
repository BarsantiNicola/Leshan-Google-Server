package googleInterface.protocol.message;


//  classe POJO per mantenere le informazioni del payload di una risposta a una richiesta di query

public class ExecResponsePayload {

    private ResponseCommand[] commands;
    private String errorCode;
    private String debugString;


    public ResponseCommand[] getCommands(){ return commands; }

    public void setCommands( ResponseCommand[] i ){ commands = i; }

    public String getErrorCode(){ return errorCode; }

    public void setErrorCode( String e ){ errorCode = e; }

    public String getDebugString(){ return debugString; }

    public void setDebugString( String e ){ debugString = e; }

}
