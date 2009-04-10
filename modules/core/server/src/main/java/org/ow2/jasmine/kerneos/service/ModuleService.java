/**
 * JASMINe
 * Copyright (C) 2008 Bull S.A.S.
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
 * $Id: ModuleService.java 2665 2009-03-01 15:08:05Z
 * Jean-Pierre & Tianyi
 * --------------------------------------------------------------------------
 */


package org.ow2.jasmine.kerneos.service;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class ModuleService implements Serializable {

	private static final long serialVersionUID = 7807669487844076133L;

	public List<String> modules() {

		List<String> modulesList = new ArrayList<String>();

		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("../../");
			File dir = new File(url.toURI());
			File[] filesOfDir = dir.listFiles();

			for (int i = 0; i < filesOfDir.length; i++) {
				String s = filesOfDir[i].getName();
				if (s.endsWith("Loader.swf")) {
					modulesList.add(s);
				}
			}
		}
		catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.INFO,"error when getting loader of the archive.");
		}
		return modulesList;
	}
}
