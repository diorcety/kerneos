/**
 * JASMINe
 * Copyright (C) 2011 Bull S.A.S.
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
 * $Id: FolderVO.as 5737 2009-12-13 17:04:13Z nicoulaj $
 */

package org.ow2.jasmine.kerneos.core.view {
import flash.display.DisplayObject;
import flash.text.TextLineMetrics;

import mx.controls.LinkButton;
import mx.core.IFlexDisplayObject;
import mx.core.mx_internal;
use namespace mx_internal;

/**
 * @see http://blogs.adobe.com/aharui/2007/04/multiline_buttons.html
 */

public class MultiLineLinkButton extends LinkButton {
    public function MultiLineLinkButton() {
        super();
    }

    override protected function createChildren() : void {
        if (!textField) {
            textField = new NoTruncationUITextField();
            textField.styleName = this;
            addChild(DisplayObject(textField));
        }
        super.createChildren();
        textField.wordWrap = true;
        textField.multiline = true;
    }

    override protected function measure() : void {
        if (!isNaN(explicitWidth)) {
            var tempIcon : IFlexDisplayObject = getCurrentIcon();
            var w : Number = explicitWidth;
            if (tempIcon)
                w -= tempIcon.width + getStyle("horizontalGap") + getStyle("paddingLeft") + getStyle("paddingRight");
            textField.width = w;
        }
        super.measure();

    }

    override public function measureText(s : String) : TextLineMetrics {
        textField.text = s;
        var lineMetrics : TextLineMetrics = textField.getLineMetrics(0);
        lineMetrics.width = textField.textWidth + 4;
        lineMetrics.height = textField.textHeight + 4;
        return lineMetrics;
    }
}

}