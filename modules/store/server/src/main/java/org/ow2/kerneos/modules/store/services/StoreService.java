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
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.ow2.kerneos.core.service.KerneosService;
import org.ow2.kerneos.core.service.KerneosSimpleService;
import org.ow2.kerneos.modules.store.IStoreRS;
import org.ow2.kerneos.modules.store.IStoreService;
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
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

@Component
@Instantiate
@Provides

@KerneosService(id = "store_service")

public class StoreService implements KerneosSimpleService, IStoreService {
    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(StoreService.class);

    private IStoreRS storeRS;
    private BundleContext bundleContext;
    private String installDirectoryString = "/tmp/store/bundles/";
    private File installDirectory;

    private JAXBContext jaxbContext;

    @Requires
    private ConfigurationAdmin configAdmin;

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

        IStoreRS store = new StoreRS();
        org.ow2.kerneosstore.api.Store result = store.getStore();

        logger.info("Store Name : " + result.getName());
        logger.info("Store Description : " + result.getDescription());
        logger.info("Store Url : " + result.getUrl());
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
     * @param url store url, REST path
     */
    @Override
    public StoreImpl getStore(String url) {
        StoreImpl result = (StoreImpl) storeRS.getStore();
        return result;
    }

    /**
     * @param id Module's id
     * @return Module with the given id
     */
    @Override
    public ModuleImpl getModule(String id) {
        ModuleImpl result = (ModuleImpl) storeRS.getModuleVersion(id);
        logger.info("Module id parameter " + id);
        logger.info("Modules name " + result.getName());
        return result;
    }

    /**
     * @param id Module's id
     * @return Image of the module with the given id
     */
    @Override
    public ModuleImage getModuleImage(String id) {

        logger.info("Send module image to client flex ");
        ModuleImage image = new ModuleImage();
        image.setIdModule(id);
        image.setImgOrig(storeRS.getModuleVersionImage(id));
        return image;
    }

    /**
     * @param filter
     * @param order
     * @param itemByPage
     * @param page
     * @return
     */
    @Override
    public Collection<ModuleImpl> searchModules(String filter, String field, String order,
                                                Integer itemByPage, Integer page) {
        Collection result = storeRS.searchModules(filter, field, order, itemByPage, page);
        return result;
    }

    @Override
    public Collection<ModuleImpl> searchModulesWithImage(String filter, String field, String order,
                                                         Integer itemByPage, Integer page) {
        Collection<ModuleImpl> result = this.searchModules(filter, field, order, itemByPage, page);
        for (ModuleImpl module : result) {
            byte[] img = storeRS.getModuleVersionImage(module.getId());
            module.setImgOrig(img);
        }
        return result;
    }

    /**
     * @param id
     * @param order
     * @param itemByPage
     * @param page
     * @return
     */
    @Override
    public Collection<ModuleImpl> searchModulesByCategory(String id, String field, String order,
                                                          Integer itemByPage, Integer page) {
        Collection result = storeRS.searchModulesByCategory(id, field, order, itemByPage, page);
        return result;
    }

    @Override
    public Collection<ModuleImpl> searchModulesWithImageByCategory(String id, String field, String order, Integer itemByPage, Integer page) {
        Collection<ModuleImpl> result = this.searchModulesByCategory(id, field, order, itemByPage, page);
        for (ModuleImpl module : result) {
            byte[] img = storeRS.getModuleVersionImage(module.getId());
            module.setImgOrig(img);
        }
        return result;
    }

    @Override
    public Collection<ModuleImpl> getCategories() {
        Collection result = storeRS.getCategories();
        return result;
    }

    @Override
    public CategoryImpl getCategory(String id) {
        return (CategoryImpl) storeRS.getCategory(id);
    }

    /**
     * @param id Module's id
     * @return Confirmation message of good or wrong module install
     */
    @Override
    public synchronized String installModule(String id) {
        ModuleVersion moduleVersion = storeRS.getModuleVersion(id);
        byte[] moduleBinary = storeRS.downloadModuleVersion(id);

        if (moduleBinary == null) {
            return "";
        }
        File file = null;
        Configuration gravityDestination = null;
        try {
            String data = encodeModule(new ModuleImpl(moduleVersion));

            file = new File(installDirectory.getAbsolutePath() + "/" + id + ".jar");
            OutputStream os = new FileOutputStream(file);
            os.write(moduleBinary);

            Bundle bundle = bundleContext.installBundle(file.toURI().toString());
            Dictionary properties = new Hashtable();
            properties.put("module_id", id);
            properties.put("bundle_id", new Long(bundle.getBundleId()).toString());
            properties.put("module", data);

            gravityDestination = configAdmin.createFactoryConfiguration(ModuleBundle.class.getName(), null);
            gravityDestination.update(properties);
            logger.info("Module \"" + id + "\" installed");
        } catch (Exception ex) {
            logger.error("Can't install the module \"" + id + "\": " + ex);
            if (file != null)
                file.delete();
            try {
                if (gravityDestination != null)
                    gravityDestination.delete();
            } catch (Exception ex2) {
                logger.error("Can't delete the configuration");
            }
        }
        return "";
    }

    @Override
    public Collection<ModuleImpl> getInstalledModules() {
        try {
            Configuration[] cfgs = configAdmin.listConfigurations(ModuleBundle.class.getName());
            Collection<ModuleImpl> list = new LinkedList<ModuleImpl>();
            for (Configuration cfg : cfgs) {
                String data = (String) cfg.getProperties().get("module");
                if (data != null) {
                    try {
                        ModuleImpl mv = decodeModule(data);
                        list.add(new ModuleImpl(mv));
                    } catch (Exception e) {

                    }
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String uninstallModule(String id) {
        return null;
    }

    @Override
    public String updateModule(String id) {
        return null;
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
}

