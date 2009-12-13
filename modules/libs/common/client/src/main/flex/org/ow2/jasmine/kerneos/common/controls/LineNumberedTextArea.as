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
package org.ow2.jasmine.kerneos.common.controls
{
import flash.display.DisplayObject;
import flash.events.Event;
import flash.text.TextFieldType;
import flash.text.TextFormat;

import mx.controls.TextArea;
import mx.core.UITextField;
import mx.core.mx_internal;

/**
* A TextArea with line numbers on the left side
*  
* @author Julien Nicoulaud
*/
public class LineNumberedTextArea extends TextArea
{
    // =========================================================================
    // Properties
    // =========================================================================
    
    /**
    * Wether the line numbers should be displayed
    * 
    * @private
    */
    private var _showLineNumbers:Boolean;
    
    /**
    * The display object added to the TextArea
    * 
    * @private
    */
    private var _lineNumberField:UITextField;
    
    /**
    * The width of one character in pixels
    * 
    * @private
    */
    private var _characterWidth:Number = -1;
    
    /**
    * The default style format applicated to the line numbers
    */
    private var defaultLineNumbersFormat:TextFormat = new TextFormat();
    
    
    // =========================================================================
    // Constructor
    // =========================================================================

    /**
    * Build a new line numbered TextArea 
    */
    public function LineNumberedTextArea()
    {
        // Call super class constructor
        super();
        
        // Initialize some properties
        defaultLineNumbersFormat.color = 0x888888;
        defaultLineNumbersFormat.bold = true;
        
        // Set properties default values
        showLineNumbers = true;
    }
    
    
    // =========================================================================
    // Initialization
    // =========================================================================
    
    /**
    * Show the line numbers
    */
    protected function addLineNumbers():void
    {
        // Create our UIComponent
        _lineNumberField = new UITextField();
        _lineNumberField.autoSize = "left";
        _lineNumberField.type = TextFieldType.DYNAMIC;
        
        // Add it to the stage
        addChild(DisplayObject(_lineNumberField));
        
        // Listen to events
        addEventListener(Event.ENTER_FRAME,updateLineNumbers);
    }
    
    /**
    * Remove the line numbers
    */
    protected function removeLineNumbers():void
    {
        // Stop listening to events
        removeEventListener(Event.ENTER_FRAME,updateLineNumbers);
           
        // Remove the UIComponent
        removeChild(_lineNumberField);
    }
    
    
    // =========================================================================
    // Getters & setters
    // =========================================================================
    
    /**
    * Set the showLineNumbers property
    */
    public function set showLineNumbers(value:Boolean):void
    {
        // Show or hide the line numbers if the value was modified
        if (_showLineNumbers !== value) {
            if (value) {
                addLineNumbers();
            } else {
                removeLineNumbers();
            }
        }
        
        // Assign the value
        _showLineNumbers = value;
    }
    
    /**
    * Get the showLineNumbers property
    */
    public function get showLineNumbers():Boolean
    {
        return _showLineNumbers;
    }
    
    
    // =========================================================================
    // Line numbering
    // =========================================================================

    /**
    * Update the line numbers display object
    * 
    * @private
    */
    private function updateLineNumbers(event:Event=null):void
    {
        // Calculate the numbers of lines of the TextArea
        var firstLineNumber : int = (this.verticalScrollPosition<1)?1:int(this.verticalScrollPosition+1);
        
        // Generate the text to display
        var lineNumber:String = "";
        var i : int = firstLineNumber;
        var visibleTextHeight:Number = super.getLineMetrics(i-1).height;
        
        // Loop on the visible lines
        while(visibleTextHeight < this.height && i <= super.mx_internal::getTextField().numLines)
        {
            // If this is not the first line, start a new line
            if(i != firstLineNumber)
            {
                lineNumber += "\n";
            }
            
            // Add the line number
            lineNumber += i + "";
            
            // If this is not the last line, measure the next line
            if(i<super.mx_internal::getTextField().numLines){
                visibleTextHeight+=super.getLineMetrics(i).height;
            }
            
            // Step to the next line
            i++;
        }
        
        // Set it as the text to display
        _lineNumberField.text = lineNumber;
        
        // Calculate the new length of the line numbers
        var lengthOfLineNumber : int =
           int(String(this.verticalScrollPosition+this._lineNumberField.numLines).length);
        
        // Calculate the width of a number if not already done
        if(_characterWidth <= 0) {
            _characterWidth =  this._lineNumberField.textWidth / lengthOfLineNumber;
        }
        
        // Calculate the width of the display commponent
        _lineNumberField.width = (_characterWidth*lengthOfLineNumber) + 7;
        
        // Set the format style
        _lineNumberField.setTextFormat(defaultLineNumbersFormat);
        
        // Commit properties
        _lineNumberField.validateNow();
        
        // Shift the TextArea text to the right
        setStyle("paddingLeft", (_lineNumberField.width + 4));

    }

}
}
