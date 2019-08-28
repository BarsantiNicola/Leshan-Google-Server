package googleInterface.protocol.message;


//  classe POJO per convertire messaggio gson di risposta a sync

public class SyncResponse extends Message{

    private SyncResponsePayload payload;


    public SyncResponsePayload getPayload(){ return payload; }

    public void setPayload( SyncResponsePayload p ){ payload = p; }

}
