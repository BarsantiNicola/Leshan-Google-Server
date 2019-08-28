package googleInterface.protocol.message;

import googleInterface.GoogleInterface;


//  classe POJO per convertire messaggio gson di richiesta  sync

public class SyncRequest extends Message{

    private SyncRequestIntent[] inputs;


    public SyncRequestIntent[] getInputs(){ return inputs; }

    public void setInputs( SyncRequestIntent[] i ){ inputs = i; }

    public SyncResponse getResponseMessage(){

        SyncResponse response = new SyncResponse();
        SyncResponsePayload payload = new SyncResponsePayload();
        payload.setAgentUserId(GoogleInterface.confData.getAgentUserId());
        response.setRequestId( this.getRequestId());
        response.setPayload( payload );

        return response;
    }

}
