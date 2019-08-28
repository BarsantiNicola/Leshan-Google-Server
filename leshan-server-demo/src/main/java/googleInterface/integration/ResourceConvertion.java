package googleInterface.integration;


//  classe POJO utilizzata insieme a LeshanResourceConvertion e TraitConvertion per il caricamento
//  dei dati contenuti in convertion/traits.json

public class ResourceConvertion {

    int OID,RID;

    ResourceConvertion( int O, int R ){
        OID = O;
        RID = R;
    }

    public void setOID( int o ){ OID = o; }

    public void setRID( int r ){ RID = r; }

    public int getRID(){ return RID; }

    public int getOID(){ return OID; }
}
