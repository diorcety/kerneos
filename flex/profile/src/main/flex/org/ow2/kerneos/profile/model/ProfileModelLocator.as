/**
 * Kerneos
 * Copyright (C) 2008 Bull S.A.S.
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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.kerneos.profile.model {
import com.adobe.cairngorm.model.ModelLocator;

import org.ow2.kerneos.profile.business.IGetProfileDelegate;
import org.ow2.kerneos.profile.business.GetProfileDelegate;

import org.ow2.kerneos.profile.vo.ProfileVO;

public class ProfileModelLocator implements ModelLocator {

    /**
     * The state of the profile.
     */
    [Bindable]
    public var state:String = ProfileState.IDLE;

    [Bindable]
    public var profile:ProfileVO = null;

    /**
     * Unique instance of this locator.
     */
    private static var model:ProfileModelLocator = null;

    //[ArrayElementType(JasmineMessageEventVO)]

    // delegate unique instances

    private var profileDelegate:IGetProfileDelegate = null;

    // functions
    public function ProfileModelLocator() {
        super();

        if (model != null) {
            throw new Error("Only one ProfileModelLocator has to be set");
        }
    }

    public static function getInstance():ProfileModelLocator {
        if (ProfileModelLocator.model == null) {
            ProfileModelLocator.model = new ProfileModelLocator();
        }

        return ProfileModelLocator.model;
    }

    // getters delegates

    public function getProfileDelegate():IGetProfileDelegate {
        if (this.profileDelegate == null) {
            this.profileDelegate = new GetProfileDelegate();
        }
        return this.profileDelegate;
    }
}
}
