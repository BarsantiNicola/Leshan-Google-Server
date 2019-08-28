package googleInterface.protocol.message;


import java.io.Serializable;


//  classe POJO per mantenere le informazioni che un device deve fornire a una richiesta di QUERY
public class QueryDevice implements Serializable {

    private String id;
    private boolean online;

    private CustomData customData;   //  opzionale, informazioni aggiuntive inserite dal venditore

    public String getId(){ return id; }

    public void setId( String i ){ id = i; }

    public CustomData getCustomData(){ return customData; }

    public void setCustomData( CustomData c ){ customData = c; }

    public boolean getOnline(){ return online; }

    public void setOnline( boolean o ){ online = o; }

}
