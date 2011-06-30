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
package org.ow2.kerneos.profile.manager {
import com.adobe.cairngorm.business.ServiceLocator;

import mx.collections.ArrayCollection;
import mx.messaging.events.MessageEvent;

import org.granite.gravity.Consumer;
import org.ow2.kerneos.profile.model.ProfileModelLocator;

import org.ow2.kerneos.profile.vo.ProfileVO;
import org.ow2.kerneos.profile.vo.ProfileBundleVO;
import org.ow2.kerneos.profile.vo.ProfileMethodVO;
import org.ow2.kerneos.profile.vo.ProfileModuleVO;
import org.ow2.kerneos.profile.vo.ProfilePolicyVO;
import org.ow2.kerneos.profile.vo.ProfileRuleVO;
import org.ow2.kerneos.profile.vo.ProfileServiceVO;

public class ProfileManager {
    /**
     * The gravity consumer for asynchronous OSGi communication
     */
    private static var consumer:Consumer = null;

    public function ProfileManager() {
    }


    /**
     * Subscribe a gravity consumer to the kerneos topic
     */
    public static function subscribe():void {
        consumer = ServiceLocator.getInstance().getConsumer("kerneosAsyncProfileService");
        consumer.addEventListener(MessageEvent.MESSAGE, onModuleEventMessage);

        consumer.subscribe();
    }

    public static function unsubscribe():void {
        consumer.removeEventListener(MessageEvent.MESSAGE, onModuleEventMessage);

        consumer.unsubscribe();
    }

    /**
     * Receive the message from gravity consumer and change profile
     * @param event ModuleEventVO
     */
    private static function onModuleEventMessage(event:MessageEvent):void {
        var profile:ProfileVO = event.message.body as ProfileVO;
        ProfileModelLocator.getInstance().profile = profile;
    }

    public static function haveModuleAccess(profile:ProfileVO, roles:ArrayCollection, bundleId:String, moduleId:String):Boolean {
        if (profile == null)
            return false;

        var policy:ProfilePolicyVO = getPolicy(profile.defaultRules, roles, profile.defaultPolicy);

        var bundle:ProfileBundleVO = getBundle(profile.bundles, bundleId);
        if (bundle != null) {
            policy = getPolicy(bundle.rules, roles, policy);
            if (policy.equals(ProfilePolicyVO.DENY))
                return false;

            var module:ProfileModuleVO = getModule(bundle.modules, moduleId);
            if (module != null) {
                policy = getPolicy(module.rules, roles, policy);
                if (policy.equals(ProfilePolicyVO.DENY))
                    return false;

            }
        }

        return policy.equals(ProfilePolicyVO.ALLOW);
    }

    public static function haveServiceAccess(profile:ProfileVO, roles:ArrayCollection, bundleId:String, serviceId:String, methodId:String = null):Boolean {
        if (profile == null)
            return false;

        var policy:ProfilePolicyVO = getPolicy(profile.defaultRules, roles, profile.defaultPolicy);

        var bundle:ProfileBundleVO = getBundle(profile.bundles, bundleId);
        if (bundle != null) {
            policy = getPolicy(bundle.rules, roles, policy);
            if (policy.equals(ProfilePolicyVO.DENY))
                return false;

            var service:ProfileServiceVO = getService(bundle.services, serviceId);
            if (service != null) {
                policy = getPolicy(service.rules, roles, policy);
                if (policy.equals(ProfilePolicyVO.DENY))
                    return false;

                if (methodId != null) {
                    var method:ProfileMethodVO = getMethod(service.methods, methodId);
                    if (method != null) {
                        policy = getPolicy(method.rules, roles, policy);
                        if (policy.equals(ProfilePolicyVO.DENY))
                            return false;
                    }
                }
            }
        }
        return policy.equals(ProfilePolicyVO.ALLOW);
    }

    private static function getBundle(bundles:ArrayCollection, id:String):ProfileBundleVO {
        for each(var bundle:ProfileBundleVO in bundles) {
            if (bundle.id == id)
                return bundle;
        }
        return null;
    }

    private static function getService(services:ArrayCollection, id:String):ProfileServiceVO {
        for each(var service:ProfileServiceVO in services) {
            if (service.id == id)
                return service;
        }
        return null;
    }

    private static function getMethod(methods:ArrayCollection, id:String):ProfileMethodVO {
        for each(var method:ProfileMethodVO in methods) {
            if (method.id == id)
                return method;
        }
        return null;
    }

    private static function getModule(modules:ArrayCollection, id:String):ProfileModuleVO {
        for each(var module:ProfileModuleVO in modules) {
            if (module.id == id)
                return module;
        }
        return null;
    }

    private static function getPolicy(rules:ArrayCollection, roles:ArrayCollection, defaultPolicy:ProfilePolicyVO):ProfilePolicyVO {
        if (roles != null) {
            for each(var rule:ProfileRuleVO in rules) {
                if (roles.contains(rule.role))
                    return rule.policy;
            }
        }
        return defaultPolicy;
    }
}
}
