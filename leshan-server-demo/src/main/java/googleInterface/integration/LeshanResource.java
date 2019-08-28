package googleInterface.integration;

import java.io.Serializable;


//  classe utilizzata per definire i percorsi delle risorse. Le operazioni per operare sulle risorse
//  richiedono LeshanResource per definire il target

public class LeshanResource implements Serializable {

    private String id;
    private int OID,IID = -1,RID = -1;


    public LeshanResource( String resource ){

        id = resource.replaceFirst( "/" , "");
        String[] r = id.split("/");
        switch( r.length ){

            case 3: RID = Integer.parseInt( r[2]);
            case 2: IID = Integer.parseInt( r[1]);
            case 1: OID = Integer.parseInt( r[0]);

        }

    }


    public LeshanResource( int o , int i , int r ){

        OID = o;
        IID = i;
        RID = r;

        if( r == -1 && i == -1 ) {
            id = String.valueOf(o);
            return;
        }

        if( r == -1 ){
            id = o + "/" + i;
            return;
        }

        id = o + "/" + i + "/" + r;

    }


    //  utilizzata per ricavare il link da fornire ai metodi Leshan

    public String toString(){ return id; }


    //  fornisce la risorsa dell'oggetto

    public Integer getRID(){
        if( RID != -1 ) return RID;
        return null;
    }


    //  fornisce l'istanza dell'oggetto

    public Integer getInstance(){
        if( IID != -1 ) return IID;
        return null;
    }


    //  fornisce l'ID dell'oggetto

    public Integer getOID(){
        if( OID != -1 ) return OID;
        return null;
    }
}
