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
package org.ow2.jasmine.kerneos.core.view.window
{

import mx.containers.Box;
import mx.controls.Text;
import mx.controls.TileList;
import mx.core.ClassFactory;
import mx.states.SetStyle;

import org.ow2.jasmine.kerneos.core.view.ListModuleRenderer;
import org.ow2.jasmine.kerneos.core.vo.FolderVO;


/**
 * A window hosting a folder of modules.
 *
 * @author Julien Nicoulaud
 */
[Bindable]
public class FolderWindow extends ModuleWindow
{

    // =========================================================================
    // Variables
    // =========================================================================

    private var descriptionLabel : Text;
    private var descriptionBox : Box;

    /**
     * The modules list displayed
     */
    private var modulesList : TileList;



    // =========================================================================
    // Constructor & initialization
    // =========================================================================

    /**
     * Build a new folder window.
     */
    public function FolderWindow(module:FolderVO)
    {
        // Call super classe constructor
        super(module);
    }

    /**
     * Create UI children.
     */
    override protected function createChildren():void
    {
        // Call super class method
        super.createChildren();

        // Create the description box
        if (descriptionBox == null)
        {
            descriptionBox = new Box();
            addChild(descriptionBox);
        }

        // Create the description label
        if (descriptionLabel == null)
        {
            descriptionLabel = new Text();
            descriptionBox.addChild(descriptionLabel);
        }

        // Create the modules list
        if (modulesList == null)
        {
            modulesList = new TileList();
            addChild(modulesList);
        }
    }

    /**
     * Commit the component properties.
     */
    override protected function commitProperties():void
    {
        // Call super class function
        super.commitProperties();

        // Set some window properties
        setStyle("paddingTop",5);
        setStyle("paddingBottom",5);
        setStyle("paddingLeft",5);
        setStyle("paddingRight",5);
        //setStyle("verticalGap",0);

        // Set the properties for the description box
        descriptionBox.percentWidth = 100;
        descriptionBox.setStyle("styleName","folderWindowDescriptionBox");

        // Set the properties for the description label
        descriptionLabel.text = module.description;
        descriptionLabel.percentWidth = 100;
        descriptionLabel.selectable = false;
        descriptionLabel.setStyle("styleName","folderWindowDescriptionLabel");

        // Set the properties for the modules list
        modulesList.percentHeight = 100;
        modulesList.percentWidth = 100;
        modulesList.verticalScrollPolicy = "none";
        modulesList.horizontalScrollPolicy = "none";
        modulesList.setStyle("styleName","folderWindowModulesList");
        modulesList.selectable = false;
        modulesList.dataProvider = (module as FolderVO).modules.modulesList;
        modulesList.itemRenderer = new ClassFactory(ListModuleRenderer);
    }
}
}
