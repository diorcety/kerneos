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

package org.ow2.kerneos.core;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.ow2.kerneos.common.KerneosConstants;
import org.ow2.kerneos.common.config.generated.Application;
import org.ow2.kerneos.common.config.generated.Folder;
import org.ow2.kerneos.common.config.generated.ManagerProperty;
import org.ow2.kerneos.common.config.generated.Module;
import org.ow2.kerneos.common.config.generated.Service;
import org.ow2.kerneos.common.config.generated.SwfModule;
import org.ow2.kerneos.core.manager.DefaultKerneosLogin;
import org.ow2.kerneos.core.manager.DefaultKerneosProfile;
import org.ow2.kerneos.core.manager.DefaultKerneosRoles;
import org.ow2.kerneos.login.KerneosLogin;
import org.ow2.kerneos.profile.KerneosProfile;
import org.ow2.kerneos.roles.KerneosRoles;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


/**
 * The core of Kerneos.
 */
@Component
@Instantiate
@Provides
public class Core implements ICore {

    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(Core.class);

    private Map<String, Instance> applicationInstances = new HashMap<String, Instance>();
    private Map<String, Dictionary> applicationConfigurations = new HashMap<String, Dictionary>();

    private Map<String, Instance> moduleInstances = new HashMap<String, Instance>();
    private Map<String, Dictionary> moduleConfigurations = new HashMap<String, Dictionary>();

    @Requires(filter = "(factory.name=org.ow2.kerneos.core.ApplicationImpl)")
    private Factory applicationFactory;

    @Requires(filter = "(factory.name=org.ow2.kerneos.core.ModuleImpl)")
    private Factory moduleFactory;

    @Requires
    private ConfigurationAdmin configurationAdmin;

    private BundleContext bundleContext;

