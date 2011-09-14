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

package org.ow2.kerneos.modules.store.services;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ow2.kerneos.core.service.KerneosService;
import org.ow2.kerneos.core.service.KerneosSimpleService;
import org.ow2.kerneos.modules.store.IStoreRS;
import org.ow2.kerneos.modules.store.IStoreService;
import org.ow2.kerneos.modules.store.Stores;
import org.ow2.kerneos.modules.store.config.ModuleBundle;
import org.ow2.kerneos.modules.store.impl.*;
import org.ow2.kerneos.modules.store.util.Base64;
import org.ow2.kerneosstore.api.ModuleVersion;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.xml.bind.JAXBContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Component
@Instantiate
@Provides

@KerneosService(id = "store_service")

public class StoreService implements KerneosSimpleService, IStoreService {
    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(StoreService.class);
    private static String BUNDLE_ID_KEY = "bundle_id";
    private static String MODULE_ID_KEY = "module_id";
    private static String MODULE_KEY = "module";

    private IStoreRS storeRS;
    private BundleContext bundleContext;
    private String installDirectoryString = "/tmp/store/bundles/";
    private File installDirectory;

    private JAXBContext jaxbContext;

    @Requires
    private ConfigurationAdmin configAdmin;

    private Stores stores;

    private StoreService(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;

        // Create installed module repository
        installDirectory = new File(installDirectoryString);
        installDirectory.mkdirs();

        // JAXB context
        jaxbContext = JAXBContext.newInstance(ModuleImpl.class);
    }

    /**
     * Start
     */
    @Validate
    private void start() {
        logger.info("Start store service");

        logger.info("Calling Store REST API");

        storeRS = new StoreRS();

        stores = new Stores();

        StoreImpl result = (StoreImpl) storeRS.getStore();
        stores.addStore(result);
    }

    /**
     * Stop
     */
    @Invalidate
    private void stop() {
        logger.info("Stop store service");
    }

    /**
     * Get a store
     *
     * @param id store id
     */
    @Override
    public StoreImpl getStore(String id) {
        String url = stores.getStoreById(id).getUrl();
        storeRS.setUrl(url);
        StoreImpl result = (StoreImpl) storeRS.getStore();
        return result;
    }

    /**
     * @param id Module's id
     * @return Module with the given id
     */
    @Override
    public ModuleImpl getModule(String id) {
        for (StoreImpl store : stores.getStores()) {
            logger.debug("Find Module with id " + id + " in url " + store.getUrl());
            storeRS.setUrl(store.getUrl());
            ModuleImpl result = (ModuleImpl) storeRS.getModuleVersion(id);
            if (result != null) {
                return result;
            }
        }
        logger.info("Couldn't find the module in the stores");
        return null;
    }

    /**
     * @param id Module's id
     * @return Image of the module with the given id
     */
    @Override
    public ModuleImage getModuleImage(String id) {
        for (StoreImpl store : stores.getStores()) {
            logger.debug("Find Module with id " + id + " in url " + store.getUrl());
            storeRS.setUrl(store.getUrl());
            ModuleImage image = new ModuleImage();
            image.setIdModule(id);
            image.setImgOrig(storeRS.getModuleVersionImage(id));
            if (image != null) {
                logger.debug("Send module image to client flex");
                return image;
            }
        }
        logger.debug("Couldn't find the image for this module");
        return null;
    }

    /**
     * @param filter module's name
     * @param order "desc" for descending or "asc" for ascendant
     * @param itemByPage items number by page
     * @param page page number
     * @return Modules result collection
     */
    @Override
    public Collection<ModuleImpl> searchModules(String filter, String field, String order,
                                                Integer itemByPage, Integer page) {
        return this.searchModulesPrivate(filter, field, order, itemByPage, page, false, null);
    }

    /**
     * @param filter module's name
     * @param order "desc" for descending or "asc" for ascendant
     * @param itemByPage items number by page
     * @param page page number
     * @return Modules result collection
     */
    @Override
    public Collection<ModuleImpl> searchModulesWithImage(String filter, String field, String order,
                                                         Integer itemByPage, Integer page) {
        Collection<ModuleImpl> results = this.searchModulesPrivate(filter, field, order, itemByPage, page, false, null);
        this.addImagesToModules(results);
        return results;
    }

    /**
     * @param id Category id
     * @param order desc" for descending or "asc" for ascendant
     ** @param itemByPage items number by page
     * @param page page number
     * @return Modules result collection
     */
    @Override
    public Collection<ModuleImpl> searchModulesByCategory(String id, String field, String order,
                                                          Integer itemByPage, Integer page) {
        return this.searchModulesPrivate(null, field, order, itemByPage, page, true, id);
    }

