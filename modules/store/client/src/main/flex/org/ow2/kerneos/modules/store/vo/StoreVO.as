/**
 * Created by IntelliJ IDEA.
 * User: riverapj
 * Date: 01/08/11
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
package org.ow2.kerneos.modules.store.vo {

import com.adobe.cairngorm.vo.IValueObject;

[Bindable]
public class StoreVO implements IValueObject {

    public var name : String;
    public var description : String;
    public var url : String;


    public function StoreVO() {
    }
}
}
