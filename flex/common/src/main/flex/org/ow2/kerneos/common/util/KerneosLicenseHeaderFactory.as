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
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.kerneos.common.util
{

/**
* A static class to easily get Kerneos project license headers, for different
* target languages.
* 
* @author Julien Nicoulaud
*/
public class KerneosLicenseHeaderFactory
{
    
    // =========================================================================
    // Variables
    // =========================================================================
    
    /**
     * The default LGPL header for the project Kerneos
     */
    private static var KERNEOS_LGPL_HEADER : String =
        "Kerneos\n" +
        "Copyright (C) [[[YEAR]]] Bull S.A.S.\n" +
        "Contact: jasmine AT ow2.org\n" +
        "\n" +
        "This library is free software; you can redistribute it and/or\n" +
        "modify it under the terms of the GNU Lesser General Public\n" +
        "License as published by the Free Software Foundation; either\n" +
        "version 2.1 of the License, or any later version.\n" +
        "\n" +
        "This library is distributed in the hope that it will be useful,\n" +
        "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
        "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n" +
        "Lesser General Public License for more details.\n" +
        "\n" +
        "You should have received a copy of the GNU Lesser General Public\n" +
        "License along with this library; if not, write to the Free Software\n" +
        "Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307\n" +
        "USA";
    
    
    // =========================================================================
    // Static methods
    // =========================================================================

    /**
    * Get the LGPL License header for the project Kerneos, without any
    * formatting
    */
    public static function getHeader():String
    {
        var date:Date = new Date();
        return String(KERNEOS_LGPL_HEADER).replace("[[[YEAR]]]",String(date.getFullYear()));
    }
    
    /**
    * Get the LGPL License header for the project Kerneos,
    * in Java-style comment
    */
    public static function getJavaHeader():String
    {
        // The RegExp that matches new lines
        var newLine:RegExp = /\n/gm;
        
        // Return the Java-comment-style formatted header
        return "/**\n * "
             + getHeader().replace(newLine,"\n * ")
             + "\n */";
    }
    
    /**
    * Get the LGPL License header for the project Kerneos,
    * in XML-style comment
    */
    public static function getXMLHeader():String
    {
        // The RegExp that matches new lines
        var newLine:RegExp = /\n/gm;
        
        // Return the Java-comment-style formatted header
        return "<!--\n - "
             + getHeader().replace(newLine,"\n - ")
             + "\n-->";
    }
    
    /**
    * Get the LGPL License header for the project Kerneos,
    * in DRL-style comment (Drools)
    */
    public static function getDRLHeader():String
    {
        // Return the Java-comment-style formatted header
        return getJavaHeader();
    }
}
}
