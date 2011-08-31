/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
 * Contact: jasmine AT ow2.org
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
 * $Id$
 */
package org.ow2.kerneos.core.vo {
import flash.utils.Dictionary;

import org.ow2.kerneos.core.vo.*;

public class ConfigVOObjects {

    private static var dictionary:Dictionary = new Dictionary();

    dictionary["org.ow2.kerneos.core.config.generated.Application"] = ApplicationVO;
    dictionary["org.ow2.kerneos.core.config.generated.Authentication"] = AuthenticationVO;
    dictionary["org.ow2.kerneos.core.config.generated.Folder"] = FolderVO;
    dictionary["org.ow2.kerneos.core.config.generated.IframeModule"] = IFrameModuleVO;
    dictionary["org.ow2.kerneos.core.config.generated.Link"] = LinkVO;
    dictionary["org.ow2.kerneos.core.config.generated.Mapping"] = MappingVO;
    dictionary["org.ow2.kerneos.core.ModuleEvent"] = ModuleEventVO;
    dictionary["org.ow2.kerneos.core.config.generated.Module"] = ModuleVO;
    dictionary["org.ow2.kerneos.core.config.generated.ModuleWithWindow"] = ModuleWithWindowVO;
    dictionary["org.ow2.kerneos.core.config.generated.PromptBeforeClose"] = PromptBeforeCloseVO;
    dictionary["org.ow2.kerneos.core.config.generated.Service"] = ServiceVO;
    dictionary["org.ow2.kerneos.core.config.generated.SwfModule"] = SWFModuleVO;

    public function ConfigVOObjects() {
    }

    public static function values():Dictionary {
        return dictionary;
    }

}
}
