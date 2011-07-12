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
package org.ow2.kerneos.common.view
{
import flash.display.DisplayObject;

import mx.controls.ComboBox;
import mx.controls.List;
import mx.core.ClassFactory;

/**
 * A ComboBox that can display icons for each items.
 * 
 * @example How to use it
 *   <listing version="3.0">
 *      // Declare your icons
 *      [Embed(source="/../assets/en_US.png")]
 *      [Bindable]
 *      public var en_USIcon:Class;
 *      
 *      [Embed(source="/../assets/fr_FR.png")]
 *      [Bindable]
 *      public var fr_FRIcon:Class;
 * 
 *      // Declare your data provider
 *      [Bindable]
 *      private var locales : Array = [{locale:"fr_FR",label:"Français", icon:fr_FRIcon},
 *                                     {locale:"en_US",label:"English", icon:en_USIcon}];
 * 
 *      // Declare a function to get each item icon
 *      private function getLocaleIcon(item:Object):Class
 *      {
 *          if(item.icon){
 *              return item.icon;
 *          }
 *          return null;
 *      }
 * 
 *      <!-- Add the component -->
 *        <view:IconComboBox id = "localeComboBox"
 *                           dataProvider = "{locales}"
 *                           labelField = "label"
 *                           iconFunction = "getLocaleIcon" />
 *   </listing>
 * 
 * @see http://flexibleexperiments.wordpress.com/2007/04/28/flex-201-combobox-with-icon-support/
 * @author Jason Hawryluk
 * @author Julien Nicoulaud
 */
public class IconComboBox extends ComboBox
{
    
    /**
     * Create our own factory so we can set the properties before initialized
     */
    private var internalDropdownFactory:ClassFactory = new ClassFactory(List);
    
    /**
     * Holds the icon image for display
     */
    private var displayIconObject:Object;
             
    /**
     * Build a new IconComboBox
     */
    public function IconComboBox():void{
        super();
        
        //setup the properties on the factory before init so that
        //the drop down will gracefully adopt them.
        internalDropdownFactory.properties = { iconField:"",iconFunction:null };
        dropdownFactory = internalDropdownFactory;
    }
    
    /**
     * Store the icon field
     */
    private var _iconField:String="icon";
    [Bindable]
    public function set iconField(value:String):void{
        _iconField = value;
        internalDropdownFactory.properties = {iconField:value};

    }
    public function get iconField():String{
        return _iconField;
    }
    
    /**
     * Store the icon function
     */
    private var _iconFunction:Function;
    [Bindable]
    public function set iconFunction(value:Function):void{
        _iconFunction = value;
        internalDropdownFactory.properties = {iconFunction:value};
    }
    public function get iconFunction():Function{
        return _iconFunction;
    }
    
    
    /**
     * When the index changes, so should the icon 
     */
    override public function set selectedIndex(value:int):void
    {
        super.selectedIndex = value;
        
        if (value!=-1){ 
            showIcon();
        }
        
    }
    
    /**
     * Set the icon to the selected item
     */
    private function showIcon():void
    {
        
        var displayIcon:Class = itemToIcon(dataProvider[selectedIndex]);
        
        //remove the previous added object so that a new one can 
        //be created. I would love to find a way to recycle this 
        //displayIconObject??     
        if (getChildByName("displayIconObject"))
        {
            removeChild(getChildByName("displayIconObject"));
        }

        //if no icon then return
        if (!displayIcon)
        {
            //move the textinput to 0 as there is no icon            
            textInput.x=0;
            return;
        }
        
        //add and size the obejct
        displayIconObject = new displayIcon;
        displayIconObject.name="displayIconObject";
        addChild(DisplayObject(displayIconObject));
        
        // set the x based on corerradius
        DisplayObject(displayIconObject).x = getStyle("cornerRadius") + 4;
        
        //set the y pos of the icon based on height
        DisplayObject(displayIconObject).y = (height-DisplayObject(displayIconObject).height)/2;
        
        //move the textinput to make room for the icon            
        textInput.x=DisplayObject(displayIconObject).width+getStyle("cornerRadius") + 4;
        
                    
    }
            
    /**
     * Make sure to take into account the icon width 
     */        
    override public function set measuredWidth(value:Number):void
    {
        super.measuredWidth = value + (DisplayObject(displayIconObject).width+getStyle("cornerRadius")+4);
    }
    
    
    /**
     * Grab the icon based on the data
     */
    public function itemToIcon(data:Object):Class
    {
        if (data == null)
            return null;

        if (iconFunction != null)
            return iconFunction(data);

        var iconClass:Class;
        var icon:*;

        if (data is XML)
        {
            try
            {
                if (data[iconField].length() != 0)
                {
                   icon = String(data[iconField]);
                   if (icon != null)
                   {
                       iconClass =
                            Class(systemManager.getDefinitionByName(icon));
                       if (iconClass)
                           return iconClass;

                       return document[icon];
                   }
                }
            }
            catch(e:Error)
            {
            }
        }

        else if (data is Object)
        {
            try
            {
                if (data[iconField] != null)
                {
                    if (data[iconField] is Class)
                        return data[iconField];

                    if (data[iconField] is String)
                    {
                        iconClass = Class(systemManager.getDefinitionByName(
                                                data[iconField]));
                        if (iconClass)
                            return iconClass;

                        return document[data[iconField]];
                    }
                }
            }
            catch(e:Error)
            {
            }
        }

        return null;
    }
    
    
}
}
