package googleInterface.integration.actions;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import googleInterface.integration.LeshanDevice;
import googleInterface.integration.LeshanResource;
import java.io.Serializable;
import java.util.HashMap;


public class ColorSetting extends Trait {

    private Color color;
    private LeshanResource COLOR_RESOURCE;

    class Color implements Serializable{

        private String model;
        private Integer temperature = null ;
        private Integer min = null ,max = null;
        private Integer rgbValue = null;
        private String name = null;
        private  Integer hsvHue = null;
        private Integer hsvSaturation = null;
        private Integer hsvValue = null;

        Color( Integer rgb , String n ){

            name = n;
            rgbValue = rgb;
            model = "rgb";

        }

        Color( int t , int mi , int ma ){
            temperature = t;
            min = mi;
            max = ma;
            model = null;
        }

        Color( int hue , int saturation , int value , String n ){

            name = n;
            hsvHue = hue;
            hsvSaturation = saturation;
            hsvValue = value;
            model = "hsv";

        }

        public Object getColor(){

            HashMap<String, Object> ret = new HashMap<>();
            if( model.compareTo("rgb") == 0 ) return rgbValue;

            ret.put("hue" , hsvHue );
            ret.put("saturation" , hsvSaturation );
            ret.put("value" , hsvValue );

            return ret;

        }

        public String getName(){ return name; }

        public Integer getTemperature(){ return temperature; }

        public Integer getMin(){ return min; }

        public Integer getMax(){ return max; }

    }

    ColorSetting( String action , LeshanResource[] resources, LeshanDevice dev ){

        super( action , resources, dev );
        Gson gson = new Gson();
        color = gson.fromJson((String)device.readResource( COLOR_RESOURCE ) , Color.class );
        startObservation();

    }

    public HashMap<String , Object> queryInformation(){

        HashMap<String , Object > ret = new HashMap<>();
        HashMap<String , Object> c = new HashMap<>();

        if( color == null )
            return null;

        if( color.model == null ) {
            c.put("temperatureK", color.getTemperature());
            return ret;
        }

        if( color.model.compareTo( "rgb") == 0 )
            c.put("spectrumRGB", color.getColor() );

        else
           c.put( "spectrumHSV" , color.getColor());

        ret.put( "color" , c );

        return ret;

    }

    public HashMap<String , Object> getAttribute(){

        HashMap<String , Object > ret = new HashMap<>();
        HashMap<String,Object> c = new HashMap<>();

        ret.put( "colorModel" , color.model );
        if( color.model == null ){

            c.put( "temperatureMinK", color.getMin() );
            c.put( "temperatureMaxK" , color.getMax() );
            ret.put("colorTemperatureRange" , c );

        }

        ret.put("commandOnlyColorSetting" , true );

        return ret;

    }

    public boolean handleRequest( String command , HashMap< String , Object > params ){

        Gson gson = new Gson();

        LinkedTreeMap<String, Object> col = (LinkedTreeMap<String,Object>)params.get( "color");

        if( command.compareTo( "action.devices.commands.ColorAbsolute") != 0 ) return false;

        if( color.model.compareTo( "rgb" ) == 0 && col.containsKey( "spectrumRGB")){

            color.rgbValue = ((Double)col.get( "spectrumRGB")).intValue();
            color.name = (String)col.get("name");
            return device.writeResource( COLOR_RESOURCE , gson.toJson( color ));

        }

        if( color.model.compareTo( "hsv") == 0 && col.containsKey( "spectrumHSV")){

            color.hsvValue = (Integer)col.get( "value");
            color.hsvHue = (Integer)col.get( "hue");
            color.hsvSaturation = (Integer)col.get( "saturation");
            return device.writeResource( COLOR_RESOURCE , gson.toJson( color ));

        }

        if( col.containsKey("temperature")){

            int t = (Integer)col.get( "temperature");
            if( t< color.min || t>color.max ) return false;
            color.temperature = t;
            return device.writeResource( COLOR_RESOURCE , gson.toJson( color ));

        }



        LOG.error( "error, the  only color model supported are hsv or rgb");
        return false;
    }

    public void putObservation( LeshanResource resourceId , Object value ){

        Gson gson = new Gson();

        if( resourceId.getRID() == COLOR_RESOURCE.getRID() )
            color = gson.fromJson((String)value, Color.class );

    }

    public void startObservation(){

        device.addObservation( COLOR_RESOURCE );

    }

    @Override
    public void setVariables( LeshanResource[] resources ){

        if( resources.length != 1 ){

            LOG.error( "Error, number of variables " + resources.length + ". Only 1 variable allowed");
            return;

        }

        COLOR_RESOURCE = resources[0];

    }



}
