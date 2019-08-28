package googleInterface.protocol.message;


//  classe POJO per convertire messaggio gson di richiesta query

public class QueryRequest extends Message{

    private QueryRequestInput[] inputs;


    public QueryRequestInput[] getInput(){ return inputs; }

    public void setInputs( QueryRequestInput[] i ){ inputs = i; }

    public QueryResponse getResponseMessage(){

        QueryResponse response = new QueryResponse();
        QueryResponsePayload payload = new QueryResponsePayload();
        response.setRequestId( getRequestId());
        response.setPayload( payload );

        return response;

    }


}
