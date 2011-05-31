/**
 * Kerneos
 * Copyright (C) 2011 Bull S.A.S.
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

package org.ow2.kerneos.core.vo
{
import com.adobe.cairngorm.vo.IValueObject;

import org.granite.util.Enum;


/**
 * Describes all the differents way to close a window.
 *
 * @author Guillaume Renault
 * @see ModuleWithWindowVO
 */
[RemoteClass(alias="org.ow2.kerneos.core.config.generated.Authentication")]
[Bindable]
public class AuthenticationVO extends Enum implements IValueObject
{
    public static const NONE : PromptBeforeCloseVO = new PromptBeforeCloseVO("NONE", _);
    public static const FLEX : PromptBeforeCloseVO = new PromptBeforeCloseVO("FLEX",_);
    public static const WWW : PromptBeforeCloseVO = new PromptBeforeCloseVO("WWW",_);

    function AuthenticationVO(value:String = null, restrictor:* = null) {
        super((value || FLEX.name), restrictor);
    }
    
    override protected function getConstants():Array {
        return constants;
    }

    public static function get constants():Array {
        return [NONE, FLEX, WWW];
    } 

}
}
