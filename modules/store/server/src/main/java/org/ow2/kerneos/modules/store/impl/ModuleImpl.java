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

package org.ow2.kerneos.modules.store.impl;

import org.ow2.kerneosstore.api.Category;
import org.ow2.kerneosstore.api.Module;
import org.ow2.kerneosstore.api.ModuleVersion;
import org.ow2.kerneosstore.web.CategoryElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlRootElement(name = "module")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModuleImpl implements ModuleVersion {
    private static Pattern versionPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

    private Long id;
    private String version;
    private String name;
    private Date date;
    private String description;
    private String author;
    private String url;
    private Collection<CategoryImpl> categories;

    public ModuleImpl()
    {

    }

    public ModuleImpl(ModuleVersion moduleVersion) {
        id = moduleVersion.getModule().getId();
        version = moduleVersion.getMajor() + "." + moduleVersion.getMinor() + "." + moduleVersion.getRevision();
        name = moduleVersion.getName();
        date = moduleVersion.getDate();
        description = moduleVersion.getDescription();
        author = moduleVersion.getAuthor();
        url = moduleVersion.getUrl();
        categories = new LinkedList<CategoryImpl>();
        for (Category category : moduleVersion.getModule().getCategories()) {
            categories.add(new CategoryImpl(category));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public Collection<CategoryImpl> getCategories() {
        return categories;
    }

    @Override
    public Module getModule() {
        return new SimpleModule(id);
    }

    @Override
    public Integer getMajor() {
        Matcher matcher = versionPattern.matcher(version);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid version format");
        }
        return Integer.parseInt(matcher.group(1));
    }

    @Override
    public Integer getMinor() {
        Matcher matcher = versionPattern.matcher(version);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid version format");
        }
        return Integer.parseInt(matcher.group(2));
    }

    @Override
    public Integer getRevision() {
        Matcher matcher = versionPattern.matcher(version);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid version format");
        }
        return Integer.parseInt(matcher.group(3));
    }

    class SimpleModule implements Module {
        private Long id;

        SimpleModule(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public Collection getVersions() {
            return null;
        }

        @Override
        public Collection getCategories() {
            return null;
        }
    }
}
