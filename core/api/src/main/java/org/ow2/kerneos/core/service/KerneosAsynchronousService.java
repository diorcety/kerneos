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
 * Interface corresponding to a declaration of asynchronous service.
 */
public interface KerneosAsynchronousService {
    String ID = "kerneos-service-id";
    String TYPE = "kerneos-asynchronous-type";


    /**
     * Different types of asynchronous service.
     */

    class Type {
        private Type() {

        }

        public static final String EVENTADMIN = "event-admin";
        public static final String JMS = "java-message-service";
    }
}
