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

package org.ow2.jasmine.kerneos.core.vo
{
import com.adobe.cairngorm.vo.IValueObject;

import org.granite.util.Enum;


/**
 * Describes all the differents way to close a window.
 *
 * @author Guillaume Renault
 * @see ModuleWithWindowVO
 */
[RemoteClass(alias="org.ow2.jasmine.kerneos.config.generated.PromptBeforeClose")]
[Bindable]
public class PromptBeforeCloseVO extends Enum implements IValueObject
{
    public static const DEFAULT : PromptBeforeCloseVO = new PromptBeforeCloseVO("DEFAULT", _);
    public static const NEVER : PromptBeforeCloseVO = new PromptBeforeCloseVO("NEVER",_);
    public static const ALWAYS : PromptBeforeCloseVO = new PromptBeforeCloseVO("ALWAYS",_);

    function PromptBeforeCloseVO(value:String = null, restrictor:* = null) {
        super((value || DEFAULT.name), restrictor);
    }
    
    override protected function getConstants():Array {
        return constants;
    }

    public static function get constants():Array {
        return [DEFAULT, NEVER, ALWAYS];
    } 

}
}
