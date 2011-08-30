/**
 * Kerneos
 * Copyright (C) 2009-2011 Bull S.A.S.
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

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.ow2.kerneos.modules.store.IStoreRS;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.ow2.kerneosstore.api.Category;
import org.ow2.kerneosstore.api.ModuleVersion;
import org.ow2.kerneosstore.api.Repository;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.Map;

public class StoreRS implements IStoreRS {
    private String url = "http://localhost:9000/store";

    public org.ow2.kerneosstore.api.Store getStore() {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/info");
        return webResource.get(StoreImpl.class);
    }

    //TODO delete this methode from the interface
    public Map<Repository, String> getRepositoryEntries(Long moduleId) {
        return null;
    }

    public byte[] getModuleVersionImage(Long id) {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/module/" + id + "/image");
        return webResource.get(byte[].class);
    }

    public ModuleVersion getModuleVersion(Long id) {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/module/" + id);
        return webResource.get(ModuleImpl.class);
    }

    public Collection<ModuleVersion> getModulesByName(String filter, String order, Integer itemByPage, Integer page) {
        Client client = new Client();

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("order", order);
        queryParams.add("itemByPage", itemByPage);
        queryParams.add("page", page);

         WebResource webResource = client.resource(url + "/modules/name/" + filter);

        GenericType<Collection<ModuleImpl>> genericModules =
                new GenericType<Collection<ModuleImpl>>() {};


        Collection modulesResult =
                webResource.queryParams(queryParams).get(genericModules);

        return modulesResult;
    }

    public Collection<Category> getCategories() {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/categories");

        GenericType<Collection<CategoryImpl>> genericCategories =
                new GenericType<Collection<CategoryImpl>>() {};

        Collection categoriesResult = webResource.get(genericCategories);

        return categoriesResult;
    }

    public Category getCategory(Long id) {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/category/" + id);
        return webResource.get(CategoryImpl.class);
    }

    public byte[] downloadModule(Long id) {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/module/" + id + "/download");
        return webResource.get(byte[].class);
    }
}
