package googleInterface.protocol.message;


import java.io.Serializable;
import java.util.HashMap;

//  classe POJO per mantenere le informazioni che un device deve fornire a una richiesta di SYNC

public class SyncDevice implements Serializable {

    private String id;
    private String type;
    private String[] traits;
    private DeviceNames name;
    private boolean willReportState;
    private String roomHint;
    private DeviceInfo deviceInfo;
    private HashMap<String , Object> attributes;        //  opzionale, attributi aggiuntivi per definire il device
    private CustomData customData;       //  opzionale, informazioni aggiuntive inserite dal venditore

    public String getId(){ return id; }

    public void setId( String i ){ id = i; }

    public String getType(){ return type; }

    public void setType( String t ){ type = t; }

    public String[] getTraits(){ return traits; }

    public void setTraits( String[] trait ){ traits = trait; }

    public DeviceNames getName(){ return name; }

    public void setName( DeviceNames n ){ name = n; }

    public boolean getWillReportState(){ return willReportState; }

    public void setWillReportState( boolean w ){ willReportState = w; }

    public String getRoomHint(){ return roomHint; }

    public void setRoomHint( String r ){ roomHint = r; }

    public DeviceInfo getDeviceInfo(){ return deviceInfo; }

    public void setDeviceInfo( DeviceInfo d ){ deviceInfo = d; }

    public HashMap<String , Object> getAttributes(){ return attributes; }

    public void setAttributes( HashMap<String , Object> a ){ attributes = a; }

    public CustomData getCustomData(){ return customData; }

    public void setCustomData( CustomData c ){ customData = c; }

}
