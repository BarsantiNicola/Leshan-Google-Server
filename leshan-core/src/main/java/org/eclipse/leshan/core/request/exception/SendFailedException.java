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
package org.eclipse.leshan.core.request.exception;

/**
 * Thrown to indicate that the request has failed to be sent.
 */
public class SendFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SendFailedException() {
    }

    public SendFailedException(String m) {
        super(m);
    }

    public SendFailedException(String m, Object... args) {
        super(String.format(m, args));
    }

    public SendFailedException(Throwable e) {
        super(e);
    }

    public SendFailedException(String m, Throwable e) {
        super(m, e);
    }

    public SendFailedException(Throwable e, String m, Object... args) {
        super(String.format(m, args), e);
    }
}
