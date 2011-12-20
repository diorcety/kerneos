
package archetype - resources.client.src.main.flex.model
{

import business.IModuleDelegate;

import business.ModuleDelegate;

import com.adobe.cairngorm.model.ModelLocator;

import mx.utils.UIDUtil;

 /**
  * The model locator for the Module.
  */
[Bindable]
public class ModuleModelLocator implements ModelLocator
{

    ////////////////////////////////////
    //                                //
    //             Variables          //
    //                                //
    ////////////////////////////////////

  /**
    * The unique ID of this component
    *
    * @internal
    *   Used to prevent a Cairngorm issue: when a command event is dispatched,
    * every controller that registered this event type receives it, even if
    * located in another module. To prevent this from happening and triggering
    * multiple severe unexpected concurrence bugs, each event dispatched is
    * postfixed with this unique ID.
    */
    public var componentID:String = UIDUtil.createUID();


    /**
    * Unique instance of this locator.
    */
    private static var moduleModel : ModuleModelLocator = null;
    
    ////////////////////////////////////////////
    //                                        //
    //             Variables Delegate         //
    //                                        //
    ////////////////////////////////////////////
    /*
    // Put here the delegate instances of your model.
    // Example :
        private var myDelegate : IModuleDelegate = null;
    */

    ////////////////////////////////////////////////
    //                                            //
    //             Variables Of the model         //
    //                                            //
    ////////////////////////////////////////////////
    
    /*
    // Put here all the variables of the model
    // Example :
        private var _myDataObj : String = null;
    */
    
    ////////////////////////////////////
    //                                //
    //             Functions          //
    //                                //
    ////////////////////////////////////

    public function ModuleModelLocator()
    {
        super();
        
        if (moduleModel != null)
        {
            throw new Error("Only one ModelLocator has to be set");
        }
    }


     /**
      * Get the only created instance of the ModuleModelLocator
      */
    public static function getInstance() : ModuleModelLocator
    {
        if (ModuleModelLocator.moduleModel == null)
        {
            ModuleModelLocator.moduleModel = new ModuleModelLocator();
        }
        
        return ModuleModelLocator.moduleModel;
    }

    ////////////////////////////////////
    //                                //
    //             Setters            //
    //                                //
    ////////////////////////////////////

    /*
    // Put here all the setters for the model update.
    // Example :
        public function set myDataObj(_myData : String) : void {
            this._myDataObj = _myData;
        }
    */

    ////////////////////////////////////
    //                                //
    //             Getters            //
    //                                //
    ////////////////////////////////////
    
    /*
    // Put here all the getters to access the model variables
    // Example :
        public function get myDataObj() : String {
            return this._myDataObj;
        }
    */


    ////////////////////////////////////////////
    //                                        //
    //            Delegate Getters            //
    //                                        //
    ////////////////////////////////////////////

    /*
    // Put here the getters to access all the delegates of the created module
    // Example :
        public function getMyDelegate() : IModuleDelegate {
            if (this.myDelegate == null) {
                this.myDelegate = new ModuleDelegate();
            }
            return this.myDelegate;
        }
    
    */

}
}
