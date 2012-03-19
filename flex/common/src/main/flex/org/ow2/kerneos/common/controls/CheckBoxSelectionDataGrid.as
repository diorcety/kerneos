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
package org.ow2.kerneos.common.controls
{
import flash.events.Event;

import mx.collections.ArrayCollection;
import mx.controls.DataGrid;
import mx.controls.dataGridClasses.DataGridColumn;
import mx.core.ClassFactory;
import mx.events.ListEvent;

import org.ow2.kerneos.common.renderers.DataGridSelectionIndicator;
import org.ow2.kerneos.common.renderers.DataGridSelectionIndicatorHeader;


/**
* A DataGrid that provides multiple selection by checkbox.
*
* @author Julien Nicoulaud
* @example Example of use:
*   <listing version="3.0">
*       <CheckBoxSelectionDataGrid allowMultipleSelection="true"
*                                   dataProvider="{myDataProvider}"
*                                   indicatorAlign="left"/>
*   </listing>
* @see org.ow2.kerneos.common.renderers.DataGridSelectionIndicator
* @see org.ow2.kerneos.common.renderers.DataGridSelectionIndicatorHeader
*/
public class CheckBoxSelectionDataGrid extends DataGrid
{

    // =========================================================================
    // Private variables
    // =========================================================================

    /**
    * The raw Datagrid columns
    *
    * @private
    */
    private var rawColumns:Array;

    /**
    * The alignment of the checkbox indicators column
    *
    * @default right
    * @private
    */
    private var _indicatorAlign:String = "right";

    /**
    * The checkbox indicators column
    *
    * @private
    */
    private var _indicatorsColumn:DataGridColumn;


    // =========================================================================
    // Constructors
    // =========================================================================

    /**
    * Build a new CheckBoxSelectionDataGrid object
    */
    public function CheckBoxSelectionDataGrid()
    {
        super();
    }


    // =========================================================================
    // Public methods
    // =========================================================================

    /**
    * Select/Unselect all the Datagrid rows
    */
    public function selectAll(value:Boolean=true):void
    {
         selectedItems = value ? ArrayCollection(collection).source : [];
    }


    // =========================================================================
    // Overriden getters & setters
    // =========================================================================

    /**
    * Set the Datagrid columns
    *
    * @override
    */
    override public function set columns(value:Array):void
    {
        super.columns = value;
        rawColumns = value;
    }

    /**
    * Set the indicator align property
    */
    public function set indicatorAlign(value:String):void
    {
        if (value != _indicatorAlign) {
            _indicatorAlign = value;
             invalidateProperties();
             dispatchEvent (new Event("indicatorAlignChange"));
        }
    }

    /**
    * Get the indicator align property
    */
    [Bindable(event="indicatorAlignChange")]
    [Inspectable(category="General", enumeration="left,right", defaultValue="right")]
    public function get indicatorAlign():String
    {
        return _indicatorAlign;
    }

    /**
    * Get the checkbox selection indicators column
    */
    public function get indicatorsColumn():DataGridColumn
    {
        if (!_indicatorsColumn){
            var selectionIndicators:DataGridColumn =
                new DataGridColumn();
            selectionIndicators.itemRenderer =
                new ClassFactory(DataGridSelectionIndicator);
            selectionIndicators.headerRenderer =
                new ClassFactory(DataGridSelectionIndicatorHeader);
            selectionIndicators.width = 20;
            selectionIndicators.sortable = false;
            selectionIndicators.resizable = false;
            selectionIndicators.editable = false;
            _indicatorsColumn = selectionIndicators;
        }
        return _indicatorsColumn;
    }

    /**
    * Set the selected items
    *
    * @override
    */
    override public function set selectedItems(items:Array):void
    {
        super.selectedItems = items;
        dispatchEvent(new ListEvent(ListEvent.CHANGE));
    }

    /**
    * Set the selected rows indices
    *
    * @override
    */
    override public function set selectedIndices(indices:Array):void {
        super.selectedIndices = indices;
        dispatchEvent(new ListEvent(ListEvent.CHANGE));
    }

    /**
    * Set the dataProvider
    *
    * @override
    */
    override public function set dataProvider(value:Object):void {
        super.dataProvider = value;
        dispatchEvent(new ListEvent(ListEvent.CHANGE));
    }


    // =========================================================================
    // Overrides
    // =========================================================================

    /**
    * Commit the component properties
    *
    * @override
    */
    override protected function commitProperties():void
    {
        var newColumns:Array = rawColumns.concat();
        if(indicatorAlign == "right") {
            newColumns.push(indicatorsColumn);
        } else {
            newColumns.unshift(indicatorsColumn);
        }
        super.columns = newColumns;
        super.commitProperties();

        // Force the column width to prevent a bug when
        // using a fluid width datagrid
        indicatorsColumn.width = 20;
    }
}
}