    private Core(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private synchronized void start() throws IOException {
        LOGGER.debug("Start Kerneos Core");
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private synchronized void stop() throws IOException {
        LOGGER.debug("Stop Kerneos Core");

        // Dispose applications
        while (applicationConfigurations.size() != 0) {
            try {
                String applicationId = applicationConfigurations.keySet().iterator().next();
                Dictionary configuration = applicationConfigurations.get(applicationId);
                removeApplication(applicationId, bundleContext.getBundle((Long) configuration.get(ModuleImpl.BUNDLE)));
            } catch (Exception e) {

            }
        }

        // Dispose modules
        while (moduleConfigurations.size() != 0) {
            try {
                String moduleId = moduleConfigurations.keySet().iterator().next();
                Dictionary configuration = moduleConfigurations.get(moduleId);
                removeModule(moduleId, bundleContext.getBundle((Long) configuration.get(ModuleImpl.BUNDLE)));
            } catch (Exception e) {

            }
        }
    }

    @Override
    public synchronized void addApplication(String applicationId, Application applicationConfiguration, Bundle bundle)
            throws Exception {
        LOGGER.debug("New Application \"" + applicationId + "\": " + applicationConfiguration);

        if (applicationInstances.containsKey(applicationId)) {
            throw new Exception("The Application \"" + applicationId + "\" already exists");
        }

        // Transform application information
        transformApplication(applicationConfiguration, applicationId);

        Instance instance = new Instance();
        try {

            Dictionary componentDictionary = new Hashtable();
            componentDictionary.put("requires.filters", new String[]{
                    ApplicationImpl.LOGIN, "(" + KerneosLogin.ID + "=" + applicationId + ")",
                    ApplicationImpl.PROFILE, "(" + KerneosProfile.ID + "=" + applicationId + ")",
                    ApplicationImpl.ROLES, "(" + KerneosRoles.ID + "=" + applicationId + ")",
            });
            componentDictionary.put(ApplicationImpl.ID, applicationId);
            componentDictionary.put(ApplicationImpl.CONFIGURATION, applicationConfiguration);
            componentDictionary.put(ApplicationImpl.BUNDLE, bundle.getBundleId());

            instance.setComponentInstance(applicationFactory.createComponentInstance(componentDictionary));

            // Managers
            if (applicationConfiguration.getManagers() != null) {
                // Login
                if (applicationConfiguration.getManagers().getLogin() != null) {
                    Configuration configuration = configurationAdmin.createFactoryConfiguration(
                            applicationConfiguration.getManagers().getLogin().getId(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put(KerneosLogin.ID, applicationId);
                    for (ManagerProperty prop : applicationConfiguration.getManagers().getLogin().getProperty()) {
                        dictionary.put(prop.getId(), prop.getValue());
                    }
                    configuration.update(dictionary);

                    instance.addConfiguration(configuration);
                } else {
                    Configuration configuration = configurationAdmin.createFactoryConfiguration(
                            DefaultKerneosLogin.class.getName(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put(KerneosLogin.ID, applicationId);
                    configuration.update(dictionary);

                    instance.addConfiguration(configuration);
                }

                // Profile
                if (applicationConfiguration.getManagers().getProfile() != null) {
                    Configuration configuration = configurationAdmin.createFactoryConfiguration(
                            applicationConfiguration.getManagers().getProfile().getId(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put(KerneosProfile.ID, applicationId);
                    for (ManagerProperty prop : applicationConfiguration.getManagers().getProfile().getProperty()) {
                        dictionary.put(prop.getId(), prop.getValue());
                    }
                    configuration.update(dictionary);

                    instance.addConfiguration(configuration);
                } else {
                    Configuration configuration = configurationAdmin.createFactoryConfiguration(
                            DefaultKerneosProfile.class.getName(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put(KerneosProfile.ID, applicationId);
                    configuration.update(dictionary);

                    instance.addConfiguration(configuration);
                }

                // Roles
                if (applicationConfiguration.getManagers().getRoles() != null) {
                    Configuration configuration = configurationAdmin.createFactoryConfiguration(
                            applicationConfiguration.getManagers().getRoles().getId(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put(KerneosRoles.ID, applicationId);
                    for (ManagerProperty prop : applicationConfiguration.getManagers().getRoles().getProperty()) {
                        dictionary.put(prop.getId(), prop.getValue());
                    }
                    configuration.update(dictionary);

                    instance.addConfiguration(configuration);
                } else {
                    Configuration configuration = configurationAdmin.createFactoryConfiguration(
                            DefaultKerneosRoles.class.getName(), null);

                    Dictionary dictionary = new Hashtable();
                    dictionary.put(KerneosRoles.ID, applicationId);
                    configuration.update(dictionary);

                    instance.addConfiguration(configuration);
                }
            }

            // Remove managers (security issue)
            applicationConfiguration.setManagers(null);

            applicationInstances.put(applicationId, instance);
            applicationConfigurations.put(applicationId, componentDictionary);
            instance.start();
            LOGGER.debug("Application \"" + applicationId + "\" added");
        } catch (Exception e) {
            try {
                instance.dispose();
            } catch (IOException e2) {

            }
            throw new Exception("Can't add Application \"" + applicationId + "\"", e);
        }
    }

    /**
     * Adapt the configuration of an application to Kerneos.
     *
     * @param application the application to transform.
     * @param name        the name associated with the application.
     */
    private void transformApplication(Application application, String name) {
        // Correctly format URL
        if (!application.getApplicationUrl().startsWith("/"))
            application.setApplicationUrl("/" + application.getApplicationUrl());
        if (application.getApplicationUrl().endsWith("/"))
            application.setApplicationUrl(application.getApplicationUrl().substring(0, application.getApplicationUrl().length() - 1));
    }

    @Override
    public synchronized void removeApplication(String applicationId, Bundle bundle)
            throws Exception {
        LOGGER.debug("Remove Application \"" + applicationId + "\"");
        try {
            if (applicationConfigurations.containsKey(applicationId)) {
                Dictionary dictionary = applicationConfigurations.get(applicationId);
                // Check bundle used for registering
                if ((Long) dictionary.get(ModuleImpl.BUNDLE) == bundle.getBundleId()) {
                    applicationConfigurations.remove(applicationId);
                    Instance applicationBundle = applicationInstances.remove(applicationId);
                    applicationBundle.dispose();

                    LOGGER.debug("Application \"" + applicationId + "\" removed");
                }
            }
        } catch (IOException e) {
            throw new Exception("Can't remove Application \"" + applicationId + "\"", e);
        }
    }


    @Override
    public synchronized void addModule(String moduleId, Module moduleConfiguration, Bundle bundle)
            throws Exception {
        LOGGER.debug("New Module \"" + moduleId + "\": " + moduleConfiguration);

        if (applicationInstances.containsKey(moduleId)) {
            throw new Exception("The Module \"" + moduleId + "\" already exists");
        }

        // Transform module information
        transformModule(moduleConfiguration, moduleId);

        Instance instance = new Instance();
        try {
            Dictionary componentDictionary = new Hashtable();
            componentDictionary.put(ModuleImpl.ID, moduleId);
            componentDictionary.put(ModuleImpl.CONFIGURATION, moduleConfiguration);
            componentDictionary.put(ModuleImpl.BUNDLE, bundle.getBundleId());

            instance.setComponentInstance(moduleFactory.createComponentInstance(componentDictionary));

            moduleInstances.put(moduleId, instance);
            moduleConfigurations.put(moduleId, componentDictionary);
            instance.start();
            LOGGER.debug("Module \"" + moduleId + "\" added");
        } catch (Exception e) {
            try {
                instance.dispose();
            } catch (IOException e2) {

            }
            throw new Exception("Can't add Module \"" + moduleId + "\"", e);
        }
    }


    /**
     * Adapt the configuration of a module to Kerneos.
     *
     * @param module the module to transform.
     * @param name   the name associated with the module.
     */
    private void transformModule(Module module, String name) {
        // Fix Paths with module name
        module.setBundle(name);
        module.setBigIcon(KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + name + "/" + module.getBigIcon());
        module.setSmallIcon(KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + name + "/" + module.getSmallIcon());
        if (module instanceof SwfModule) {
            SwfModule swfModule = (SwfModule) module;
            swfModule.setFile(KerneosConstants.KERNEOS_MODULE_PREFIX + "/" + name + "/" + swfModule.getFile());

            // Completing the destination of the module's services
            for (Service service : swfModule.getServices()) {
                if (service.getDestination() == null) {
                    service.setDestination(name + "~" + service.getId());
                }
            }
        } else if (module instanceof Folder) {
            for (Module subModule : ((Folder) module).getModules()) {
                transformModule(subModule, name);
            }
        }
    }

    @Override
    public synchronized void removeModule(final String moduleId, Bundle bundle)
            throws Exception {
        LOGGER.debug("Remove Module \"" + moduleId + "\"");
        try {
            if (moduleConfigurations.containsKey(moduleId)) {
                Dictionary dictionary = moduleConfigurations.get(moduleId);
                // Check bundle used for registering
                if ((Long) dictionary.get(ModuleImpl.BUNDLE) == bundle.getBundleId()) {
                    moduleConfigurations.remove(moduleId);
                    Instance applicationBundle = moduleInstances.remove(moduleId);
                    applicationBundle.dispose();

                    LOGGER.debug("Module \"" + moduleId + "\" removed");
                }
            }
        } catch (IOException e) {
            throw new Exception("Can't remove Module \"" + moduleId + "\"", e);
        }
    }
}
