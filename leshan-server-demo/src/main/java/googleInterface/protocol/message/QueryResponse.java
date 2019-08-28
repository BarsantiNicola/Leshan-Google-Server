package googleInterface.protocol.message;


//  classe POJO per convertire messaggio gson di risposta a query

public class QueryResponse extends Message{

    private QueryResponsePayload payload;


    public QueryResponsePayload getPayload(){ return payload; }

    public void setPayload( QueryResponsePayload a ){ payload = a; }

}
