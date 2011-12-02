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

package org.ow2.kerneos.core.service;


/**
 * Interface corresponding to a OSGi service used like a factory.
 *
 * @param <T> is the class returned by the factory.
 */
public interface KerneosFactoryService<T> {
    public final static String ID = "kerneos-service-id";
    public final static String SCOPE = "kerneos-factory-scope";

    /**
     * Create a new instance of the service.
     *
     * @return the new instance.
     */
    T newInstance();


    /**
     * Differents scope of a kerneos factory.
     */
    public final static class Scope {
        /**
         * Create an instance by request.
         */
        public final static String REQUEST = "request";
        /**
         * Create an instance http session.
         */
        public final static String SESSION = "session";

        /**
         * Create on instance.
         */
        public final static String APPLICATION = "application";
    }
}
