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

    @Override
    public Map<Repository, String> getRepositoryEntries(String id) {
        return null;
    }

    @Override
    public byte[] getModuleVersionImage(String id) {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/module/" + id + "/image");
        return webResource.get(byte[].class);
    }

    @Override
    public ModuleVersion getModuleVersion(String id) {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/module/" + id);
        return webResource.get(ModuleImpl.class);
    }

    @Override
    public Collection<ModuleVersion> searchModules(String filter, String field, String order,
                                                             Integer itemByPage, Integer page) {
        Client client = new Client();

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("field", field);
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

    @Override
    public Collection<ModuleVersion> searchModulesByCategory(String id, String field, String order,
                                                                       Integer itemByPage, Integer page) {
         Client client = new Client();

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("field", field);
        queryParams.add("order", order);
        queryParams.add("itemByPage", itemByPage);
        queryParams.add("page", page);

         WebResource webResource = client.resource(url + "/category/"+id+"/modules");

        GenericType<Collection<ModuleImpl>> genericModules =
                new GenericType<Collection<ModuleImpl>>() {};


        Collection modulesResult =
                webResource.queryParams(queryParams).get(genericModules);

        return modulesResult;
    }

    @Override
    public Collection<Category> getCategories() {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/categories");

        GenericType<Collection<CategoryImpl>> genericCategories =
                new GenericType<Collection<CategoryImpl>>() {};

        Collection categoriesResult = webResource.get(genericCategories);

        return categoriesResult;
    }

    @Override
    public Category getCategory(String id) {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/category/" + id);
        return webResource.get(CategoryImpl.class);
    }

    @Override
    public byte[] downloadModule(String id) {
        Client client = new Client();

        WebResource webResource = client.resource(url + "/module/" + id + "/download");
        return webResource.get(byte[].class);
    }
}
