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
package org.ow2.kerneos.core.vo {
import com.adobe.cairngorm.vo.IValueObject;

import mx.core.UIComponent;

import org.ow2.kerneos.common.util.IconUtility;


/**
 * A link displayed in Kerneos.
 *
 * @author Julien Nicoulaud
 */
[RemoteClass(alias="org.ow2.kerneos.core.config.generated.Link")]
[Bindable]
public class LinkVO extends ModuleVO implements IValueObject {

    // =========================================================================
    // Properties
    // =========================================================================

    // Assets

    /**
     * Default link big icon (64x64).
     */
    [Transient]
    [Embed(source="/../assets/link64.png")]
    public static var defaultLinkBigIcon:Class;

    // Fields

    /**
     * The web page URL.
     */
    public var url:String = null;


    // =========================================================================
    // Public methods
    // =========================================================================

    /**
     * Get the the big icon asset.
     */
    override public function getBigIcon(target:UIComponent, obj:Boolean = false):Object {
        // If no icon specified, return the default one
        if (bigIcon == null) {
            return defaultLinkBigIcon;
        }
        else {
            return super.getBigIcon(target, obj);
        }
    }
}
}
