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
package org.ow2.jasmine.kerneos.common.containers
{
import flash.events.*;
import mx.effects.AnimateProperty;
import mx.events.*;
import mx.containers.Panel;
import mx.core.ScrollPolicy;


/**
 * The icon designating a "closed" state
 */
[Style(name="closedIcon", property="closedIcon", type="Object")]

/**
 * The icon designating an "open" state
 */
[Style(name="openIcon", property="openIcon", type="Object")]

/**
 * This is a Panel that can be collapsed and expanded by clicking on the header.
 *
 * @author Ali Rantakari
 */
public class CollapsiblePanel extends Panel
{
    
    private var _creationComplete : Boolean = false;
    
    private var _open : Boolean = true;
    
    private var _openAnim : AnimateProperty;
    
    
    
    /**
     * Constructor
     *
     */
    public function CollapsiblePanel(aOpen : Boolean = true) : void
    {
        super();
        open = aOpen;
        this.addEventListener(FlexEvent.CREATION_COMPLETE, creationCompleteHandler);
    }
    
    
    
    /**
     * creation complete event handler
     */
    
    private function creationCompleteHandler(event : FlexEvent) : void
    {
        this.horizontalScrollPolicy = ScrollPolicy.OFF;
        this.verticalScrollPolicy = ScrollPolicy.OFF;
        
        _openAnim = new AnimateProperty(this);
        _openAnim.duration = 300;
        _openAnim.property = "height";
        
        titleBar.addEventListener(MouseEvent.CLICK, headerClickHandler);
        
        _creationComplete = true;
    }
    
    
    
    private function headerClickHandler(event : MouseEvent) : void
    {
        toggleOpen();
    }
    
    
    
    private function callUpdateOpenOnCreationComplete(event : FlexEvent) : void
    {
        updateOpen();
    }
    
    
    
    /**
     * sets the height of the component without animation, based
     * on the _open variable
     */
    private function updateOpen() : void
    {
        if (!_open)
            height = closedHeight;
        else
            height = openHeight;
        setTitleIcon();
    }
    
    
    
    /**
     * the height that the component should be when open
     */
    
    private function get openHeight() : Number
    {
        return measuredHeight;
    }
    
    
    
    /**
     * the height that the component should be when closed
     */
    private function get closedHeight() : Number
    {
        var hh : Number = getStyle("headerHeight");
        
        if (hh <= 0 || isNaN(hh))
            hh = titleBar.height;
        return hh;
    }
    
    
    
    /**
     * sets the correct title icon
     */
    private function setTitleIcon() : void
    {
        if (!_open)
            this.titleIcon = getStyle("closedIcon");
        else
            this.titleIcon = getStyle("openIcon");
    }
    
    
    
    /**
     * Collapses / expands this block (with animation)
     */
    public function toggleOpen() : void
    {
        if (_creationComplete && !_openAnim.isPlaying)
        {
            
            _openAnim.fromValue = _openAnim.target.height;
            
            if (!_open)
            {
                _openAnim.toValue = openHeight;
                _open = true;
                dispatchEvent(new Event(Event.OPEN));
            }
            else
            {
                _openAnim.toValue = _openAnim.target.closedHeight;
                _open = false;
                dispatchEvent(new Event(Event.CLOSE));
            }
            setTitleIcon();
            _openAnim.play();
            
        }
    
    }
    
    
    
    /**
     * Whether the block is in a expanded (open) state or not
     */
    public function get open() : Boolean
    {
        return _open;
    }
    
    
    
    /**
     * @private
     */
    public function set open(aValue : Boolean) : void
    {
        _open = aValue;
        
        if (_creationComplete)
            updateOpen();
        else
            this.addEventListener(FlexEvent.CREATION_COMPLETE, callUpdateOpenOnCreationComplete, false, 0, true);
    }
    
    
    
    /**
     * @private
     */
    override public function invalidateSize() : void
    {
        super.invalidateSize();
        
        if (_creationComplete)
            if (_open && !_openAnim.isPlaying)
                this.height = openHeight;
    }

}

}
