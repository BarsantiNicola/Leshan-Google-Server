package googleInterface.protocol.message;


//  classe POJO per convertire messaggio gson di risposta a execute

public class ExecResponse extends Message {

    private ExecResponsePayload payload;


    public ExecResponsePayload getPayload(){ return payload; }

    public void setPayload( ExecResponsePayload a ){ payload = a; }

}
