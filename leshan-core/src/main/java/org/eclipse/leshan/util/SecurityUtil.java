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
package org.eclipse.leshan.util;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.eclipse.leshan.core.credentials.CredentialsReader;

public class SecurityUtil {

    public static CredentialsReader<PrivateKey> privateKey = new CredentialsReader<PrivateKey>() {
        @Override
        public PrivateKey decode(byte[] bytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(spec);
        }
    };

    public static CredentialsReader<PublicKey> publicKey = new CredentialsReader<PublicKey>() {
        @Override
        public PublicKey decode(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePublic(spec);
        }
    };

    public static CredentialsReader<X509Certificate> certificate = new CredentialsReader<X509Certificate>() {
        @Override
        public X509Certificate decode(InputStream inputStream) throws CertificateException {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate certificate = cf.generateCertificate(inputStream);
            if (certificate instanceof X509Certificate) {
                return (X509Certificate) certificate;
            }
            throw new CertificateException(
                    String.format("%s certificate format is not supported, Only X.509 certificate is supported",
                            certificate.getType()));
        }
    };
}
