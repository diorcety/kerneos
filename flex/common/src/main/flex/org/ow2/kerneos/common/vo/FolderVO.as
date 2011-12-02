/**
 * Kerneos
 * Copyright (C) 2009 Bull S.A.S.
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
 * $Id$
 */
package org.ow2.kerneos.common.vo {

import com.adobe.cairngorm.vo.IValueObject;

import mx.collections.ArrayCollection;
import mx.core.UIComponent;

import org.ow2.kerneos.common.util.IconUtility;


/**
 * A group of modules displayed as a folder in Kerneos.
 *
 * @author Julien Nicoulaud
 */

[Bindable]
public class FolderVO extends ModuleWithWindowVO implements IValueObject {

    // =========================================================================
    // Properties
    // =========================================================================

    // Fields

    /**
     * The modules.
     */
    [ArrayElementType('org.ow2.kerneos.common.vo.ModuleVO')]
    public var modules:ArrayCollection = null;

    /**
     * Constructor
     */
    function FolderVO() {

    }

    // =========================================================================
    // Public methods
    // =========================================================================

    /**
     * Get the the small icon asset.
     */
    override public function getSmallIcon(target:UIComponent = null):Object {
        // If no icon specified, return the default one
        if (smallIcon == null) {
            if (target == null)
                return IconUtility.getObject("images/folder16.png", 16, 16);
            else
                return IconUtility.getClass(target, "images/folder16.png", 16, 16);
        }
        else {
            return super.getSmallIcon(target);
        }
    }

    /**
     * Get the the big icon asset.
     */
    override public function getBigIcon(target:UIComponent = null):Object {
        // If no icon specified, return the default one
        if (bigIcon == null) {
            if (target == null)
                return IconUtility.getObject("images/folder64.png", 64, 64);
            else
                return IconUtility.getClass(target, "images/folder64.png", 64, 64);
        }
        else {
            return super.getBigIcon(target);
        }
    }
}
}
