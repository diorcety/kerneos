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

package org.ow2.kerneos.flex;

import org.ow2.kerneos.common.config.ModuleEvent;
import org.ow2.kerneos.common.config.generated.Application;
import org.ow2.kerneos.common.config.generated.Authentication;
import org.ow2.kerneos.common.config.generated.IframeModule;
import org.ow2.kerneos.common.config.generated.Language;
import org.ow2.kerneos.common.config.generated.Mapping;
import org.ow2.kerneos.common.config.generated.Module;
import org.ow2.kerneos.common.config.generated.PromptBeforeClose;
import org.ow2.kerneos.common.config.generated.Service;
import org.ow2.kerneos.common.config.generated.ServiceType;
import org.ow2.kerneos.common.config.generated.SwfModule;

/**
 * Classes used with configuration service.
 */
public class ConfigObjects {
    private ConfigObjects() {

    }

    private final static Class[] list = new Class[]{
            ServiceType.class,
            Service.class,
            Application.class,
            Language.class,
            Mapping.class,
            Module.class,
            ModuleEvent.class,
            IframeModule.class,
            SwfModule.class,
            PromptBeforeClose.class,
            Authentication.class};

    public static Class[] list() {
        return list;
    }
}
