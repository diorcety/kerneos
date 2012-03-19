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

import mx.core.FlexGlobals;

import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

[Bindable]
public class LanguageVO implements IValueObject {

    private var _name:String;

    private var _locale:String;

    public function LanguageVO() {
    }

    public function get name():String {
        return _name;
    }

    public function set name(value:String):void {
        _name = value;
    }

    public function get locale():String {
        return _locale;
    }

    public function set locale(value:String):void {
        _locale = value;
    }

    [Transient]
    public function get icon():Object {
        var style:String = _locale;
        // Remove invalid characters
        style = style.replace(/_/gi, "");
        style = style.replace(/-/gi, "");
        var css:CSSStyleDeclaration = FlexGlobals.topLevelApplication.styleManager.getStyleDeclaration(".flags");
        if (!css)
            return null;
        return css.getStyle(style);
    }

    [Transient]
    public function get label():String {
        return _name;
    }

    //
    // Not Used
    //
    public function set icon(icon:Object):void {

    }

    //
    // Not Used
    //
    public function set label(label:String):void {

    }
}
}