    /**
     * @param id Category id
     * @param order desc" for descending or "asc" for ascendant
     ** @param itemByPage items number by page
     * @param page page number
     * @return Modules result collection
     */
    @Override
    public Collection<ModuleImpl> searchModulesWithImageByCategory(String id, String field, String order,
                                                                   Integer itemByPage, Integer page) {
        Collection<ModuleImpl> results = this.searchModulesPrivate(null, field, order, itemByPage, page, true, id);
        this.addImagesToModules(results);
        return results;
    }

    @Override
    public Collection<CategoryImpl> getCategories() {
        Collection <CategoryImpl> results = new ArrayList<CategoryImpl>();

        //For each store
        for (StoreImpl store : stores.getStores()) {
            storeRS.setUrl(store.getUrl());
            Collection categories = storeRS.getCategories();
            for (Object cat : categories) {
                if (!results.contains(cat)) {
                    results.add((CategoryImpl)cat);
                }
            }
        }

        return results;
    }

    @Override
    public CategoryImpl getCategory(String id) {
        //For each store
        for (StoreImpl store : stores.getStores()) {
            logger.debug("Find Category with id " + id + " in url " + store.getUrl());
            storeRS.setUrl(store.getUrl());
            CategoryImpl cat = (CategoryImpl)storeRS.getCategory(id);
            if (cat != null) {
                return cat;
            }
        }
        logger.info("Couldn't find the category in the stores");
        return null;
    }

    @Override
    public Collection<ModuleImpl> getInstalledModules() {
        Collection<ModuleImpl> list = new LinkedList<ModuleImpl>();
        try {
            Configuration[] cfgs = configAdmin.listConfigurations("(service.factoryPid=" + ModuleBundle.class.getName() + ")");
            if (cfgs != null) {
                for (Configuration cfg : cfgs) {
                    String data = (String) cfg.getProperties().get(MODULE_KEY);
                    if (data != null) {
                        try {
                            ModuleImpl mv = decodeModule(data);
                            list.add(mv);
                        } catch (Exception e) {
                            logger.error("Can't find Module information: " + cfg.toString());
                        }
                    } else {
                        logger.error("Can't find Module information: " + cfg.toString());
                    }

                }
            }
        } catch (Exception e) {
            logger.error("Can't find the ModuleBundle configurations");
        }
        return list;
    }


    private Configuration getModuleBundleConfiguration(String id) throws StoreException {
        Configuration[] cfgs = null;
        try {
            cfgs = configAdmin.listConfigurations("(service.factoryPid=" + ModuleBundle.class.getName() + ")");
        } catch (Exception e) {
            throw new StoreException(StoreException.INVALID_CONTEXT, "Can't find the ModuleBundle configurations");
        }

        if (cfgs != null) {
            for (Configuration cfg : cfgs) {
                Dictionary dict = cfg.getProperties();
                String moduleId = (String) dict.get(MODULE_ID_KEY);
                if (id.equals(moduleId)) {
                    return cfg;
                }
            }
        }
        return null;
    }

    /**
     * Install a bundle using his id
     *
     * @param id Module's id
     * @return Confirmation message of good or wrong module install
     */
    @Override
    public void installModule(String id) throws StoreException {
        if (getModuleBundleConfiguration(id) != null) {
            throw new StoreException(StoreException.MODULE_ALREADY_INSTALLED, "The module \"" + id + "\" is already installed");
        }

        ModuleImpl module = this.getModule(id);
        if (module == null)
            throw new StoreException(StoreException.MODULE_NOT_FOUND, "Can't find the module \"" + id + "\"on the store");

        installModule(module);
    }

