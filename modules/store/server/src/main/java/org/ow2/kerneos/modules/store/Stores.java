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

package org.ow2.kerneos.modules.store;

import org.ow2.kerneos.modules.store.impl.StoreImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Stores {

    private Collection<StoreImpl> stores;

    public Stores() {
        stores = new ArrayList<StoreImpl>();
    }

    public Collection<StoreImpl> getStores() {
        return stores;
    }

    public void setStores(Collection<StoreImpl> stores) {
        this.stores = stores;
    }

    public boolean deleteStore(String id) {
        for (StoreImpl store : stores) {
            if (store.getId().equals(id)) {
                stores.remove(store);
                return true;
            }
        }
        return false;
    }

    public StoreImpl getStoreById(String id) {
        for (StoreImpl store : stores) {
            if (store.getId().equals(id)) {
                return store;
            }
        }
        return null;
    }

    public void addStore(StoreImpl store) {
        if (store.getId() == null || store.getId().equals("")) {
            store.setId(UUID.randomUUID().toString());
        }
        this.stores.add(store);
    }
}
