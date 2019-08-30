package org.eclipse.leshan.client.demo;

import com.google.gson.Gson;
import org.eclipse.leshan.client.request.ServerIdentity;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;

import java.io.Serializable;
import java.util.*;

public class OnOffSensor  extends BaseInstanceEnabler {

    private boolean OnOff = false;
    private OnOffSensor.Color color = new OnOffSensor.Color( 16711935 ,  "incandescent" );

    private boolean observe = false;
    private static final int ONOFF = 5850;
    private static final int COLOR = 5706;

    private static final List<Integer> supportedResources = Arrays.asList(5850,5706);

    class Color implements Serializable {

        private String model;
        private Integer temperature;
        private Integer rgbValue = null;
        private String name;
        private  Integer hsvHue = null;
        private Integer hsvSaturation = null;
        private Integer hsvValue = null;

        Color( Integer rgb , String n ){

            name = n;
            rgbValue = rgb;
            model = "rgb";

        }

        Color( int t ){
            temperature = t;
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

    }

    @Override
    public synchronized ReadResponse read(ServerIdentity identity, int resourceId) {

        Gson gson = new Gson();
        switch (resourceId) {
            case ONOFF:
                return ReadResponse.success(resourceId, OnOff );
            case COLOR:
                return ReadResponse.success( resourceId, gson.toJson( color ) );

            default:
                return super.read(identity, resourceId);
        }
    }

    @Override
    public WriteResponse write(ServerIdentity identity, boolean replace, LwM2mObjectInstance value) {

        Map<Integer, LwM2mResource> resourcesToWrite = new HashMap<>(value.getResources());
        Set<Integer> resource = resourcesToWrite.keySet();
        Gson gson = new Gson();
        for( int index: resource )
            switch( index ){
                case ONOFF:
                OnOff = Boolean.getBoolean( (String)resourcesToWrite.get( index ).getValue());
                    return WriteResponse.success();

                default: System.out.println(index);

            }
        return WriteResponse.notFound();

    }

    @Override
    public WriteResponse write(ServerIdentity identity, int resourceid, LwM2mResource value) {
            boolean o = OnOff;
            Gson gson = new Gson();
            switch( resourceid ){
                case ONOFF:
                    OnOff = (Boolean)value.getValue();
                     fireResourcesChange(5850  );

                    return WriteResponse.success();
                case COLOR:
                    color = gson.fromJson((String)value.getValue() , OnOffSensor.Color.class );
                    fireResourcesChange(5706  );

                    return WriteResponse.success();
                default: return WriteResponse.notFound();

            }

    }

    @Override
    public synchronized ExecuteResponse execute(ServerIdentity identity, int resourceId, String params) {

        switch (resourceId) {

            default:
                return super.execute(identity, resourceId, params);
        }
    }

    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        return supportedResources;
    }

}
