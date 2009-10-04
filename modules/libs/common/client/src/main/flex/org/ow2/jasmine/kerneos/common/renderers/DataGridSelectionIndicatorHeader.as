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
package org.ow2.jasmine.kerneos.common.renderers
{

import flash.events.Event;

import mx.controls.CheckBox;
import mx.controls.listClasses.BaseListData;

import org.ow2.jasmine.kerneos.common.controls.CheckBoxSelectionDataGrid;


/**
 * A Checkbox column header renderer to set all items of a
 * DataGrid selected/unselected
 *
 * @author Julien Nicoulaud
 * @see org.ow2.jasmine.kerneos.common.controls.CheckBoxSelectionDataGrid
 */
public class DataGridSelectionIndicatorHeader extends CheckBox
{
    
    // =====================================================================
    // Private variables
    // =====================================================================
    
    /**
     * The parent DataGrid
     */
    private var grid : CheckBoxSelectionDataGrid;
    
    
    
    // =====================================================================
    // Constructors
    // =====================================================================
    
    /**
     * Build a new DataGridSelectionIndicatorHeader
     */
    public function DataGridSelectionIndicatorHeader()
    {
        super();
        addEventListener(Event.CHANGE, onChange);
        setStyle("paddingLeft", 3);
        toolTip = "Select all";
    }
    
    
    
    // =====================================================================
    // Events handling
    // =====================================================================
    
    /**
     * Called on data change
     *
     * @private
     */
    private function onChange(event : Event) : void
    {
        grid.selectAll(selected);
    }
    
    
    
    // =====================================================================
    // Overriden getters & setters
    // =====================================================================
    
    /**
     * Set the list data
     *
     * @override
     */
    override public function set listData(value : BaseListData) : void
    {
        //super.listData = value
        grid = CheckBoxSelectionDataGrid(value.owner)
    }
    
    
    
    /**
     * Set the renderer data
     *
     * @override
     */
    override public function set data(value : Object) : void
    {
        // prevent default behavior
    }

}
}
