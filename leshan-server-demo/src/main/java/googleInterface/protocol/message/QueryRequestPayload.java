package googleInterface.protocol.message;


//  classe POJO per mantenere le informazioni del payload di una risposta a una richiesta di query

public class QueryRequestPayload {

    private QueryDevice[] devices;


    public QueryDevice[] getDevices(){ return devices; }

    public void setDevices( QueryDevice[] dev ){ devices = dev; }

}
