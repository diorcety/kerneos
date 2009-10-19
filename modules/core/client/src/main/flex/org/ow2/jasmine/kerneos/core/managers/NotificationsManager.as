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
package org.ow2.jasmine.kerneos.core.managers
{

import flash.events.Event;

import org.ow2.jasmine.kerneos.common.event.KerneosNotificationEvent;
import org.ow2.jasmine.kerneos.core.model.KerneosModelLocator;
import org.ow2.jasmine.kerneos.core.view.DesktopView;
import org.ow2.jasmine.kerneos.core.view.notification.NotificationPopUp;
import org.ow2.jasmine.kerneos.core.view.window.ModuleWindow;
import org.ow2.jasmine.kerneos.core.vo.KerneosNotification;
	

/**
 * Manages the notifications sent from modules to Kerneos.
 *
 * @author Julien Nicoulaud
 */
public class NotificationsManager
{
    
    // =========================================================================
    // Properties
    // =========================================================================
    
    /**
     * The desktop view on which operations are done.
     *
     * Must be set before calling the static functions.
     */
    [Bindable]
    public static var desktop : DesktopView = null;
    
    
    
    // =========================================================================
    // Public static methods
    // =========================================================================
    
    /**
     * Receive notification events from modules
     */
    public static function handleNotificationEvent(event : KerneosNotificationEvent) : void
    {
        // Identify the module that sent the notification
        var window : ModuleWindow = event.currentTarget as ModuleWindow;
        
        // Retrieve the model
        var model : KerneosModelLocator = KerneosModelLocator.getInstance();
        
        // Store the notification, if notifications log activated
        if (model.config.enableNotificationsLog)
        {
            model.notifications.addItem(new KerneosNotification(window.module, event.message, event.level));
        }
        
        // If this is not a debug notif, display visual notifications
        if (event.level !== KerneosNotificationEvent.DEBUG)
        {
            
            // Flash the taskbar button
            // It could also blink with window.minimizedModuleWindow.blink();
            if (event.level == KerneosNotificationEvent.WARNING)
            {
                window.minimizedModuleWindow.flash(0xCC6600);
            }
            else if (event.level == KerneosNotificationEvent.ERROR)
            {
                window.minimizedModuleWindow.flash(0xD34328);
            }
            else
            {
                window.minimizedModuleWindow.blink();
            }
            
            // If a PopUp should be shown and the window does not have the focus
            if (model.config.showNotificationPopUps && event.showPopup && (model.config.showPopupsFromActiveWindow || (!model.config.showPopupsFromActiveWindow && !window.hasFocus)))
            {
		        // Check that desktop is not null
		        checkDesktopNotNull();
                
                // Build and display the popup
                var notifPopUp : NotificationPopUp = new NotificationPopUp();
                notifPopUp.message = event.message;
                notifPopUp.level = event.level;
                notifPopUp.window = window;
                notifPopUp.setStyle("bottom", 0);
                notifPopUp.setStyle("right", 0);
                desktop.windowContainer.addChild(notifPopUp);
            }
        }
    }
    
    
    
    // =========================================================================
    // Private methods
    // =========================================================================
    
    /**
     * Check that the desktop view is referenced.
     */
    private static function checkDesktopNotNull(e : Event = null) : void
    {
        if (desktop == null)
        {
            throw new Error('the "desktop" property must be assigned before calling the modules' + ' life cycle manager methods.');
        }
    }
}
}