    private void installModule(ModuleImpl module) throws StoreException {
        String id = module.getId();
        byte[] moduleBinary = storeRS.downloadModuleVersion(id);

        if (moduleBinary == null)
            throw new StoreException(StoreException.MODULE_DOWNLOAD_ISSUE, "Can't download the module \"" + id + "\"");

        File file = null;
        Configuration configuration = null;
        try {

            String data = encodeModule(module);

            file = new File(installDirectory.getAbsolutePath() + "/" + id + ".jar");
            OutputStream os = new FileOutputStream(file);
            os.write(moduleBinary);

            Bundle bundle = bundleContext.installBundle(file.toURI().toString());
            bundle.start();

            // Create configuration
            Dictionary properties = new Hashtable();
            properties.put(MODULE_KEY, data);
            properties.put(MODULE_ID_KEY, id);
            properties.put(BUNDLE_ID_KEY, new Long(bundle.getBundleId()).toString());

            configuration = configAdmin.createFactoryConfiguration(ModuleBundle.class.getName(), null);
            configuration.update(properties);
            logger.info("Module \"" + id + "\" installed");
        } catch (Exception ex) {
            if (file != null)
                file.delete();
            try {
                if (configuration != null)
                    configuration.delete();
            } catch (Exception ex2) {
            }
            throw new StoreException(StoreException.CANT_INSTALL, "Can't install the module \"" + id + "\": " + ex);
        }
    }

    /**
     * Update a bundle using his id
     *
     * @param id
     * @throws StoreException
     */
    @Override
    public void updateModule(String id) throws StoreException {
        ModuleImpl remoteModule = this.getModule(id);
        if (remoteModule == null)
            throw new StoreException(StoreException.MODULE_NOT_FOUND, "Can't find the module \"" + id + "\" on the store");

        Configuration configuration = getModuleBundleConfiguration(id);
        if (configuration == null)
            throw new StoreException(StoreException.INVALID_CONTEXT, "Can't find the module \"" + id + "\"");

        boolean update = false;

        String data = (String) configuration.getProperties().get(MODULE_KEY);
        if (data != null) {
            try {
                ModuleImpl localModule = decodeModule(data);
                if (remoteModule.getMajor() > localModule.getMajor()) {
                    update = true;
                } else if (remoteModule.getMajor() == localModule.getMajor()) {
                    if (remoteModule.getMinor() > localModule.getMinor()) {
                        update = true;
                    } else if (remoteModule.getMinor() == localModule.getMinor()) {
                        if (remoteModule.getRevision() > localModule.getRevision()) {
                            update = true;
                        }
                    }
                }

            } catch (Exception e) {
                throw new StoreException(StoreException.MODULE_INVALID, "Can't decode module information");
            }
        } else {
            throw new StoreException(StoreException.MODULE_INVALID, "Can't find module information");
        }

        if (update) {
            uninstallModule(configuration);
            installModule(remoteModule);
        }
    }

    /**
     * Uninstall a bundle using his id
     *
     * @param id
     * @throws StoreException
     */
    @Override
    public void uninstallModule(String id) throws StoreException {
        Configuration configuration = getModuleBundleConfiguration(id);
        if (configuration == null)
            throw new StoreException(StoreException.MODULE_NOT_INSTALLED, "Can't find the module \"" + id + "\"");

        uninstallModule(configuration);
    }

    /**
     * Uninstall a bundle using a configuration
     *
     * @param cfg
     * @throws StoreException
     */
    private void uninstallModule(Configuration cfg) throws StoreException {
        String moduleId = (String) cfg.getProperties().get(MODULE_ID_KEY);
        Long bundleId = Long.parseLong((String) cfg.getProperties().get(BUNDLE_ID_KEY));

        // Delete configuration
        try {
            cfg.delete();
        } catch (IOException e) {
            throw new StoreException(StoreException.CANT_UNINSTALL, "Can't delete the configuration");
        }

        Bundle bundle = bundleContext.getBundle(bundleId);
        if (bundle == null)
            throw new StoreException(StoreException.CANT_UNINSTALL, "Can't find the bundle");

        try {
            // Stop & Uninstall the bundle
            bundle.stop();
            bundle.uninstall();
        } catch (Exception e) {
            throw new StoreException(StoreException.CANT_UNINSTALL, "Can't uninstall the bundle: " + e);
        }

        logger.info("Module \"" + moduleId + "\" uninstalled");
    }

    @Override
    public StoreImpl addStore(StoreImpl store) throws StoreException {
        try{
            //test if the url is good
            URI uri = new URI(store.getUrl());
        }catch (URISyntaxException ex) {
            throw new StoreException(StoreException.BAD_STORE_URL, "The store " + store.getName() +
                    " doesn't have a valid URL format");
        }
        stores.addStore(store);
        return store;
    }

    @Override
    public void updateStore(StoreImpl store) throws StoreException {
        StoreImpl originalStore = stores.getStoreById(store.getId());
        if (originalStore != null) {
            originalStore.setName(store.getName());
            originalStore.setDescription(store.getDescription());

            if (!originalStore.getUrl().equals(store.getUrl())) {
                try{
                    //test if the url is good
                    URI uri = new URI(store.getUrl());
                }catch (URISyntaxException ex) {
                    throw new StoreException(StoreException.BAD_STORE_URL, "The store " + store.getName() +
                            " doesn't have a valid URL format");
                }
                originalStore.setUrl(store.getUrl());
            }

            logger.debug("Store with name " + store.getName() + " has been updated");
        }
    }

