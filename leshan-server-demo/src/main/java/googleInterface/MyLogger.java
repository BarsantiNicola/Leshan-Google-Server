package googleInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

//  classe utilizzata per l'output delle classi dell'interfaccia.
//  applicando un colore all'output permette di distinguere i messaggi della nostra interfaccia
//  da quelli del server Leshan

public class MyLogger implements Serializable {

    private final Logger LOG;

    public MyLogger( Class c ){

        LOG = LoggerFactory.getLogger( c );

    }

    public  synchronized  void info( String info ){

            LOG.info( (char) 27 + "[34m" + info + (char) 27 + "[39;49m" );

    }

    public synchronized void error( String info ){

        LOG.error( (char) 27 + "[34m" + info + (char) 27 + "[39;49m" );

    }

    public synchronized void debug( String info ){

        LOG.debug( (char) 27 + "[34m" + info + (char) 27 + "[39;49m" );

    }



}

