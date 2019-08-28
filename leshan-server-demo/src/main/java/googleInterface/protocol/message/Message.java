package googleInterface.protocol.message;


//  classe POJO principale per la definizione dei vari tipi di messaggi

public class Message {

    private String requestId;   //  ogni messaggio deve possedere un requestId che deve essere riusato invariato


    public String getRequestId(){ return requestId; }

    public void setRequestId( String r ){ requestId = r; }
}
