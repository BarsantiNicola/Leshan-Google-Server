/*******************************************************************************
 * Copyright (c) 2018 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.core.californium;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.leshan.core.response.LwM2mResponse;

public abstract class SyncRequestObserver<T extends LwM2mResponse> extends CoapSyncRequestObserver {

    public SyncRequestObserver(Request coapRequest, long timeout) {
        super(coapRequest, timeout);
    }

    public T waitForResponse() throws InterruptedException {
        Response coapResponse = waitForCoapResponse();
        if (coapResponse != null) {
            return buildResponse(coapResponse);
        } else {
            return null;
        }
    }

    protected abstract T buildResponse(Response coapResponse);
}
