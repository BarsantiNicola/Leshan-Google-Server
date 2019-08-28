package googleInterface.protocol.message;


//  classe POJO per mantenere le informazioni del payload di una richiesta di execute

public class ExecRequestPayload {

    private ExecRequestCommand[] commands;


    public ExecRequestCommand[] getCommands(){ return commands; }

    public void setCommands( ExecRequestCommand[] c ){ commands = c; }



}
