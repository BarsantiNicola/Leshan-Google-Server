package googleInterface.protocol.message;


//  classe POJO per convertire payload delle risposte a richieste di SYNC

public class SyncResponsePayload{

    private String agentUserId;
    private String errorCode;
    private String debugString;
    private SyncDevice[] devices;


    public String getAgentUserId(){ return agentUserId; }

    public void setAgentUserId( String id ){ agentUserId = id; }

    public String getErrorCode(){ return errorCode; }

    public void setErrorCode( String e ){ errorCode = e; }

    public String getDebugString(){ return debugString; }

    public void setDebugString( String e ){ debugString = e; }

    public SyncDevice[] getDevices(){ return devices; }

    public void setDevices( SyncDevice[] i ){ devices = i; }

}
