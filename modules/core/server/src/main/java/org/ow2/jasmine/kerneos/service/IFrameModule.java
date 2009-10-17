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
import javax.xml.bind.annotation.XmlRootElement;


/**
 * An IFrame deployed as a module in Kerneos.
 *
 * @author Julien Nicoulaud
 */
@XmlRootElement(name = "iframe-module", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
public class IFrameModule extends ModuleWithWindow {

    /**
     * The web page URL.
     */
    @XmlElement(name = "url", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public String url = null;

    /**
    * Show the "Open in browser" button.
    */
    @XmlElement(name = "show-open-in-browser-button", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean showOpenInBrowserButton = false;

    /**
    * Show the history navigation buttons.
    */
    @XmlElement(name = "show-history-navigation-buttons", namespace = KerneosConfig.KERNEOS_CONFIG_NAMESPACE)
    public Boolean showHistoryNavigationButtons = false;


    // Utils

    @Override
    public String toString() {
        return "[IFrame module] "  + super.toString() + ", url: \"" + url + "\"";
    }

}
