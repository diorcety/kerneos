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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.kerneos.core.api {

import com.adobe.cairngorm.business.ServiceLocator;

import flash.events.Event;

import mx.binding.utils.BindingUtils;
import mx.binding.utils.ChangeWatcher;
import mx.collections.ArrayCollection;

import org.ow2.kerneos.common.vo.ModuleVO;
import org.ow2.kerneos.login.model.LoginModelLocator;
import org.ow2.kerneos.profile.manager.ProfileManager;
import org.ow2.kerneos.profile.model.ProfileModelLocator;
import org.ow2.kerneos.profile.vo.ProfileVO;

import spark.modules.Module;

import mx.core.mx_internal;

use namespace mx_internal;

[Event(name="open", type="flash.events.Event")]
[Event(name="close", type="flash.events.Event")]
public class KerneosModule extends Module {

    private var __configuration:ModuleVO;
    private var __serviceLocator:ServiceLocator;
    private var __profileWatcher:ChangeWatcher;
    private var __rolesWatcher:ChangeWatcher;

    public function KerneosModule() {
        __serviceLocator = new ServiceLocator();
    }

    public function getConfiguration():ModuleVO {
        return __configuration;
    }

    public function getServices():ServiceLocator {
        return __serviceLocator;
    }

    [Bindable(event="haveAccess_change")]
    public function haveAccess(serviceId:String, methodId:String = null):Boolean {
        return ProfileManager.haveServiceAccess(ProfileModelLocator.getInstance().profile, LoginModelLocator.getInstance().session.roles, __configuration.bundle, serviceId, methodId);
    }

    public function canBeClosedWithoutPrompt():Boolean {
        return true;
    }

    /////////////////////////
    /// INTERNAL USE ONLY ///
    /////////////////////////

    private function updatedProfile(profile:ProfileVO):void {
        dispatchEvent(new Event("haveAccess_change"));
    }

    private function updatedRoles(roles:ArrayCollection):void {
        dispatchEvent(new Event("haveAccess_change"));
    }

    mx_internal function openModule():void {
        __profileWatcher = BindingUtils.bindSetter(updatedProfile, ProfileModelLocator.getInstance(), "profile");
        __rolesWatcher = BindingUtils.bindSetter(updatedRoles, LoginModelLocator.getInstance().session, "roles");
        dispatchEvent(new Event("open"));
    }


    mx_internal function closeModule():void {
        dispatchEvent(new Event("close"));
        __profileWatcher.unwatch();
        __rolesWatcher.unwatch();
    }

    mx_internal function setConfiguration(configuration:ModuleVO):void {
        __configuration = configuration;
    }
}
}
