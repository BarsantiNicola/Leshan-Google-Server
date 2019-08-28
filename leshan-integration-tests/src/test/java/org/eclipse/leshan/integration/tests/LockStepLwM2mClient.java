/*******************************************************************************
 * Copyright (c) 2017 Sierra Wireless and others.
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
package org.eclipse.leshan.integration.tests;

import java.net.InetSocketAddress;
import java.util.Random;

import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.serialization.UdpDataSerializer;
import org.eclipse.californium.core.test.lockstep.LockstepEndpoint;
import org.eclipse.californium.elements.RawData;
import org.eclipse.leshan.client.californium.impl.CoapRequestBuilder;
import org.eclipse.leshan.core.request.UplinkRequest;
import org.eclipse.leshan.core.response.LwM2mResponse;

public class LockStepLwM2mClient extends LockstepEndpoint {

    private static final Random r = new Random();
    private InetSocketAddress destination;

    public LockStepLwM2mClient(final InetSocketAddress destination) {
        super(destination);
        this.destination = destination;
    }

    public void sendLwM2mRequest(UplinkRequest<? extends LwM2mResponse> lwm2mReq) {
        // create CoAP request
        CoapRequestBuilder coapRequestBuilder = new CoapRequestBuilder(destination);
        lwm2mReq.accept(coapRequestBuilder);
        Request coapReq = coapRequestBuilder.getRequest();
        byte[] token = new byte[8];
        r.nextBytes(token);
        coapReq.setToken(token);

        // serialize request
        UdpDataSerializer serializer = new UdpDataSerializer();
        RawData raw = serializer.serializeRequest(coapReq);

        // send it
        super.send(raw);
    }
}
