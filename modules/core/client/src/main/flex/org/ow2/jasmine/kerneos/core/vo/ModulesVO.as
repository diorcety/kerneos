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
 * $Id$
 */

package org.ow2.jasmine.kerneos.core.vo
{
import com.adobe.cairngorm.vo.IValueObject;

import mx.collections.ArrayCollection;
import mx.controls.Alert;


/**
 * Modules.
 *
 * @author Guillaume Renault
 * @see ModuleVO
 */
[RemoteClass(alias="org.ow2.jasmine.kerneos.config.generated.Modules")]
[Bindable]
public class ModulesVO implements IValueObject
{

    /**
     * The folders.
     */
    [ArrayElementType('org.ow2.jasmine.kerneos.core.vo.FolderVO')]
    private var m_folder : ArrayCollection;
    
    public function set folder(p_folder : ArrayCollection) : void {
        this.m_folder = p_folder;
    }
    
    public function get folder() : ArrayCollection {
        if (this.m_folder == null) {
            this.m_folder = new ArrayCollection();
        }
        return this.m_folder;
    }
    
    /**
     * The SWF modules.
     */
    [ArrayElementType('org.ow2.jasmine.kerneos.core.vo.SWFModuleVO')]
    private var m_swfModule : ArrayCollection;
    
    public function set swfModule(p_swfModule : ArrayCollection) : void {
        this.m_swfModule = p_swfModule;
    }
    
    public function get swfModule() : ArrayCollection {
        if (this.m_swfModule == null) {
            this.m_swfModule = new ArrayCollection();
        }
        return this.m_swfModule;
    }
    
    /**
     * The IFrame modules.
     */
    [ArrayElementType('org.ow2.jasmine.kerneos.core.vo.IFrameModuleVO')]
    private var m_iframeModule : ArrayCollection;
    
    public function set iframeModule(p_iframeModule : ArrayCollection ) : void {
        this.m_iframeModule = p_iframeModule;
    }
    
    public function get iframeModule() : ArrayCollection {
        if (this.m_iframeModule == null) {
            this.m_iframeModule = new ArrayCollection();
        }
        return this.m_iframeModule;
    }
    
    /**
     * The Link modules.
     */
    [ArrayElementType('org.ow2.jasmine.kerneos.core.vo.LinkVO')]
    private var m_link : ArrayCollection;

    public function set link(p_link : ArrayCollection) : void {
        this.m_link = p_link;
    }
    
    public function get link() : ArrayCollection {
        if (this.m_link == null) {
            this.m_link = new ArrayCollection();
        }
        return this.m_link;
    }

    // =========================================================================
    // Public methods
    // =========================================================================

    /**
    * Get all modules.
    */
    public function get allModules() : ArrayCollection {
        var result : ArrayCollection = new ArrayCollection();
        
        if (this.m_folder != null) {
            for each ( var _folder : ModuleVO in this.m_folder) {
                result.addItem(_folder);
            }    
        }
        
        if (this.m_link != null) {
            for each ( var _link : ModuleVO in this.m_link) {
                result.addItem(_link);
            }    
        }
        
        if (this.m_iframeModule != null) {
            for each ( var _iframeModule : ModuleVO in this.m_iframeModule) {
                result.addItem(_iframeModule);
            }    
        }
        
        if (this.m_swfModule != null) {
            for each ( var _swfModule : ModuleVO in this.m_swfModule) {
                result.addItem(_swfModule);
            }    
        }
        
        return result;
    }
    
    public function set allModules(_allModules : ArrayCollection) : void {
        // do nothing
    }

}
}
