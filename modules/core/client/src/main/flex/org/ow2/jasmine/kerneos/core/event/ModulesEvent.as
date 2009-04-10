package org.ow2.jasmine.kerneos.core.event
{
import com.adobe.cairngorm.control.CairngormEvent;

import flash.events.Event;

/**
 * ModuleEvent class. This is the link between the view layer and the command layer
 */
public class ModulesEvent extends CairngormEvent {

    /**
    * type of the event.
    */
    public static var GET_MODULE:String = "Get modules";

    /**
     * Create a new instance, and call the super.
     *
     * @param type String to put in the event.
     */
    public function ModulesEvent(type:String) {
        super( type );
    }

    /**
     * Override the inherited clone() method, but don't return any state.
     *
     * @return this event
     */
    override public function clone() : Event {
        return new ModulesEvent(this.type);
    }
}
}