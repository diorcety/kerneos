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

import flash.events.KeyboardEvent;
import flash.ui.Keyboard;

import org.ow2.jasmine.kerneos.common.util.StringUtils;


/**
 * An extended TextArea aimed to code edition. Uses a monospace font, displays
 * line numbers and accepts tabulations.
 *
 * @author Julien Nicoulaud
 */
public class CodeArea extends LineNumberedTextArea
{
    
    // =========================================================================
    // Constructor
    // =========================================================================
    
    /**
     * Build a new code TextArea
     */
    public function CodeArea()
    {
        // Call super class constructor
        super();
        
        // Set some properties
        this.setStyle("fontFamily", "monospace"), this.setStyle("fontSize", 12);
        
        // Listen to events
        addEventListener(KeyboardEvent.KEY_DOWN, onKeyDown, false, 100);
    }
    
    
    
    // =========================================================================
    // Events handling
    // =========================================================================
    
    /**
     * Intercept some keyboard events
     */
    private function onKeyDown(event : KeyboardEvent) : void
    {
        switch (event.keyCode)
        {
            case Keyboard.TAB:
                onTabKey(event);
                break;
            case Keyboard.ENTER:
                onEnterKey(event);
                break;
        }
    }
    
    
    
    /**
     * Handle the Tab key
     */
    private function onTabKey(event : KeyboardEvent) : void
    {
        // Calculate the begin and end indexes of the range to transform
        var beginLineIndex : int = textField.getLineIndexOfChar(selectionBeginIndex);
        
        if (beginLineIndex < 0)
        {
            beginLineIndex = textField.numLines - 1;
        }
        var endLineIndex : int = textField.getLineIndexOfChar(selectionEndIndex);
        
        if (endLineIndex < 0)
        {
            endLineIndex = textField.numLines - 1;
        }
        
        // Store the selection bounds
        var oldSelectionBeginIndex : int = selectionBeginIndex;
        var oldSelectionEndIndex : int = selectionEndIndex;
        var oldVerticalScrollPosition : int = verticalScrollPosition;
        
        var beginIndex : int;
        var endIndex : int;
        
        // If we are on single line
        if (beginLineIndex == endLineIndex)
        {
            
            // If unindenting
            if (event.shiftKey)
            {
                
                /*// Identify the text range to modify
                   beginIndex = textField.getLineOffset(beginLineIndex);
                   endIndex = textField.getLineOffset(endLineIndex) +
                   textField.getLineLength(endLineIndex);
                
                   EoSLogger.info(text.substring(beginIndex,endIndex));
                
                   // Build the new text
                   text = text.substring(0,beginIndex) +
                   StringUtils.unIndent(text.substring(beginIndex,endIndex)) +
                   text.substring(endIndex);
                
                   callLater(function():void {
                   selectionBeginIndex = Math.max(beginIndex,oldSelectionBeginIndex - 4);
                   selectionEndIndex = Math.max(beginIndex,oldSelectionBeginIndex - 4);;
                 });*/
            }
            
            // If indenting, just insert a tab
            else
            {
                textField.replaceSelectedText("    ");
            }
        }
        
        // If a code block is selected
        else
        {
            
            // Identify the text range to modify
            beginIndex = textField.getLineOffset(beginLineIndex);
            endIndex = textField.getLineOffset(endLineIndex) + textField.getLineLength(endLineIndex);
            
            // Modify the identified block
            var modifiedBlock : String;
            
            if (event.shiftKey)
            {
                modifiedBlock = StringUtils.unIndent(text.substring(beginIndex, endIndex));
            }
            else
            {
                modifiedBlock = StringUtils.indent(text.substring(beginIndex, endIndex));
            }
            
            // Build the new text
            text = text.substring(0, beginIndex) + modifiedBlock + text.substring(endIndex);
            
            callLater(function() : void
            {
                // Update the selection bounds
                selectionBeginIndex = beginIndex;
                selectionEndIndex = beginIndex + modifiedBlock.length - 1;
                
                // Prevent the component from reseting its scroll position
                verticalScrollPosition = oldVerticalScrollPosition;
            });
            
        }
        
        // Keep the focus
        callLater(this.setFocus);
    }
    
    
    
    /**
     * Handle the Enter key
     */
    private function onEnterKey(event : KeyboardEvent) : void
    {
        // If this is a selection, execute the default behaviour
        if (selectionBeginIndex !== selectionEndIndex)
        {
            textField.replaceSelectedText("\n");
        }
        
        // Else step to a newline and keep indentation
        else
        {
            
            // Store some vars
            var oldSelectionBeginIndex : int = selectionBeginIndex;
            var oldText : String = text;
            
            // Calculate the indentation
            var currentLineIndex : int = textField.getLineIndexOfChar(oldSelectionBeginIndex);
            
            if (currentLineIndex < 0)
            {
                currentLineIndex = textField.numLines - 1;
            }
            var currentLine : String = textField.getLineText(currentLineIndex);
            var insert : String = currentLine.replace(/^(\s*).*[^\n]/, '\n$1');
            
            // Replace the text and move the cursor
            callLater(function() : void
            {
                text = oldText.substring(0, oldSelectionBeginIndex) + insert + oldText.substring(oldSelectionBeginIndex);
                selectionBeginIndex = oldSelectionBeginIndex + insert.length;
                selectionEndIndex = oldSelectionBeginIndex + insert.length;
            });
            
        }
    }

}
}
