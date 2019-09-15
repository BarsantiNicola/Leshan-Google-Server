package googleInterface.integration;

import googleInterface.MyLogger;
import googleInterface.protocol.message.DeviceInfo;
import org.eclipse.leshan.Link;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.request.*;
import org.eclipse.leshan.core.response.*;
import org.eclipse.leshan.server.LwM2mServer;
import org.eclipse.leshan.server.registration.Registration;
import java.io.Serializable;
import java.util.*;

public class LeshanDevice implements Serializable {

    private static final MyLogger LOG = new MyLogger(LeshanDevice.class);

    private String deviceId;           //  Identificatore del dispositivo per Google
    private Registration registration; //  Identificatore del dispositivo per Leshan
    private String type;               //  tipo del dispositivo
    private HashMap<Integer, Object> deviceResource;  //  albero di ricerca che contiene le risorse del dispositivo
    //  lista di risorse obbligatorie per definire un dispositivo
    private LeshanResource[] mandatoryObject = {new LeshanResource("/1/0/0"), new LeshanResource("/3/0/0"), new LeshanResource("/3/0/1"), new LeshanResource("/3/0/17"), new LeshanResource("/3/0/18"), new LeshanResource("/3/0/19")};
    private transient LwM2mServer server;     //  server Leshan a cui connettersi
    private DeviceInfo deviceInfo;  //  informazioni generiche del dispositivo


    LeshanDevice(Registration r, LwM2mServer s) throws Exception {

		final String DEVICE_ID = "/1/0/0";
		final String DEVICE_TYPE = "/3/0/17";
		final String DEVICE_HW_VERSION = "/3/0/18";
		final String DEVICE_SW_VERSION = "/3/0/19";
		final String DEVICE_MODEL_NUMBER = "/3/0/1";
		final String MANUFACTURER = "/3/0/0";
		
        registration = r;
        server = s;
        deviceResource = new HashMap<>();

        discoverObjects();  //  ricavo tutte le istanze degli oggetti allocati del dispositivo

        haveMandatoryObjects(mandatoryObject);  //  controllo siano presenti gli oggetti obbligatori

        //  si creano le informazioni generiche del dispositivo(usate poi anche da SYNC)
        deviceId = Long.toString((Long) readResource(new LeshanResource(DEVICE_ID)));
        type = (String) readResource(new LeshanResource(DEVICE_TYPE));

        deviceInfo = new DeviceInfo((String) readResource(new LeshanResource(MANUFACTURER)), (String) readResource(new LeshanResource(DEVICE_MODEL_NUMBER)), (String) readResource(new LeshanResource(DEVICE_SW_VERSION)), (String) readResource(new LeshanResource(DEVICE_HW_VERSION)));
        LOG.info("leshan's device correctly created " + deviceId);

    }

    ////  METODI PER COMUNICARE CON I DISPOSITIVI


    //  metodo per prelevare tutte le istanze degli oggetti disponibili nel dispositivo

    private void discoverObjects() throws InterruptedException {

        long TIMEOUT = 5000; // ms
        DiscoverRequest request;

        Link[] list = registration.getObjectLinks();
        Link[] value;
        HashMap<Integer, Object> instance;
        List<Integer> values;

        //  estraggo tutti gli oggetti definiti dal descrittore Leshan del dispositivo
        for (Link resource : list) {
            //  id[0] = OID , id[1] = istanza
            if( resource.getUrl().length() < 2 ) continue;

            String[] id = resource.getUrl().replaceFirst("/", "").split("/");
            //  organizzo un albero di ricerca per l'oggetto allocando un nodo per le istanze
            //  ( RADICE(OID), NODI( INSTANZE) , FOGLIE( RISORSE)
            if (deviceResource.containsKey(Integer.parseInt(id[0])))
                instance = (HashMap<Integer, Object>) deviceResource.get(Integer.parseInt(id[0]));
            else
                instance = new HashMap<>();

            values = new ArrayList<>();
            request = new DiscoverRequest(resource.getUrl());  // prelevo le istanze dell'oggetto
            value = server.send(registration, request, TIMEOUT).getObjectLinks();
            //  prelevo tutte le risorse disponibili
            for (int b = 1; b < value.length; b++)
                values.add(Integer.parseInt(value[b].getUrl().replaceFirst(resource.getUrl() + "/", "")));
            //  inserisco le risorse nel nodo istanza

            instance.put(Integer.parseInt(id[1]), values);
            if (!deviceResource.containsKey(Integer.parseInt(id[0])))
                deviceResource.put(Integer.parseInt(id[0]), instance);
        }

    }


    //  permette la lettura di una risorsa sul dispositivo

