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

import org.ow2.kerneos.profile.vo.*;
import org.ow2.kerneos.login.vo.KerneosSessionVO;

public class SecurityVOObjects {

    private static var dictionary:Dictionary = new Dictionary();

    dictionary["org.ow2.kerneos.profile.config.generated.ProfileBundle"] = ProfileBundleVO;
    dictionary["org.ow2.kerneos.profile.config.generated.ProfileMethod"] = ProfileMethodVO;
    dictionary["org.ow2.kerneos.profile.config.generated.ProfileModule"] = ProfileModuleVO;
    dictionary["org.ow2.kerneos.profile.config.generated.ProfilePolicy"] = ProfilePolicyVO;
    dictionary["org.ow2.kerneos.profile.config.generated.ProfileRule"] = ProfileRuleVO;
    dictionary["org.ow2.kerneos.profile.config.generated.ProfileService"] = ProfileServiceVO;
    dictionary["org.ow2.kerneos.profile.config.generated.Profile"] = ProfileVO;
    dictionary["org.ow2.kerneos.login.KerneosSession"] = KerneosSessionVO;

    public function SecurityVOObjects() {
    }

    public static function values():Dictionary {
        return dictionary;
    }
}
}
