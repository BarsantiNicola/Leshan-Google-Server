package googleInterface.protocol.message;


//  classe POJO per convertire campo intent dei messaggi di richiesta a SYNC e QUERY

public class SyncRequestIntent {

    private String intent;

    public String getIntent(){ return intent; }

    public void setIntent( String i ){ intent = i; }

}
