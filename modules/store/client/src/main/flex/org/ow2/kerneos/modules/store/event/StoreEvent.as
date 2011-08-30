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

public class StoreEvent extends CairngormEvent {

    public static var GET_STORE : String = "GET_STORE";


    private var _url : String = "";

    public function StoreEvent(type : String)
    {
        super(type);
    }

    public function set url(url:String):void
    {
        this._url = url;
    }

    public function get url(): String
    {
        return _url;
    }

    /**
     * Overrides the clone function of the CairngormEvent class.
     * returns a new ModuleEvent
     */
    override public function clone() : Event
    {
        var ev:StoreEvent = new StoreEvent(this.type);

         ev.url = this._url;

        return ev;
    }
}
}
