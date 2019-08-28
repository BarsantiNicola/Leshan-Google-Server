package googleInterface.integration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//  classe POJO utilizzata insieme a LeshanResourceConvertion e ResourceConvertion per il caricamento
//  dei dati contenuti in convertion/traits.json

public class TraitConvertion {

    private String[] commands;
    private LeshanResourceConvertion[] resources;

    public void setResources( LeshanResourceConvertion[] r ){ resources = r; }

    public void setCommands( String[] r ){ commands = r; }

    public LeshanResourceConvertion[] getResources(){ return resources; }

    public String[] getCommands(){ return commands; }

    public String[] getStates(){

        List<String> states = new ArrayList<>();
        String[] retStates;
        for( LeshanResourceConvertion resource: resources )
            if( resource.getState() != null && resource.getState().length() >0 )
                states.add( resource.getState());

        Iterator<String> it = states.iterator();
        retStates = new String[states.size()];
        for( int a = 0; it.hasNext(); a++ )
            retStates[a] = it.next();

        return retStates;
    }

}
