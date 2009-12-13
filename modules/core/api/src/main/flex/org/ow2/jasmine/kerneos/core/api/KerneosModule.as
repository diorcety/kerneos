/**
 * JASMINe
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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.core.api
{

/**
* Any Kerneos module can implement this interface in order to have the member
* methods triggered.
* 
* @author Julien Nicoulaud
*/
public interface KerneosModule
{
    /**
    * Returns true if Kerneos can close the module without prompting the user.
    */
    function canBeClosedWithoutPrompt():Boolean;

    /**
    * Method called before the module is unloaded. Use it to close client-server
    * connexions for example.
    */
    function closeModule():void;
    
}
}
