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

package org.ow2.kerneos.modules.store;

import org.ow2.kerneos.modules.store.impl.CategoryImpl;
import org.ow2.kerneos.modules.store.impl.ModuleImpl;
import org.ow2.kerneos.modules.store.impl.StoreImpl;

import java.util.Collection;

public interface IStoreService {
    public StoreImpl getStore(String url);
    public ModuleImpl getModule(String id);
    public byte[] getModuleImage(String id);
    public Collection<ModuleImpl> searchModules(String filter, String field, String order,
                                                Integer itemByPage, Integer page);
    public Collection<ModuleImpl> searchModulesByCategory(String id, String field, String order,
                                                          Integer itemByPage, Integer page);
    public Collection<ModuleImpl> getCategories();
    public CategoryImpl getCategory(String id);
    public String downloadModule(String id);
}
