package googleInterface.protocol.message;


//  classe POJO per mantere le informazioni di Inputs di una richiesta di execute

public class ExecRequestInput {

    private String intent;
    private ExecRequestPayload payload;


    public String getIntent(){ return intent; }

    public void setIntent( String i ){ intent = i; }

    public ExecRequestPayload getPayload(){ return payload; }

    public void setPayload( ExecRequestPayload e ){ payload = e; }

}
