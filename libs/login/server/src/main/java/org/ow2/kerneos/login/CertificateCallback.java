/**
 * JOnAS: Java(TM) Open Application Server
 * Copyright (C) 1999-2007 Bull S.A.S.
 * Contact: jonas-team@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Initial developer: Alexandre Thaveau & Marc-Antoine Bourgeot
 * --------------------------------------------------------------------------
 * $Id: CertificateCallback.java 15428 2008-10-07 11:20:29Z sauthieg $
 * --------------------------------------------------------------------------
 */
package org.ow2.kerneos.login;

import javax.security.auth.callback.Callback;
import java.io.Serializable;
import java.security.cert.Certificate;

/**
 * Defines a callback which is use to store a certificate.
 * @author Alexandre Thaveau (initial developer)
 * @author Marc-Antoine Bourgeot (initial developer)
 */
public class CertificateCallback implements Callback, Serializable {

    /**
     * Certificate to use.
     */
    private Certificate userCertificate = null;

    /**
     * Gets the user certificate.
     * @return the user certificate
     */
    public Certificate getUserCertificate() {
        return userCertificate;
    }

    /**
     * Set the certificate.
     * @param certificate certificate to set
     */
    public void setUserCertificate(final Certificate certificate) {
        userCertificate = certificate;
    }

}
