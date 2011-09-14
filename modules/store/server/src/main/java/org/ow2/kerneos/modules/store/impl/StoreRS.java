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

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.ow2.kerneos.modules.store.IStoreRS;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.ow2.kerneosstore.api.Category;
import org.ow2.kerneosstore.api.ModuleIdsWrapper;
import org.ow2.kerneosstore.api.ModuleVersion;
import org.ow2.kerneosstore.api.Repository;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

public class StoreRS implements IStoreRS {
    private String url = "http://localhost:9000/store";

    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(StoreRS.class);

    @Override
    public org.ow2.kerneosstore.api.Store getStore() {
        Client client = new Client();

        logger.debug("Call API REST " + url + "/info");

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

        logger.debug("Call API REST " + url + "/module/" + id + "/image");

        WebResource webResource = client.resource(url + "/module/" + id + "/image");
        return webResource.get(byte[].class);
    }

    @Override
    public ModuleVersion getModuleVersion(String id) {
        Client client = new Client();

        logger.debug("Call API REST " + url + "/module/" + id);

        WebResource webResource = client.resource(url + "/module/" + id);
        return webResource.get(ModuleImpl.class);
    }

    @Override
    public Collection<ModuleVersion> searchModules(String filter, String field, String order,
                                                   Integer itemByPage, Integer page) {
        Client client = new Client();

        MultivaluedMap queryParams = new MultivaluedMapImpl();

        if (field != null) {
            queryParams.add("field", field);
        }

        if (order != null) {
            queryParams.add("order", order);
        }

        if (itemByPage != null) {
            queryParams.add("itemByPage", itemByPage);
        }

        if (page != null) {
            queryParams.add("page", page);
        }

        String[] filterSplit = filter.split(" ");

        String filterURL = "";

        if (filterSplit != null && filterSplit[0] != null) {
            int i = 0;

            while (i < filterSplit.length) {
                if (!filterSplit[i].equals("")) {
                    filterURL = filterSplit[i];
                    break;
                }
                i++;
            }
        }

        try {
            //test if the url is good
            String resourceURL = url + "/modules/" + filterURL;
            URI uri = new URI(resourceURL);

            logger.debug("Call API REST " + resourceURL);

            WebResource webResource = client.resource(resourceURL);

            GenericType<Collection<ModuleImpl>> genericModules =
                    new GenericType<Collection<ModuleImpl>>() {
                    };


            Collection modulesResult =
                    webResource.queryParams(queryParams).get(genericModules);

            return modulesResult;
        } catch (URISyntaxException ex) {

            logger.error(ex.getStackTrace());

            //TODO create own exception with a good message
            return null;
        }
    }

    @Override
    public ModuleIdsWrapper searchModulesGetIds(String filter) {
        Client client = new Client();
        try {
            //test if the url is good
            String resourceUrl = url + "/modules/" + filter + "/ids";
            URI uri = new URI(resourceUrl);

            logger.debug("Call API REST " + resourceUrl);

            WebResource webResource = client.resource(resourceUrl);

            return webResource.get(ModuleIdsWrapperImpl.class);

        } catch (URISyntaxException ex) {
            logger.error(ex.getStackTrace());
            //TODO create own exception with a good message
            return null;
        }
    }

    @Override
    public String searchModulesResultsNumber(String filter) {
        Client client = new Client();

        try {
            //test if the url is good
            URI uri = new URI(url + "/modules/" + filter + "/number");

            logger.debug("Call API REST " + url + "/modules/" + filter + "/number");

            WebResource webResource = client.resource(url + "/modules/" + filter + "number");

            return webResource.get(String.class);
        } catch (URISyntaxException ex) {
            logger.error(ex.getStackTrace());
            //TODO create own exception with a good message
            return "0";
        }
    }

    @Override
    public Collection<ModuleVersion> searchModulesByCategory(String id, String field, String order,
                                                             Integer itemByPage, Integer page) {
        Client client = new Client();

        MultivaluedMap queryParams = new MultivaluedMapImpl();

        if (field != null) {
            queryParams.add("field", field);
        }

        if (order != null) {
            queryParams.add("order", order);
        }

        if (itemByPage != null) {
            queryParams.add("itemByPage", itemByPage);
        }

        if (page != null) {
            queryParams.add("page", page);
        }

        try {
            //test if the url is good
            URI uri = new URI(url + "/category/" + id + "/modules");

            logger.debug("Call API REST " + url + "/category/" + id + "/modules");

            WebResource webResource = client.resource(url + "/category/" + id + "/modules");

            GenericType<Collection<ModuleImpl>> genericModules =
                    new GenericType<Collection<ModuleImpl>>() {
                    };


            Collection modulesResult =
                    webResource.queryParams(queryParams).get(genericModules);

            return modulesResult;
        } catch (URISyntaxException ex) {
            logger.error(ex.getStackTrace());
            //TODO create own exception with a good message
            return null;
        }
    }

    @Override
    public ModuleIdsWrapper searchModulesByCategoryGetIds(String id) {
        Client client = new Client();
        try {
            //test if the url is good
            String resourceUrl = url + "/category/" + id + "/modules/ids";
            URI uri = new URI(resourceUrl);

            logger.debug("Call API REST " + resourceUrl);

            WebResource webResource = client.resource(resourceUrl);

            return webResource.get(ModuleIdsWrapperImpl.class);

        } catch (URISyntaxException ex) {
            logger.error(ex.getStackTrace());
            //TODO create own exception with a good message
            return null;
        }
    }

    @Override
    public String searchModulesByCategoryResultsNumber(String id) {
        Client client = new Client();

        try {
            //test if the url is good
            String resourceUrl = url + "/category/" + id + "/modules/number";
            URI uri = new URI(resourceUrl);

            logger.debug("Call API REST " + resourceUrl);

            WebResource webResource = client.resource(resourceUrl);

            return webResource.get(String.class);
        } catch (URISyntaxException ex) {
            logger.error(ex.getStackTrace());
            //TODO create own exception with a good message
            return "0";
        }
    }

    @Override
    public Collection<Category> getCategories() {
        Client client = new Client();

        logger.debug("Call API REST " + url + "/categories");

        WebResource webResource = client.resource(url + "/categories");

        GenericType<Collection<CategoryImpl>> genericCategories =
                new GenericType<Collection<CategoryImpl>>() {
                };

        Collection categoriesResult = webResource.get(genericCategories);

        return categoriesResult;
    }

    @Override
    public Category getCategory(String id) {
        Client client = new Client();

        logger.debug("Call API REST " + url + "/category/" + id);

        WebResource webResource = client.resource(url + "/category/" + id);
        return webResource.get(CategoryImpl.class);
    }

    @Override
    public byte[] downloadModuleVersion(String id) {
        Client client = new Client();

        logger.debug("Call API REST " + url + "/module/" + id + "/download");

        WebResource webResource = client.resource(url + "/module/" + id + "/download");
        return webResource.get(byte[].class);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }
}
