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

import javax.xml.bind.annotation.XmlElement;

/**
 * A module deployed in Kerneos.
 *
 * @author Guillaume Renault, Julien Nicoulaud
 */
public class Module {

    /**
     * The displayed name of the module.
     */
    @XmlElement(name = "name", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String name = null;

    /**
     * The description of the module.
     */
    @XmlElement(name = "description", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String description = null;

    /**
     * The small icon (16x16) path.
     */
    @XmlElement(name = "small-icon", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String smallIcon = null;

    /**
     * The big icon (64x64) path.
     */
    @XmlElement(name = "big-icon", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String bigIcon = null;

    /**
     * Load the module on application startup.
     */
    @XmlElement(name = "load-on-startup", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean loadOnStartup = false;

    /**
     * Load the module maximized.
     */
    @XmlElement(name = "load-maximized", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean loadMaximized = false;

    /**
     * Prompt the user before closing the module Can be "never", "always", or
     * "default". "default" means that if the module implements {@see
     * KerneosModule}, the method canBeCloseWithoutPrompt() is called. If not,
     * the user is prompted by default.
     */
    public static String DEFAULT_PROMPT_BEFORE_CLOSE = "default";

    public static String NEVER_PROMPT_BEFORE_CLOSE = "never";

    public static String ALWAYS_PROMPT_BEFORE_CLOSE = "always";

    @XmlElement(name = "prompt-before-close", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String promptBeforeClose = DEFAULT_PROMPT_BEFORE_CLOSE;

    /**
     * Output a String preview of the object.
     */
    @Override
    public String toString() {
        return "name: " + name + ", description: " + description + ", smallIcon: " + smallIcon + ", bigIcon: " + bigIcon
            + ", loadOnStartup: " + loadOnStartup + ", loadMaximized: " + loadMaximized + ", promptBeforeClose: " + promptBeforeClose;
    }

}
