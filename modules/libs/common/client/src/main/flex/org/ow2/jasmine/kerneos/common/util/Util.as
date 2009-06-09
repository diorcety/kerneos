/**
 * JASMINe
 * Copyright (C) 2008 Bull S.A.S.
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
 * 
 */
 
 
package org.ow2.jasmine.kerneos.common.util
{
	import mx.controls.Alert;
	
	public class Util
	{
		
		/**
		 * This function parse the input URL and return the associated context
		 * 
		 */
		public static function parserURL(s:String):String
		{	
			var context:String = new String();
			var defaut_protocol:String = "http://"
			
			// remove the protocol on the url string
			var s_without_protocol:String = s.substring(defaut_protocol.length,s.length);
			
			// splicing the string into an array
			var tokens:Array = s_without_protocol.split("/");
			
			for(var i:int = 1;  i< tokens.length - 1; i++){
				context += tokens[i].toString();
				if (i < tokens.length - 2) {
					context += "/";
				}
			}
			
			return context;
		}

	}
}