    public Object readResource(LeshanResource resource) {

        long TIMEOUT = 5000; // ms
        ContentFormat contentFormat = null;

        ReadRequest request = new ReadRequest(contentFormat, resource.toString());
        ReadResponse cResponse;

        try {

            cResponse = server.send(registration, request, TIMEOUT);

        } catch (InterruptedException ie) {

            LOG.error("error trying to read resource " + resource.toString() + "\n" + ie.getMessage());
            return null;
        }

        if (cResponse.isValid() && cResponse.isSuccess())
            return ((LwM2mSingleResource) cResponse.getContent()).getValue();

        return null;
    }


    //  metodo per la scrittura di una risorsa del dispositivo

    public boolean writeResource( LeshanResource resource, Object value) {

        long TIMEOUT = 5000; // ms
        ContentFormat contentFormat = null;
        WriteResponse cResponse;
        LwM2mNode node = null;
        Integer resTarget = resource.getRID();


        if (value instanceof Boolean)
            node = LwM2mSingleResource.newBooleanResource(resTarget, (Boolean) value);
        else if (value instanceof Integer)
            node = LwM2mSingleResource.newIntegerResource(resTarget, (Integer) value);
        else if (value instanceof String)
            node = LwM2mSingleResource.newStringResource(resTarget, (String) value);
        else if (value instanceof Long || value instanceof Double)
            node = LwM2mSingleResource.newFloatResource(resTarget, (Float) value);


        WriteRequest request = new WriteRequest(WriteRequest.Mode.REPLACE, contentFormat, resource.toString(), node);
        try {

            cResponse = server.send(registration, request, TIMEOUT);

            return (cResponse.isValid() && cResponse.isSuccess());


        } catch (InterruptedException ie) {

            LOG.error("error while trying to read the resource " + resource.toString() + "\n" + ie.getMessage());
            return false;
        }
    }


    //  permette l'esecuzione di una risorsa del dispositivo

    public boolean execResource(LeshanResource res, String value) {

        long TIMEOUT = 5000; // ms
        ExecuteRequest request = new ExecuteRequest(res.toString(), value);
        ExecuteResponse cResponse;

        try {

            cResponse = server.send(registration, request, TIMEOUT);
            if (cResponse.isValid() && cResponse.isSuccess()) {
                LOG.info("object " + res.toString() + "of device " + deviceId + " correctly executed");
                return true;
            } else {
                LOG.info("error, can't exec object " + res.toString() + "of device " + deviceId);
                return false;
            }

        } catch (InterruptedException ie) {

            LOG.error("error while trying to exec resource " + res.toString() + "of device " + deviceId + "\n" + ie.getMessage());
            return false;
        }

    }


    ////  FUNZIONI GENERICHE
    public void addObservation(LeshanResource res) {

        long TIMEOUT = 5000; // ms
        ContentFormat contentFormat = null;
        ObserveRequest request = new ObserveRequest(contentFormat, res.toString());
        ObserveResponse cResponse;

        try {

            cResponse = server.send(registration, request, TIMEOUT);
            if (cResponse.isValid() && cResponse.isSuccess()) {
                LOG.info("object " + res.toString() + "of device " + deviceId + " correctly put on observation");

            } else {
                LOG.info("error, can't put object " + res.toString() + "of device " + deviceId + " on observation");

            }
        } catch (InterruptedException ie) {

            LOG.error("error while trying to observe resource " + res.toString() + "of device " + deviceId + "\n" + ie.getMessage());

        }

    }


    public void putObservation(LeshanResource id, Object value) {

        switch (id.getOID()) {
            case 1:

                deviceId = (String) value;
                break;

            case 3:

                switch (id.getRID()) {

                    case 0:

                        deviceInfo.setManufacturer((String) value);
                        break;

                    case 1:

                        deviceInfo.setModel((String) value);
                        break;

                    case 17:

                        type = (String) value;
                        break;

                    case 18:

                        deviceInfo.setHwVersion((String) value);
                        break;

                    case 19:

                        deviceInfo.setSwVersion((String) value);
                        break;

                }
        }
    }

    public void startObservation() {

        for ( LeshanResource resource : mandatoryObject)

            addObservation( resource );


    }


    ////  FUNZIONI GENERICHE


    //  verifica la presenza degli oggetti obbligatori per definire il dispositivo

    private void haveMandatoryObjects(LeshanResource[] list) throws Exception {

        HashMap<Integer, List<Integer>> instances;
        Iterator<Integer> instanceValues;
        boolean control = false;
        List<Integer> values;

        for (LeshanResource r : list) {
            if (!deviceResource.containsKey(r.getOID()))
                throw new Exception("The device MUST have the <" + r.getOID() + "/*/" + r.getRID() + "> object to be converted");
            if (r.getRID() == -1) return;
            instances = (HashMap<Integer, List<Integer>>) deviceResource.get(r.getOID());
            instanceValues = instances.keySet().iterator();
            while (instanceValues.hasNext()) {
                values = instances.get(instanceValues.next());
                if (values.contains(r.getRID()))
                    control = true;
                //  inserimento delle varie instanze nella struttura utilizzata alla fine
            }
            if (!control)
                throw new Exception("The device MUST have the <" + r.getOID() + "/*/" + r.getRID() + "> object to be converted");

        }


    }


