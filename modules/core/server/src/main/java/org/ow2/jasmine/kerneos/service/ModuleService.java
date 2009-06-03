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
 * $Id: ModuleService.java 2665 2009-03-01 15:08:05Z
 * Jean-Pierre & Tianyi
 * --------------------------------------------------------------------------
 */

package org.ow2.jasmine.kerneos.service;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModuleService implements Serializable {

    private static final long serialVersionUID = 7807669487844076133L;

    private static Log logger = LogFactory.getLog(ModuleService.class);

    private static final String KERNEOS_CONFIG_FILE = "META-INF/kerneos-config.xml";

    public List<Module> modules() {

        return loadModules();

    }

    private List<Module> loadModules() {

        List<Module> modules = new ArrayList<Module>();

        try {
            URL kerneos = ModuleService.class.getClassLoader().getResource("META-INF/kerneos-config.xml");

            logger.info("loading file : {0}", kerneos);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = null;

            ClassLoader cl = this.getClass().getClassLoader();
            if (cl.getResource(KERNEOS_CONFIG_FILE) != null) {

                logger.info("loading file : {0}", KERNEOS_CONFIG_FILE);

                logger.debug("Loading the registy XML file from classpath");
                InputStream resource = cl.getResourceAsStream(KERNEOS_CONFIG_FILE);
                try {
                    doc = documentBuilder.parse(resource);
                } finally {
                    resource.close();
                    resource = null;
                }
            } else {
                logger.error("No configuration file available : {0}", KERNEOS_CONFIG_FILE);
            }

            if (doc != null) {
                doc.getDocumentElement().normalize();
                NodeList listOfEntries = doc.getElementsByTagName("module");

                logger.debug("Number of modules : {0}", listOfEntries.getLength());

                for (int i = 0; i < listOfEntries.getLength(); i++) {
                    Module mod = new Module();

                    mod.setSwfFile(listOfEntries.item(i).getAttributes().getNamedItem("swfFile").getNodeValue());
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return modules;

    }
}