    @Override
    public void deleteStore(String id) {
        boolean deleted = this.stores.deleteStore(id);
        if (deleted) {
            logger.debug("The store with id " + id + " has been deleted");
        }
    }

    @Override
    public Collection<StoreImpl> getStores() {
        return this.stores.getStores();
    }

    /**
     * Encode a ModuleImpl into a base64 string
     *
     * @param module the module to encode
     * @return a string containing the serialized module
     * @throws Exception issue during the serialization
     */
    private String encodeModule(ModuleImpl module) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jaxbContext.createMarshaller().marshal(module, os);
        return Base64.encodeToString(os.toByteArray(), false);
    }

    /**
     * Decode a ModuleImpl from a base64 string
     *
     * @param data the string to decode
     * @return the module from the de-serialized string
     * @throws Exception issue during the de-serialization
     */
    private ModuleImpl decodeModule(String data) throws Exception {
        byte[] xml = Base64.decode(data);
        ByteArrayInputStream is = new ByteArrayInputStream(xml);
        return (ModuleImpl) jaxbContext.createUnmarshaller().unmarshal(is);
    }

    /**
     * Set the image (icon) of each module if the image exists otherwise the image set will be a null object
     * @param modules Modules collection
     */
    private void addImagesToModules(Collection<ModuleImpl> modules) {
        for (ModuleImpl mod : modules) {
            byte[] img = storeRS.getModuleVersionImage(mod.getId());
            if (img != null) {
                mod.setImgOrig(img);
            }
        }
    }

    private Collection<ModuleImpl> searchModulesPrivate(String filter, String field, String order,
                                                        Integer itemByPage, Integer page, Boolean byCategory, String id) {
        if (itemByPage == null) {
            Collection<ModuleImpl> results = new ArrayList<ModuleImpl>();
            //For each store
            for (StoreImpl store : stores.getStores()) {
                storeRS.setUrl(store.getUrl());
                Collection modules = null;
                //If we search by Category
                if (byCategory) {
                    modules = storeRS.searchModulesByCategory(id, field, order, itemByPage, page);
                } else {
                    //If we search by keyword
                    modules = storeRS.searchModules(filter, field, order, itemByPage, page);
                }
                // For each result module
                for (Object mod : modules) {
                    //If global collection doesn't contains this module then add it
                    if (!results.contains(mod)) {
                        results.add((ModuleImpl)mod);
                    }
                }
            }
            return results;
        } else {
            ArrayList<String> resultsIds = new ArrayList<String>();

            //TODO add cache system
            //Attention get all modules ids is not the best way to do the pagination but it is a solution
            //For each store
            for (StoreImpl store : stores.getStores()) {
                storeRS.setUrl(store.getUrl());
                ModuleIdsWrapperImpl idsWrapper = null;
                //If we search by Category
                if (byCategory) {
                    idsWrapper = (ModuleIdsWrapperImpl) storeRS.searchModulesByCategoryGetIds(id);
                } else {
                    //If we search by keyword
                    idsWrapper = (ModuleIdsWrapperImpl) storeRS.searchModulesGetIds(filter);
                }
                if (idsWrapper != null) {
                    // For each result module id
                    for (String modId : idsWrapper.getIds()) {
                        //If global collection doesn't contains this module id then add it
                        if (!resultsIds.contains(modId)) {
                            resultsIds.add(modId);
                        }
                    }
                }
            }

            //Get page number
            Integer pageParam = page;
            if (pageParam==null) pageParam = 0;

            //Set the start index of the resultsIds List
            Integer startIndex = pageParam * itemByPage;
            if (startIndex >= resultsIds.size()) {
                return new ArrayList<ModuleImpl>();
            }

            //Set the end index of the resultsIds List
            Integer endIndex = startIndex + itemByPage;
            if (endIndex > resultsIds.size()) {
                endIndex = resultsIds.size();
            }

            Collection<ModuleImpl> modulesResult = new ArrayList<ModuleImpl>();

            //Get the modules in the list from the start index to the end index
            while (startIndex < endIndex) {
                modulesResult.add(this.getModule(resultsIds.get(startIndex)));
                startIndex++;
            }

            return modulesResult;
        }
    }
}
