/**
 * JASMINe
 * Copyright (C) 2009 Bull S.A.S.
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * Kerneos configuration file loading and parsing service.
 *
 * @author Guillaume Renault
 * @author Julien Nicoulaud
 */
public class KerneosConfigService implements Serializable {

    /**
     * The class serial version ID.
     */
    private static final long serialVersionUID = 7807669487844076133L;

    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(KerneosConfigService.class);

    /**
     * The path to the Kerneos config file.
     */
    private static final String KERNEOS_CONFIG_FILE = "META-INF/kerneos-config.xml";

    /**
     * The path to the application root.
     */
    private static final String PREFIX = "../../";

    /**
     * The JAXB context for rules packages serialization/deserialization. Must
     * be declared with all the potentially involved classes.
     */
    private JAXBContext jaxbContext;

    /**
     * Load the Kerneos config file and build the configuration object
     */
    public KerneosConfig loadKerneosConfig() throws Exception {

        // Retrieve the kerneos config file
        String configurationFile = "";
        ClassLoader loader = this.getClass().getClassLoader();
        if (loader.getResource(KERNEOS_CONFIG_FILE) != null) {
            configurationFile = KERNEOS_CONFIG_FILE;
        } else if (loader.getResource(PREFIX + KERNEOS_CONFIG_FILE) != null) {
            configurationFile = PREFIX + KERNEOS_CONFIG_FILE;
        }

        if (!(configurationFile.equals("") || configurationFile == null)) {

            // Load the file
            logger.debug("loading file : {0}", configurationFile);
            InputStream resource = loader.getResourceAsStream(configurationFile);

            // Unmarshall it
            try {

                if (jaxbContext == null) {
                    jaxbContext = JAXBContext.newInstance(KerneosConfig.class, SWFModule.class, Service.class,
                        IFrameModule.class, Link.class, Folder.class);
                }

                // Create an unmarshaller
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                // Deserialize the configuration file
                return (KerneosConfig) unmarshaller.unmarshal(resource);

            } catch (Exception e) {
                throw e;
            } finally {
                resource.close();
            }
        } else {
            throw new Exception("No configuration file available at " + KERNEOS_CONFIG_FILE);
        }

    }
}
