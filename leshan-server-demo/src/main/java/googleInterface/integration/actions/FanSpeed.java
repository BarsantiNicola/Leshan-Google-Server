package googleInterface.integration.actions;

import com.google.gson.Gson;
import googleInterface.integration.LeshanDevice;
import googleInterface.integration.LeshanResource;

import java.io.Serializable;
import java.util.HashMap;

public class FanSpeed extends Trait {

    private Speeds availableSpeeds;
    private boolean availableReverse;
    private String currentSpeed;
    private Boolean currentVerse;

    private class Speeds implements Serializable{

        private boolean ordered;
        private Speed[] speeds;

        Speeds(){
            ordered = true;
            speeds = new Speed[3];
            for( int a = 0; a<speeds.length; a++ )
                speeds[a] = new Speed(a);

        }
        public boolean getOrdered(){ return ordered; }

        public Speed[] getSpeeds(){ return speeds; }

        public void setOrdered( boolean o ){ ordered = o; }

        public void setSpeeds( Speed[] s ){ speeds = s; }

    }

    private class Speed implements Serializable{

        private String speed_name;
        private SpeedLanguage[] speed_values;

        Speed( int a ){

            speed_values = new SpeedLanguage[1];

            switch( a ){

                case 0:  speed_name = "S1";
                    speed_values[0] = new SpeedLanguage( a );
                    break;
                case 1:  speed_name = "S2";
                    speed_values[0] = new SpeedLanguage( a );
                    break;
                case 2:  speed_name = "S3";
                    speed_values[0] = new SpeedLanguage( a );
                    break;
                default: speed_name = "S4";
                    speed_values[0] = new SpeedLanguage( 2 );
            }
        }
        public String getSpeed_name(){ return speed_name; }

        public SpeedLanguage[] getSpeed_values(){ return speed_values; }

        public void setSpeed_name( String n ){ speed_name = n; }

        public void setSpeed_values( SpeedLanguage[] n ){ speed_values = n; }
    }

    private class SpeedLanguage implements Serializable{

        private String[] speed_synonym;
        private String lang;

        SpeedLanguage( int a ){
            String[] val = new String[2];
            switch( a ){
                case 0: val[0] = "low";
                    val[1] = "slow";
                    break;
                case 1:
                    val[0] = "medium";
                    val[1] = "intemediate";
                    break;
                case 2:
                    val[0] = "high";
                    val[1] = "fast";
            }

            speed_synonym = val;
            lang = "en";

        }
        public String[] getSpeed_synonym(){ return speed_synonym; }

        public String getLang(){ return lang; }

        public void setSpeed_synonym( String[] g ){ speed_synonym = g; }

        public void setLang( String g ){ lang = g; }

    }


    FanSpeed( String action , LeshanResource[] resources, LeshanDevice dev ){

        super( action , resources, dev );

        Gson gson = new Gson();

        availableSpeeds = gson.fromJson( (String)device.readResource( new LeshanResource("/20000/0/10009" )) ,Speeds.class );
        currentVerse = (Boolean)device.readResource( new LeshanResource("/20000/0/10001"));
        currentSpeed = (String)device.readResource( new LeshanResource("/20000/0/10000"));
        availableReverse = currentVerse!=null;
        startObservation();

    }


    public HashMap<String , Object> queryInformation(){

        HashMap<String,Object> ret = new HashMap<>();
        ret.put( "currentFanSpeedSetting" , currentSpeed );
        return ret;

    }

    public boolean handleRequest( String command , HashMap< String , Object > params ){

        if( command.compareTo("action.devices.commands.SetFanSpeed") == 0 && params.containsKey( "fanSpeed") ){

            currentSpeed = (String)params.get( "fanSpeed");
            return device.writeResource( new LeshanResource("/20000/0/10000") , currentSpeed );

        }

        if( command.compareTo( "action.devices.command.Reverse") == 0 && availableReverse ){

            currentVerse = !currentVerse;
            return device.writeResource( new LeshanResource("/20000/0/10001") , currentVerse );
        }

        return false;

    }

    public void putObservation( LeshanResource resourceId , Object value ){

        switch( resourceId.getRID() ){

            case 10000:
                currentSpeed = (String)value;
                break;
            case 10001:
                currentVerse = (Boolean)value;
                break;
        }
    }

    public void startObservation(){

        device.addObservation( new LeshanResource("/20000/0/10000"));
        if( availableReverse )
            device.addObservation( new LeshanResource("/20000/0/10001"));
    };

    public HashMap<String , Object> getAttribute(){

        HashMap<String, Object> ret = new HashMap<>();
        ret.put( "availableFanSpeeds" , availableSpeeds );
        ret.put( "reversible" , availableReverse );
        return ret;


    }

    public void setVariables( LeshanResource[] resources ){}

}
