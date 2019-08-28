package googleInterface.protocol.message;


import java.util.HashMap;

//  classe POJO per mantenere le informazioni del payload della risposta a una richiesta QUERY

public class QueryResponsePayload{

        private HashMap<String, Object> devices;
        private String errorCode;
        private String debugString;


        public HashMap<String,Object> getDevices(){ return devices; }

        public void setDevices( HashMap<String,Object> dev ){ devices = dev; }

        public String getErrorCode(){ return errorCode; }

        public void setErrorCode( String e ){ errorCode = e; }

        public String getDebugString(){ return debugString; }

        public void setDebugString( String e ){ debugString = e; }

}
