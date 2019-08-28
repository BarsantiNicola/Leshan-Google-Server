package googleInterface.protocol.message;


//  classe POJO per convertire messaggio gson di richiesta di execute

public class ExecRequest extends Message{

    private ExecRequestInput[] inputs;


    public ExecRequestInput[] getInputs(){ return inputs; }

    void setInputs( ExecRequestInput[] e ){ inputs = e; }

    public ExecResponse getResponseMessage(){

        ExecResponse response = new ExecResponse();
        ExecResponsePayload payload = new ExecResponsePayload();
        response.setRequestId( getRequestId());
        response.setPayload( payload );

        return response;

    }


}
