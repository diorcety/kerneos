/**
 * Created by IntelliJ IDEA.
 * User: riverapj
 * Date: 01/08/11
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
package org.ow2.kerneos.modules.store.event {
import flash.events.Event;
import com.adobe.cairngorm.control.CairngormEvent;

public class GetModuleEvent extends CairngormEvent {

    public static var GET_MODULE : String = "GET_MODULE";

    public static var GET_MODULE_IMAGE : String = "GET_MODULE_IMAGE";

    private var _id : String = "";

    public function GetModuleEvent(type : String)
    {
        super(type);
    }

    public function set id(id:String):void
    {
        this._id = id;
    }

    public function get id(): String
    {
        return _id;
    }

    /**
     * Overrides the clone function of the CairngormEvent class.
     * returns a new ModuleEvent
     */
    override public function clone() : Event
    {
        var ev:GetModuleEvent = new GetModuleEvent(this.type);

         ev.id = this.id;

        return ev;
    }
}
}
