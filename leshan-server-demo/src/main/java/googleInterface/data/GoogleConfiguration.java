package googleInterface.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


//  classe POJO per le informazioni del file configurazione_google/conf.json

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class GoogleConfiguration {

        @JsonProperty("smarthomeEndpoint")
        private String smartHomeEndpoint;  //  INDIRIZZO IP ASSEGNATO

        @JsonProperty("smarthomePort")     //  PORTA UTILIZZATA DA INTERFACCIA HTTP
        private int smartHomePort;

        @JsonProperty("smarthomeSecurePort")  //  PORTA UTILIZZATA DA INTERFACCIA HTTPS
        private int smartHomeSecurePort;

        @JsonProperty("smarthomeRefreshTime")  //  TIMER IN SECONDI DEL REFRESH DEI TOKEN
        private long smartHomeRefreshTime;

        @JsonProperty("google_client_id")   //  CLIENT_ID ACCETTATO DAL SERVIZIO
        private String googleClientId;

        @JsonProperty("google_APIkey")  //  API_KEY CREATA DA CREDENTIALS IN GOOGLE ACTIONS
        private String googleAPIkey;   //  SERVE A ESEGUIRE RE_SYNC AL SERVIZIO NEL CLOUD GOOGLE

        @JsonProperty("service_Username")   //  NOME UTENTE UTILIZZATO NEL LOGIN
        private String serviceUsername;

        @JsonProperty("service_Password")   //  PASSWORD UTILIZZATA NEL LOGIN
        private String servicePassword;

        @JsonProperty("request_sync_endpoint")   //  ENDPOINT GOOGLE A CUI EFFETTUARE RICHIESTE RESYNC
        private String requestSyncEndpoint;

        @JsonProperty("agent_user_id")    //  AGENT_USER_ID ASSEGNATO ALL'UTENTE DA FORNIRE A GOOGLE PER IDENTIFICARLO
        private String agentUserId;

        public String getSmartHomeEndpoint(){  return smartHomeEndpoint;  }

        public int getSmartHomePort(){ return smartHomePort; }

        public int getSmartHomeSecurePort(){ return smartHomeSecurePort; }

        public String getGoogleClientId(){ return googleClientId; }

        public String getGoogleAPIkey(){ return googleAPIkey; }

        public String getServiceUsername(){ return serviceUsername; }

        public String getServicePassword(){ return servicePassword; }

        public long getSmartHomeRefreshTime(){ return smartHomeRefreshTime; }

        public String getRequestSyncEndpoint(){ return requestSyncEndpoint; }

        public String getAgentUserId(){ return agentUserId; }


}
