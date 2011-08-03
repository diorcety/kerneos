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

public class ChangeStoreStateEvent extends CairngormEvent {

    public static var CHANGE_MODULE_STATE : String = "CHANGE_MODULE_STATE";

    private var _state : String = "";

    public function ChangeStoreStateEvent(type : String)
    {
        super(type);
    }

    public function set state(state:String):void
    {
        this._state = state;
    }

    public function get state(): String
    {
        return _state;
    }

    /**
     * Overrides the clone function of the CairngormEvent class.
     * returns a new ModuleEvent
     */
    override public function clone() : Event
    {
        var ev:ChangeStoreStateEvent = new ChangeStoreStateEvent(this.type);

         ev.state = this._state;

        return ev;
    }
}
}
