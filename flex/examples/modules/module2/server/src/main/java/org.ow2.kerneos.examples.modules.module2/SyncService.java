/**
 * Kerneos
 * Copyright (C) 2009-2011 Bull S.A.S.
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

package org.ow2.kerneos.examples.modules.module2;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import org.ow2.kerneos.core.service.KerneosSimpleService;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

//iPOJO annotations Component, Instantiate and Provides
@Component
@Instantiate
@Provides
public class SyncService implements KerneosSimpleService {
    /**
     * The logger
     */
    private static final Log LOGGER = LogFactory.getLog(SyncService.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * Event Admin.
     */
    @Requires
    private EventAdmin eventAdmin;


    @ServiceProperty(name = KerneosSimpleService.ID, value = "module2-serviceSync")
    private String id;


    /**
     * Start
     */
    @Validate
    private void start() {
        LOGGER.info("Start Module2 - ServiceSync");
    }

    /**
     * Stop
     */
    @Invalidate
    private void stop() {
        LOGGER.info("Stop Module2 - ServiceSync");
    }

    /**
     * Post a message on the chat
     *
     * @param post
     */
    public void post(Post post) {
        // Timestamp the post
        post.setDate(DATE_FORMAT.format(new Date()));

        // Send message using EventAdmin
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("user.data", post);
        Event event = new Event("kerneos-module2/chat", properties);
        eventAdmin.sendEvent(event);
    }
}
