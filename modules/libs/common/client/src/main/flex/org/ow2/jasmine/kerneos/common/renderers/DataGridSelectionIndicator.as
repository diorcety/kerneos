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
package org.ow2.jasmine.kerneos.common.renderers
{

import flash.events.Event;

import mx.controls.CheckBox;
import mx.controls.DataGrid;
import mx.controls.listClasses.BaseListData;
import mx.events.FlexEvent;
import mx.events.ListEvent;


/**
 * A Checkbox renderer to set an item of a DataGrid selected/unselected.
 *
 * @author Julien Nicoulaud
 * @see org.ow2.jasmine.kerneos.common.controls.CheckBoxSelectionDataGrid
 */
public class DataGridSelectionIndicator extends CheckBox
{

    // =====================================================================
    // Constructors
    // =====================================================================

    /**
     * Build a new DataGridSelectionIndicator
     */
    public function DataGridSelectionIndicator()
    {
        super();
        addEventListener(Event.CHANGE, onChange);
        setStyle("paddingLeft", 3);
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
        var grid : DataGrid = DataGrid(listData.owner);
        var myIndex : int = grid.itemRendererToIndex(this);

        if (selected)
        {
            if (grid.selectedIndices.indexOf(myIndex) >= 0)
                return;
            var indices : Array = grid.selectedIndices;
            indices.push(myIndex);
            grid.selectedIndices = indices;
                //onItemClick()
                //grid.dispatchEvent(new ListEvent(ListEvent.ITEM_CLICK))
        }
        else
        {
            grid.selectedIndices = grid.selectedIndices.filter(function(... args) : Boolean
            {
                if (args[0] == myIndex)
                    return false;
                return true;
            })
        }
    }



    /**
     * Called on item click
     *
     * @private
     */
    private function onItemClick(event : Event) : void
    {
        var grid : DataGrid = DataGrid(listData.owner)
        var myIndex : int = grid.itemRendererToIndex(this)
        selected = grid.selectedIndices.indexOf(myIndex) >= 0
    }



    // =====================================================================
    // Getters & setters
    // =====================================================================

    /**
     * Set the list data
     *
     * @override
     */
    override public function set listData(value : BaseListData) : void
    {
        super.listData = value;

        var grid : DataGrid = DataGrid(value.owner);

        grid.addEventListener(FlexEvent.VALUE_COMMIT, onItemClick, false, 0, true);
        grid.addEventListener(ListEvent.ITEM_CLICK, onItemClick, false, 0, true);
        //grid.addEventListener(ListEvent.CHANGE, onItemClick)
        selected = false;
    }



    /**
     * Set the row data
     *
     * @override
     */
    override public function set data(value : Object) : void
    {
        // prevent default behavior
    }
}
}
