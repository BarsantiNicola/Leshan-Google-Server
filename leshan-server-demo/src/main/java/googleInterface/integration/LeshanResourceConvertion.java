package googleInterface.integration;


//  classe POJO utilizzata insieme a TraitConvertion e ResourceConvertion per il caricamento
//  dei dati contenuti in convertion/traits.json

public class LeshanResourceConvertion {

    private int OID,RID;
    private String state;
    private ResourceConvertion[] alias;

    public void setState( String t ){ state = t; }

    public void setAlias( ResourceConvertion[] a ){ alias = a; }

    public void setOID( int o ){ OID = o; }

    public void setRID( int r ){ RID = r; }

    public String getState(){ return state; }

    public ResourceConvertion[] getAlias(){ return alias; }

    public int getOID(){ return OID; }

    public int getRID(){ return RID; }


    // fornisce una lista di risorse utilizzabili per implementare un azione( basta che il dispositivo ne abbia una )

    public ResourceConvertion[] getAllResources(){

        ResourceConvertion[] ret = new ResourceConvertion[ alias.length+1 ];
        ret[0] = new ResourceConvertion( OID , RID );
        for( int a = 0; a<alias.length; a++ )
            ret[a+1] = alias[a];

        return ret;
    }

}
