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

import mx.utils.StringUtil;


/**
 * A static class that provides utility functions to manipulate Strings.
 *
 * @author Julien Nicoulaud
 */
public class StringUtils
{

    // =========================================================================
    // Get informations about the String
    // =========================================================================

    /**
     * Determines whether the specified string contains text.
     *
     * @param string The string to check.
     * @return Boolean
     */
    public static function hasText(string : String) : Boolean
    {
        var str : String = removeExtraWhitespace(string);
        return !!str.length;
    }



    /**
     * Determines whether the specified string is numeric.
     *
     * @param string The string.
     * @return Boolean
     */
    public static function isNumeric(string : String) : Boolean
    {
        if (string == null)
        {
            return false;
        }
        var regx : RegExp = /^[-+]?\d*\.?\d+(?:[eE][-+]?\d+)?$/;
        return regx.test(string);
    }



    /**
     * Count the number of words in a string
     *
     * @param string The string.
     * @return uint the number of words
     */
    public static function wordCount(string : String) : uint
    {
        if (string == null)
        {
            return 0;
        }
        return string.match(/\b\w+\b/g).length;
    }



    /**
     * Parse the input URL and return the associated context.
     */
    public static function parseURLContext(s : String) : String
    {
        var context : String = new String();
        var defaut_protocol : String = "http://"

        // remove the protocol on the url string
        var s_without_protocol : String = s.substring(defaut_protocol.length, s.length);

        // splicing the string into an array
        var tokens : Array = s_without_protocol.split("/");

        for (var i : int = 1; i < tokens.length - 1; i++)
        {
            context += tokens[i].toString();

            if (i < tokens.length - 2)
            {
                context += "/";
            }
        }

        return context;
    }



    /**
     * Parse the String Boolean value.
     *
     * @return true
     *           if the String value is in {"1","true","yes"}.
     *         false
     *           if the String value is in {"0","false","no"}.
     *         Boolean(string)
     *           in every other case.
     */
    public static function parseBoolean(string : String) : Boolean
    {
        switch (string.toLowerCase())
        {
            case "1":
            case "true":
            case "yes":
                return true;
            case "0":
            case "false":
            case "no":
                return false;
            default:
                return Boolean(string);
        }
    }



    // =========================================================================
    // Format the String
    // =========================================================================

    /**
     * Escapes all of the characters in a string to create a friendly "quotable" sting
     *
     * @param string The string that will be modified
     * @return String The modified String
     */
    public static function quote(string : String) : String
    {
        var regx : RegExp = /[\\"\r\n]/g;
        return '"' + string.replace(regx, _quote) + '"';
    }



    /**
     * Removes extra whitespaces (extra spaces, tabs, line breaks, etc) from the
     * specified string.
     *
     * @param string The String whose extra whitespaces will be removed.
     * @return String The modified String
     */
    public static function removeExtraWhitespace(string : String) : String
    {
        if (string == null)
        {
            return '';
        }
        var str : String = StringUtil.trim(string);
        return str.replace(/\s+/g, ' ');
    }



    /**
     * Remove all < and > based tags from a string
     *
     * @param string The source string.
     * @return String The modified String
     */
    public static function stripTags(string : String) : String
    {
        if (string == null)
        {
            return '';
        }
        return string.replace(/<\/?[^>]+>/igm, '');
    }



    /**
     * Remove whitespace from the front (left-side) of the specified string
     *
     * @param string The String whose beginning whitespace will be removed.
     * @return String The modified String
     */
    public static function trimLeft(string : String) : String
    {
        if (string == null)
        {
            return '';
        }
        return string.replace(/^\s+/, '');
    }



    /**
     * Remove whitespaces from the end (right-side) of the specified string
     *
     * @param string The String whose ending whitespace will be removed.
     * @return String The modified String
     */
    public static function trimRight(string : String) : String
    {
        if (string == null)
        {
            return '';
        }
        return string.replace(/\s+$/, '');
    }



    /**
     * Indent each line of the String with 4 spaces
     *
     * @param string The String that will be indented
     * @return String The modified String
     */
    public static function indent(string : String) : String
    {
        if (string == null)
        {
            return '';
        }
        return string.replace(/^(.*)/gm, '    $1');
    }



    /**
     * Unindent each line of the String if possible
     *
     * @param string The String that will be unindented
     * @return String The modified String
     */
    public static function unIndent(string : String) : String
    {
        if (string == null)
        {
            return '';
        }
        return string.replace(/^\s\s\s\s(.*)/gm, '$1');
    }



    // =========================================================================
    // Private helper methods
    // =========================================================================

    /**
     * Escape the given String
     */
    private static function _quote(string : String, ... args) : String
    {
        switch (string)
        {
            case "\\":
                return "\\\\";
            case "\r":
                return "\\r";
            case "\n":
                return "\\n";
            case '"':
                return '\\"';
            default:
                return '';
        }
    }
}
}
