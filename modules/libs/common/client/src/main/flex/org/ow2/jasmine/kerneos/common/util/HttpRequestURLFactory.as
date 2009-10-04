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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jasmine.kerneos.common.util
{
import flash.external.ExternalInterface;


/**
 * A util static class that builds the URL to call for a HTTP request. Adds the
 * session ID to the URL to respect authentication.
 *
 * @example
 * <listing>
 *     var request:URLRequest = new URLRequest();
 *     request.url = HttpRequestURLFactory.getURL("./PathToMyServlet");
 * </listing>
 *
 * @author Julien Nicoulaud
 */
public class HttpRequestURLFactory
{
    /**
     * The user session ID
     */
    private static var jsessionid : String = null;
    
    
    
    /**
     * Build the URL to call with the session ID
     */
    public static function getURL(url : String) : String
    {
        if (jsessionid == null)
        {
            
            // Get the cookie
            ExternalInterface.call('eval', 'window.getCookie = function () {return document.cookie};');
            var cookie : String = ExternalInterface.call('getCookie');
            
            // Extract the JSESSIONID value
            // FIXME not safely done
            var regexp : RegExp = new RegExp("JSESSIONID=([\\d\\w]*)", "ig");
            cookie = cookie.match(regexp)[0];
            jsessionid = cookie.split("=")[1];
        }
        return url + ";jsessionid=" + jsessionid;
    }

}
}
