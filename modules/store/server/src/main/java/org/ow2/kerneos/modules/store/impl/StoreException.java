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

package org.ow2.kerneos.modules.store.impl;

public class StoreException extends Exception {
    public final static String MODULE_NOT_INSTALLED = "MODULE_NOT_INSTALLED";
    public final static String MODULE_ALREADY_INSTALLED = "MODULE_ALREADY_INSTALLED";
    public final static String MODULE_NOT_FOUND = "MODULE_NOT_FOUND";
    public final static String MODULE_DOWNLOAD_ISSUE = "MODULE_DOWNLOAD_ISSUE";
    public final static String MODULE_INVALID = "MODULE_INVALID";

    public final static String CANT_INSTALL = "CANT_INSTALL";
    public final static String CANT_UNINSTALL = "CANT_UNINSTALL";
    public final static String INVALID_CONTEXT = "INVALID_CONTEXT";
    public final static String UNKNOWN = "UNKNOWN";

    public final static String BAD_STORE_URL = "BAD_STORE_URL";

    private String type;

    public StoreException() {
        this.type = UNKNOWN;
    }

    public StoreException(String type) {
        this.type = type;
    }

    public StoreException(String type, String message) {
        super(message);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
