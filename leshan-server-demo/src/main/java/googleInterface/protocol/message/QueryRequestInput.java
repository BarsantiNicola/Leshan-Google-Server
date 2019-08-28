package googleInterface.protocol.message;


//  classe POJO per mantenere le informazioni di input di una richiesta di QUERY

public class QueryRequestInput {

    private String intent;
    private QueryRequestPayload payload;

    public String getIntent(){ return intent; }

    public void setIntent( String i ){ intent = i; }

    public QueryRequestPayload getPayload(){ return payload; }

    public void setPayload( QueryRequestPayload p ){ payload = p; }

}
