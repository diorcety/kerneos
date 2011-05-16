/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
 * Contact: jasmine@ow2.org
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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */

package org.ow2.jasmine.kerneos.service.impl;

import org.osgi.service.http.HttpContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The specific http context used by Kerneos.
 */
public class KerneosHttpContext implements HttpContext {
    private static final String PREFIX = "bundle:/";
    private static final String PREFIX2 = "bundle://";

    /**
     * Return the Mine corresponding to the url.
     *
     * @param url is the string corresponding to the url request.
     * @return the Mine string.
     */
    public String getMimeType(final String url) {
        return null;
    }

    /**
     * Get the URL of the resource corresponding to a URL string.
     *
     * @param name url is the string corresponding to the url request.
     * @return the URL type which permits to acced to the resource.
     */
    public URL getResource(final String name) {
        if (name.startsWith(PREFIX)) {
            try {
                // Fix Jetty bug
                if (!name.startsWith(PREFIX2)) {
                    return new URL("bundle://" + name.substring(PREFIX.length()));
                } else {
                    return new URL(name);
                }
            } catch (MalformedURLException e) {
                return null;
            }
        } else {
            return this.getClass().getClassLoader().getResource(name);
        }
    }

    /**
     * Called for allow or deny an access.
     *
     * @param request  is the object containing the request information.
     * @param response is the object containing the response information.
     * @return return always true.
     * @throws IOException neven happen.
     */
    public boolean handleSecurity(final HttpServletRequest request,
                                  final HttpServletResponse response) throws IOException {
        return true;
    }
}
