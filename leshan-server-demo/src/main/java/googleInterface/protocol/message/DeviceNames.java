package googleInterface.protocol.message;


import java.io.Serializable;

//  classe POJO per mantenere le informazioni di identificazione del device
public class DeviceNames implements Serializable {

    private String name;
    private String[] defaultNames;  //opt
    private String[] nicknames;  //opt

    public DeviceNames( String n , String[] defaultn , String[] nicks ){

        name = n;
        defaultNames = defaultn;
        nicknames = nicks;

    }

    public String getName(){ return name; }

    public void setName( String n ){ name = n; }

    public String[] getDefaultNames(){ return defaultNames; }

    public void setDefaultNames( String n[] ){ defaultNames = n; }

    public String[] getNicknames(){ return nicknames; }

    public void setNicknames( String nick[] ){ nicknames = nick; }

}
