package googleInterface.protocol.message;

//  classe POJO per mantere i comandi di una richiesta di EXECUTE

public class ExecRequestCommand {

    private ExecDevice[] devices;
    private ExecuteGoogleRequest[] execution;

    public ExecDevice[] getDevices(){ return devices; }

    public void setDevices( ExecDevice[] dev ){ devices = dev; }

    public ExecuteGoogleRequest[] getExecution() { return execution; }

    public void setExecution( ExecuteGoogleRequest[] ex ){ execution = ex; }

}
