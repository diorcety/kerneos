/**
 * JASMINe
 * Copyright (C) 2008 Bull S.A.S.
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
import mx.controls.DataGrid;
import mx.controls.dataGridClasses.*;
import mx.controls.listClasses.BaseListData;


/**
 * A DataGrid item renderer that allows complex objects.
 *
 * @example
 * <listing version="3.0">
 *     <mx:DataGridColumn dataField="object.subObject"
 *                        itemRenderer="HierarchicDataGridItemRenderer"/>
 * </listing>
 * @author Julien Nicoulaud
 */
public class HierarchicalDataGridItemRenderer extends DataGridItemRenderer
{
    /**
     * The list data
     */
    private var _listData2 : BaseListData;



    /**
     * Build a new HierarchicalDataGridItemRenderer.
     */
    public function HierarchicalDataGridItemRenderer()
    {
    }



    /**
     * Set the itemRenderer data
     */
    override public function set data(value : Object) : void
    {
        // Forward value to super class
        super.data = value;

        // Retrieve the parent Datagrid
        var dg : DataGrid = DataGrid(_listData2.owner);

        // Retrieve the column
        var column : DataGridColumn = dg.columns[_listData2.columnIndex];

        // Split the path in fields
        var fields : Array = column.dataField.split(".");

        // Follow the path to the real value
        var buffer : * = value;

        for each (var p : String in fields)
        {
            buffer = buffer[p];
        }

        // Set the real value
        _listData2.label = buffer;

        // Set the list data
        super.listData = _listData2;
    }



    /**
     * Intercept the list data
     */
    override public function set listData(value : BaseListData) : void
    {
        _listData2 = value;
    }
}
}
