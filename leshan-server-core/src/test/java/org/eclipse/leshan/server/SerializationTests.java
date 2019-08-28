/*******************************************************************************
 * Copyright (c) 2019 Sierra Wireless and others.
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
package org.eclipse.leshan.server;

import static org.junit.Assert.fail;

import java.util.Map;

import org.eclipse.leshan.server.bootstrap.BootstrapConfig;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.security.SecurityInfo;
import org.junit.Test;

public class SerializationTests {

    @Test
    public void ensure_Registration_is_serializable() {
        // TODO we excludes "org.eclipse.leshan.Link.attributes" because we can not be sure it is serializable in a
        // reliable way.
        assertIsSerializable(Registration.class, "org.eclipse.leshan.Link.attributes");
    }

    @Test
    public void ensure_SecurityInfo_is_serializable() {
        assertIsSerializable(SecurityInfo.class);
    }

    @Test
    public void ensure_BootstrapConfig_is_serializable() {
        assertIsSerializable(BootstrapConfig.class);
    }

    private static void assertIsSerializable(Class<?> clazz, String... excludes) {
        Map<Object, String> results = SerializationUtil.isSerializable(clazz, excludes);

        if (!results.isEmpty()) {
            StringBuilder issues = new StringBuilder();
            for (String issue : results.values()) {
                issues.append("\n");
                issues.append(issue);
            }
            fail(issues.toString());
        }
    }
}
