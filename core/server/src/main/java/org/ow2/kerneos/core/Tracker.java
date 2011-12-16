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

package org.ow2.kerneos.core;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.osgi.util.tracker.BundleTracker;
import org.ow2.kerneos.common.KerneosConstants;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.io.IOException;

/**
 * This is the component which handles the arrival/departure of the Kerneos' modules.
 */
@Component
@Instantiate
public final class Tracker {
    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(Tracker.class);

    @Requires
    private ICore kerneosCore;

    private BundleTracker bundleTracker;
    private BundleContext bundleContext;

    /**
     * Class used for tracking Kerneos' modules.
     */
    private class KerneosBundleTracker extends BundleTracker {

        /**
         * Constructor of the Kerneos bundle tracker.
         *
         * @param bundleContext the current bundle context.
         */
        KerneosBundleTracker(final BundleContext bundleContext) {
            super(bundleContext, Bundle.ACTIVE, null);
        }

        /**
         * Called by the OSGi Framework when a new bundle arrived.
         *
         * @param bundle is the new bundle.
         */
        @Override
        public Object addingBundle(final Bundle bundle, org.osgi.framework.BundleEvent event) {
            String moduleHeader = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_MODULE_MANIFEST);
            String applicationHeader = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_APPLICATION_MANIFEST);

            if (moduleHeader != null) {
                try {
                    onModuleArrival(bundle, moduleHeader);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
            if (applicationHeader != null) {
                try {
                    onApplicationArrival(bundle, applicationHeader);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
            return bundle;
        }

        /**
         * Called by the OSGi Framework on a bundle departure.
         *
         * @param bundle is the bundle which is gone.
         */
        @Override
        public void removedBundle(final Bundle bundle, org.osgi.framework.BundleEvent event, Object object) {
            String moduleHeader = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_MODULE_MANIFEST);
            String applicationHeader = (String) bundle.getHeaders().get(KerneosConstants.KERNEOS_APPLICATION_MANIFEST);

            if (moduleHeader != null) {
                try {
                    onModuleDeparture(bundle, moduleHeader);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
            if (applicationHeader != null) {
                try {
                    onApplicationDeparture(bundle, applicationHeader);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    /**
     * Constructor used by iPojo.
     *
     * @param bundleContext is the current bundle context.
     * @throws Exception can't load the JAXBContext.
     */
    private Tracker(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        bundleTracker = new KerneosBundleTracker(bundleContext);
    }

    /**
     * Called when all the component dependencies are met.
     */
    @Validate
    private void start() throws IOException {
        LOGGER.debug("Start Kerneos Tracker");

        // Start to track bundles
        bundleTracker.open();
    }

    /**
     * Called when all the component dependencies aren't met anymore.
     */
    @Invalidate
    private void stop() throws IOException {
        LOGGER.debug("Stop Kerneos Tracker");

        // Stop the tracking of bundles
        bundleTracker.close();
    }

    /**
     * Call when a new Kerneos Module is arrived.
     *
     * @param bundle The Bundle corresponding to the module.
     * @param name   The name of the module.
     */
    private void onApplicationArrival(final Bundle bundle, final String name) {
        LOGGER.debug("Kerneos Application Arrival: " + bundle.getSymbolicName());

        try {
            kerneosCore.addApplication(bundle);
        } catch (Exception e) {
            LOGGER.error("Can't create the application \"" + bundle.getSymbolicName() + "\": " + e);
        }
    }

    /**
     * Call when a new Kerneos Module is gone.
     *
     * @param bundle The Bundle corresponding to the module.
     * @param name   The name of the module.
     */
    private void onApplicationDeparture(final Bundle bundle, final String name) {
        LOGGER.debug("Application Module Departure: " + bundle.getSymbolicName());
        try {
            kerneosCore.removeApplication(bundle);
        } catch (Exception e) {
            LOGGER.error("Can't remove Application \"" + bundle.getSymbolicName() + "\"", e);
        }
    }


    /**
     * Call when a new Kerneos Module is arrived.
     *
     * @param bundle The Bundle corresponding to the module.
     * @param name   The name of the module.
     */
    private void onModuleArrival(final Bundle bundle, final String name) {
        LOGGER.debug("Kerneos Module Arrival: " + bundle.getSymbolicName());
        try {
            kerneosCore.addModule(bundle);
        } catch (Exception e) {
            LOGGER.error("Issue(s) during Module \"" + bundle.getSymbolicName() + "\" loading", e);
        }
    }

    /**
     * Call when a new Kerneos Module is gone.
     *
     * @param bundle The Bundle corresponding to the module.
     * @param name   The name of the module.
     */
    private void onModuleDeparture(final Bundle bundle, final String name) {
        LOGGER.debug("Kerneos Module Departure: " + bundle.getSymbolicName());
        try {
            kerneosCore.removeModule(bundle);
        } catch (Exception e) {
            LOGGER.error("Can't remove Module \"" + bundle.getSymbolicName() + "\"", e);
        }
    }
}
