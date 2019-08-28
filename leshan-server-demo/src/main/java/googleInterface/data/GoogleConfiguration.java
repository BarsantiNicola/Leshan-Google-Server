package googleInterface.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


//  classe POJO per le informazioni del file configurazione_google/conf.json

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class GoogleConfiguration {

        @JsonProperty("smarthomeEndpoint")
        private String smartHomeEndpoint;

        @JsonProperty("smarthomePort")
        private int smartHomePort;

        @JsonProperty("smarthomeSecurePort")
        private int smartHomeSecurePort;

        @JsonProperty("smarthomeSessionTime")
        private long smartHomeSessionTime;

        @JsonProperty("smarthomeRefreshTime")
        private long smartHomeRefreshTime;

        @JsonProperty("google_client_id")
        private String googleClientId;

        @JsonProperty("google_client_secret")
        private String googleClientSecret;

        @JsonProperty("google_APIkey")
        private String googleAPIkey;

        @JsonProperty("service_Username")
        private String serviceUsername;

        @JsonProperty("service_Password")
        private String servicePassword;

        @JsonProperty("request_sync_endpoint")
        private String requestSyncEndpoint;

        @JsonProperty("agent_user_id")
        private String agentUserId;

        public String getSmartHomeEndpoint(){  return smartHomeEndpoint;  }

        public int getSmartHomePort(){ return smartHomePort; }

        public int getSmartHomeSecurePort(){ return smartHomeSecurePort; }

        public String getGoogleClientId(){ return googleClientId; }

        public String getGoogleClientSecret(){ return googleClientSecret; }

        public String getGoogleAPIkey(){ return googleAPIkey; }

        public String getServiceUsername(){ return serviceUsername; }

        public String getServicePassword(){ return servicePassword; }

        public long getSmartHomeSessionTime(){ return smartHomeSessionTime; }

        public long getSmartHomeRefreshTime(){ return smartHomeRefreshTime; }

        public String getRequestSyncEndpoint(){ return requestSyncEndpoint; }

        public String getAgentUserId(){ return agentUserId; }


}
