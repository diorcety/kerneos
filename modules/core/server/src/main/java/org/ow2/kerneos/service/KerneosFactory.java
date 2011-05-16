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

package org.ow2.kerneos.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set the kerneos Factory Service.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KerneosFactory {
    /**
     * Differents scope of a kerneos factory.
     */
    enum SCOPE {
        /**
         * Create an instance by request.
         */
        REQUEST("request"),
        /**
         * Create an instance http session.
         */
        SESSION("session"),
        /**
         * Create on instance.
         */
        APPLICATION("application");

        private final String value;

        /**
         * Constructor.
         *
         * @param value the string corresponding.
         */
        SCOPE(final String value) {
            this.value = value;
        }

        /**
         * Convert the type to String.
         *
         * @return the string corresponding.
         */
        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * The scope used with this factory.
     */
    SCOPE scope();
}
