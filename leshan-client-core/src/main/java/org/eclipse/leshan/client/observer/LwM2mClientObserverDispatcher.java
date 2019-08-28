/*******************************************************************************
 * Copyright (c) 2016 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors: Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.client.observer;

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.servers.Server;

/**
 * A dispatcher for LwM2mClientObserver. It allow several observers on a LwM2mClient.
 *
 */
public class LwM2mClientObserverDispatcher implements LwM2mClientObserver {
    private CopyOnWriteArrayList<LwM2mClientObserver> observers = new CopyOnWriteArrayList<>();

    public void addObserver(LwM2mClientObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LwM2mClientObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void onBootstrapSuccess(Server bsserver) {
        for (LwM2mClientObserver observer : observers) {
            observer.onBootstrapSuccess(bsserver);
        }
    }

    @Override
    public void onBootstrapFailure(Server bsserver, ResponseCode responseCode, String errorMessage) {
        for (LwM2mClientObserver observer : observers) {
            observer.onBootstrapFailure(bsserver, responseCode, errorMessage);
        }
    }

    @Override
    public void onBootstrapTimeout(Server bsserver) {
        for (LwM2mClientObserver observer : observers) {
            observer.onBootstrapTimeout(bsserver);
        }
    }

    @Override
    public void onRegistrationSuccess(Server server, String registrationID) {
        for (LwM2mClientObserver observer : observers) {
            observer.onRegistrationSuccess(server, registrationID);
        }
    }

    @Override
    public void onRegistrationFailure(Server server, ResponseCode responseCode, String errorMessage) {
        for (LwM2mClientObserver observer : observers) {
            observer.onRegistrationFailure(server, responseCode, errorMessage);
        }
    }

    @Override
    public void onRegistrationTimeout(Server server) {
        for (LwM2mClientObserver observer : observers) {
            observer.onRegistrationTimeout(server);
        }
    }

    @Override
    public void onUpdateSuccess(Server server, String registrationID) {
        for (LwM2mClientObserver observer : observers) {
            observer.onUpdateSuccess(server, registrationID);
        }
    }

    @Override
    public void onUpdateFailure(Server server, ResponseCode responseCode, String errorMessage) {
        for (LwM2mClientObserver observer : observers) {
            observer.onUpdateFailure(server, responseCode, errorMessage);
        }
    }

    @Override
    public void onUpdateTimeout(Server server) {
        for (LwM2mClientObserver observer : observers) {
            observer.onUpdateTimeout(server);
        }
    }

    @Override
    public void onDeregistrationSuccess(Server server, String registrationID) {
        for (LwM2mClientObserver observer : observers) {
            observer.onDeregistrationSuccess(server, registrationID);
        }
    }

    @Override
    public void onDeregistrationFailure(Server server, ResponseCode responseCode, String errorMessage) {
        for (LwM2mClientObserver observer : observers) {
            observer.onDeregistrationFailure(server, responseCode, errorMessage);
        }
    }

    @Override
    public void onDeregistrationTimeout(Server server) {
        for (LwM2mClientObserver observer : observers) {
            observer.onDeregistrationTimeout(server);
        }
    }

}
