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

package org.ow2.kerneos.profile.jonas;

import org.ow2.kerneos.profile.config.generated.Profile;
import org.ow2.util.archive.api.IFileArchive;
import org.ow2.util.ee.deploy.impl.deployable.AbsDeployable;
import org.ow2.util.plan.deploy.deployable.api.FileDeployable;

public class KerneosProfileDeployable extends AbsDeployable<KerneosProfileDeployable>
        implements FileDeployable<KerneosProfileDeployable, Profile> {

    /**
     * Namespace for kerneos profile.
     */
    public static final String NAMESPACE = "http://jasmine.ow2.org/xsds/kerneos-profile-2.1.xsd";

    /**
     * Transported JAXB metadata.
     */
    private Profile data;


    /**
     * Defines and create a deployable for the given archive.
     *
     * @param archive the given archive.
     */
    public KerneosProfileDeployable(IFileArchive archive) {
        super(archive);
    }


    public Profile getAttachedData() {
        return data;
    }

    public void setAttachedData(Profile attachedData) {
        this.data = attachedData;
    }

}
