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

import org.ow2.kerneos.common.vo.ApplicationVO;
import org.ow2.kerneos.common.vo.AuthenticationVO;
import org.ow2.kerneos.common.vo.FolderVO;
import org.ow2.kerneos.common.vo.IFrameModuleVO;
import org.ow2.kerneos.common.vo.LanguageVO;
import org.ow2.kerneos.common.vo.LinkVO;
import org.ow2.kerneos.common.vo.MappingVO;
import org.ow2.kerneos.common.vo.ModuleEventVO;
import org.ow2.kerneos.common.vo.ModuleVO;
import org.ow2.kerneos.common.vo.ModuleWithWindowVO;
import org.ow2.kerneos.common.vo.PromptBeforeCloseVO;
import org.ow2.kerneos.common.vo.SWFModuleVO;
import org.ow2.kerneos.common.vo.ServiceVO;

public class ConfigVOObjects {

    private static var dictionary:Dictionary = new Dictionary();

    dictionary["org.ow2.kerneos.common.config.generated.Application"] = ApplicationVO;
    dictionary["org.ow2.kerneos.common.config.generated.Language"] = LanguageVO;
    dictionary["org.ow2.kerneos.common.config.generated.Authentication"] = AuthenticationVO;
    dictionary["org.ow2.kerneos.common.config.generated.Folder"] = FolderVO;
    dictionary["org.ow2.kerneos.common.config.generated.IframeModule"] = IFrameModuleVO;
    dictionary["org.ow2.kerneos.common.config.generated.Link"] = LinkVO;
    dictionary["org.ow2.kerneos.common.config.generated.Mapping"] = MappingVO;
    dictionary["org.ow2.kerneos.common.config.ModuleEvent"] = ModuleEventVO;
    dictionary["org.ow2.kerneos.common.config.generated.Module"] = ModuleVO;
    dictionary["org.ow2.kerneos.common.config.generated.ModuleWithWindow"] = ModuleWithWindowVO;
    dictionary["org.ow2.kerneos.common.config.generated.PromptBeforeClose"] = PromptBeforeCloseVO;
    dictionary["org.ow2.kerneos.common.config.generated.Service"] = ServiceVO;
    dictionary["org.ow2.kerneos.common.config.generated.SwfModule"] = SWFModuleVO;

    public function ConfigVOObjects() {
    }

    public static function values():Dictionary {
        return dictionary;
    }

}
}