    //  fornisce tutte le istanze di un determinato oggetto OID/RID
    //  le mie azioni sono solo di esempio, per una trattazione completa bisognerà gestire anche le
    //  singole istanze delle risorse, con questa funzione, partendo da una risorsa fornisce tutte le
    //  istanze disponibili così da poter operare su di esse.

    public List<LeshanResource> getInstances( LeshanResource res) {

        if (!deviceResource.containsKey(res.getOID())) return null;

        List<LeshanResource> retObj = new ArrayList();
        Integer instance;

        Iterator<Integer> instances = ((HashMap<Integer, List<Integer>>) deviceResource.get(res.getOID())).keySet().iterator();
        while (instances.hasNext()) {
            instance = instances.next();
            if (((HashMap<Integer, List<Integer>>) deviceResource.get(res.getOID())).get(instance).contains(res.getRID()))
                retObj.add(new LeshanResource(res.getOID(), instance, res.getRID()));

        }
        return retObj;
    }


    //  fornisce le risorse utilizzate dal Trait

    public HashMap<Integer, Object> getTraitObjects(String trait) {

        List<LeshanResource> ret;
        Iterator<LeshanResource> i;
        HashMap<Integer, Object> retValues = new HashMap<>();
        Integer v;

        if (trait != null)
            if( haveObjects( LeshanGoogleConverter.getStates(trait)) ) {  //  verifico di avere gli oggetti del trait
                ret = getLeshanObjects( LeshanGoogleConverter.getStates(trait) );  //  prelevo gli oggetti usati dal trait

                i = ret.iterator();
                while (i.hasNext()) {

                    v = i.next().getOID();
                    retValues.put(v, deviceResource.get(v));

                }

                return retValues;
            }

        return null;

    }


    //  fornisce le risorse obbligatorie del dispositivo

    LeshanResource[] getObjectsID() {

        LeshanResource ret[] = new LeshanResource[ mandatoryObject.length ];
        for (int a = 0; a < ret.length; a++)
            ret[a] = new LeshanResource(mandatoryObject[a].getOID(), 0, mandatoryObject[a].getRID());
        return ret;
    }


    //  verifica siano presenti le risorse richieste ad implementare una action

    boolean haveObjects( LeshanResourceConvertion[] r ) {

        boolean control;

        for( LeshanResourceConvertion resource : r ) {
            control = false;

            ResourceConvertion[] lResources = resource.getAllResources();
            for ( ResourceConvertion lResource : lResources) {

                if( haveObject( lResource )) {
                    control = true;
                    break;
                }

            }
            if ( !control ) return false;
        }
        return true;

    }


    //  FUNZIONE DI APPOGGIO: usata per verificare la presenza di una risorsa

    private boolean haveObject( ResourceConvertion r ) {

        if ( !deviceResource.containsKey(r.getOID()) ) return false;

        HashMap<Integer, List<Integer>> instances = (HashMap<Integer, List<Integer>>) deviceResource.get(r.getOID());
        Iterator<Integer> instance = instances.keySet().iterator();

        while (instance.hasNext())
            if (instances.get(instance.next()).contains( r.getRID()) ) return true;

        return false;

    }


    //  funzione per prelevare tutte le risorse utilizzate dal dispositivo
    //  necessaria a Device per conoscere le risorse utilizzate dai Trait e inoltrargli le Notify
    //  opportune( attraverso observeTrait )
    List<LeshanResource> getLeshanObjects( LeshanResourceConvertion[] r ) {

        List<LeshanResource> ret = new ArrayList<>();
        int val;

        for (LeshanResourceConvertion resource : r) {
            ResourceConvertion[] lResources = resource.getAllResources();
            for ( ResourceConvertion lResource : lResources) {
                if (deviceResource.containsKey(lResource.getOID())) {
                    HashMap<Integer, List<Integer>> instances = (HashMap<Integer, List<Integer>>) deviceResource.get(lResource.getOID());
                    Iterator<Integer> instance = instances.keySet().iterator();
                    while (instance.hasNext()) {
                        val = instance.next();
                        if (instances.get(val).contains(lResource.getRID()))
                            ret.add(new LeshanResource(lResource.getOID(), val, lResource.getRID()));
                    }
                }
            }
        }

        return ret;
    }



    DeviceInfo getDeviceInfo() {

        return deviceInfo;
    }

    public String getId() {
        return deviceId;
    }

    public String getType() {
        return type;
    }

    public Registration getRegistration() {
        return registration;
    }
}