/**
 * JASMINe
 * Copyright (C) 2008 Bull S.A.S.
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

package org.ow2.jasmine.kerneos.service;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author
 */
public class ModuleService implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7807669487844076133L;

    /**
     *
     */
    private static Log logger = LogFactory.getLog(ModuleService.class);

    /**
     *
     */
    private static final String KERNEOS_CONFIG_FILE = "META-INF/kerneos-config.xml";

    /**
     *
     * @return
     */
    public KerneosConfig modules() {

        return loadModules();

    }

    /**
     *
     * @return
     */
    private KerneosConfig loadModules() {

        KerneosConfig config = new KerneosConfig();
        List<Module> modules = new ArrayList<Module>();

        try {

            // Prepare to parse the xml Kerneos config file
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = null;

            // Retrieve the kerneos config file
            ClassLoader cl = this.getClass().getClassLoader();
            if (cl.getResource(KERNEOS_CONFIG_FILE) != null) {

                logger.info("loading file : {0}", KERNEOS_CONFIG_FILE);

                logger.debug("Loading the registy XML file from classpath");
                InputStream resource = cl.getResourceAsStream(KERNEOS_CONFIG_FILE);
                try {

                    // Parse the config file
                    doc = documentBuilder.parse(resource);
                } finally {
                    resource.close();
                    resource = null;
                }
            } else {
                logger.error("No configuration file available : {0}", KERNEOS_CONFIG_FILE);
            }

            // Extract data from the config file
            if (doc != null) {

                // Normalize the XML document
                doc.getDocumentElement().normalize();

                // Read the options
                logger.info("Reading options");
                NodeList optionsNode = doc.getElementsByTagName("options");
                NodeList optionsNodes = optionsNode.item(0).getChildNodes();

                for (int i = 0; i < optionsNodes.getLength(); i++) {
                    Node option = optionsNodes.item(i);

                    // Console project name
                    if (option.getNodeName().equals("console-project")) {
                        config.consoleProject = option.getTextContent();
                        logger.info("Console project : " + config.consoleProject);
                    }

                    // Console name
                    else if (option.getNodeName().equals("console-name")) {
                        config.consoleName = option.getTextContent();
                        logger.info("Console name : " + config.consoleName);
                    }

                    // Show "Minimize all" icon
                    else if (option.getNodeName().equals("show-minimize-all-icon")) {
                        config.showMinimizeAllIcon = Boolean.parseBoolean(option.getTextContent());
                        logger.info("Show Minimize all icon : " + config.showMinimizeAllIcon);
                    }

                    // Show "cascade" icon
                    else if (option.getNodeName().equals("show-cascade-icon")) {
                        config.showCascadeIcon = Boolean.parseBoolean(option.getTextContent());
                        logger.info("Show cascade icon : " + config.showCascadeIcon);
                    }

                    // Show "tile" icon
                    else if (option.getNodeName().equals("show-tile-icon")) {
                        config.showTileIcon = Boolean.parseBoolean(option.getTextContent());
                        logger.info("Show tile icon : " + config.showTileIcon);
                    }

                    // Show notifications popups
                    else if (option.getNodeName().equals("show-notification-popups")) {
                        config.showNotificationPopUps = Boolean.parseBoolean(option.getTextContent());
                        logger.info("Show notification popups : " + config.showNotificationPopUps);
                    }
                }

                // Read the list of modules
                NodeList listOfEntries = doc.getElementsByTagName("module");

                logger.debug("Number of modules : {0}", listOfEntries.getLength());

                for (int i = 0; i < listOfEntries.getLength(); i++) {
                    Module mod = new Module();

                    if (listOfEntries.item(i).getAttributes().getNamedItem("swfFile") != null) {
                        mod.setSwfFile(listOfEntries.item(i).getAttributes().getNamedItem("swfFile").getNodeValue());
                        if (Thread.currentThread().getContextClassLoader().getResource(mod.getSwfFile()) == null) {
                            logger.error("ERROR : {0} file is not loadable, check that this file is embedded in the WAR.", mod.getSwfFile());
                        }
                    } else if (listOfEntries.item(i).getAttributes().getNamedItem("url") != null) {
                        mod.setUrl(listOfEntries.item(i).getAttributes().getNamedItem("url").getNodeValue());
                    } else {
                        new Exception("A swf file or an url must be set up").printStackTrace();
                    }
                    mod.setLoaded(Boolean.getBoolean(listOfEntries.item(i).getAttributes().getNamedItem("loaded")
                        .getNodeValue()));

                    NodeList module = listOfEntries.item(i).getChildNodes();
                    for (int j = 0; j < module.getLength(); j++) {
                        Node moduleDetail = module.item(j);
                        if ("name".equals(moduleDetail.getNodeName())) {
                            mod.setName(moduleDetail.getTextContent());
                            logger.debug("module name : {0}", mod.getName());
                        } else if ("description".equals(moduleDetail.getNodeName())) {
                            mod.setDescription(moduleDetail.getTextContent());
                            logger.debug("module description : {0}", mod.getDescription());
                        } else if ("small-icon".equals(moduleDetail.getNodeName())) {
                            mod.smallIcon = moduleDetail.getTextContent();
                            logger.debug("module small icon : {0}", mod.getDescription());
                        } else if ("big-icon".equals(moduleDetail.getNodeName())) {
                            mod.bigIcon = moduleDetail.getTextContent();
                            logger.debug("module big icon : {0}", mod.getDescription());
                        } else if ("services".equals(moduleDetail.getNodeName())) {
                            NodeList services = moduleDetail.getChildNodes();
                            List<Service> servicesList = new ArrayList<Service>();
                            for (int k = 0; k < services.getLength(); k++) {
                                Node servicesDetail = services.item(k);
                                if ("service".equals(servicesDetail.getNodeName())) {
                                    Service s = new Service();
                                    s.setId(servicesDetail.getAttributes().getNamedItem("id").getNodeValue());
                                    s.setDestination(servicesDetail.getAttributes().getNamedItem("destination").getNodeValue());
                                    servicesList.add(s);
                                }
                            }
                            mod.setServices(servicesList);
                        }
                    }
                    modules.add(mod);
                    logger.info("{0}", mod.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        config.modules = modules;

        return config;

    }
}
