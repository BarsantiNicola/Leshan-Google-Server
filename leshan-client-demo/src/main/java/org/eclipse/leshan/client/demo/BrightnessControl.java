package org.eclipse.leshan.client.demo;

import org.eclipse.leshan.client.request.ServerIdentity;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;

import java.util.*;

public class BrightnessControl extends BaseInstanceEnabler {

    private Integer brightness = 0;
    private boolean observe = false;
    private static final int BRIGHTNESS= 5548;
    private static final List<Integer> supportedResources = Arrays.asList(5548);

    @Override
    public synchronized ReadResponse read(ServerIdentity identity, int resourceId) {

        switch (resourceId) {
            case BRIGHTNESS:
                return ReadResponse.success(resourceId, brightness );

        }

        return ReadResponse.notFound();
    }


    @Override
    public WriteResponse write(ServerIdentity identity, boolean replace, LwM2mObjectInstance value) {

        Map<Integer, LwM2mResource> resourcesToWrite = new HashMap<>(value.getResources());
        Set<Integer> resource = resourcesToWrite.keySet();

        for( int index: resource )
            switch( index ){
                case BRIGHTNESS:System.out.println(index);
                    brightness = (Integer)value.getResource( index ).getValue();
                    fireResourcesChange(5548  );
                    return WriteResponse.success();
                default: System.out.println(index);

            }
        return WriteResponse.notFound();

    }

    @Override
    public WriteResponse write(ServerIdentity identity, int resourceid, LwM2mResource value) {

        switch( resourceid ){
            case BRIGHTNESS:

                brightness = ((Double)value.getValue()).intValue();
                fireResourcesChange(5548  );
                return WriteResponse.success();

            default: return WriteResponse.notFound();

        }

    }


    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        return supportedResources;
    }

}
