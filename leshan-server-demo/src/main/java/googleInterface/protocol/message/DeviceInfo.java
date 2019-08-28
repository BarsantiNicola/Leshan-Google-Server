package googleInterface.protocol.message;


import java.io.Serializable;


//  classe POJO per mantenere le informazioni sull'hardware e software dei device
public class DeviceInfo implements Serializable {

    private String manufacturer;
    private String model;
    private String hwVersion;
    private String swVersion;


    public DeviceInfo( String man , String mod , String hw , String sw ){

        manufacturer = man;
        model = mod;
        hwVersion = hw;
        swVersion = sw;

    }

    public String getManufacturer(){ return manufacturer; }

    public void setManufacturer( String m ){ manufacturer = m; }

    public String getModel(){ return model; }

    public void setModel( String m ){ model = m; }

    public String getHwVersion(){ return hwVersion; }

    public void setHwVersion( String s ){ hwVersion = s; }

    public String getSwVersion(){ return swVersion; }

    public void setSwVersion( String s ){ swVersion = s; }
}
