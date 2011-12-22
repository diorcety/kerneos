#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model
{

import ${package}.business.IModuleDelegate;

import ${package}.business.ModuleDelegate;

import com.adobe.cairngorm.model.ModelLocator;

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
    
    ////////////////////////////////////////////
    //                                        //
    //             Variables Delegate         //
    //                                        //
    ////////////////////////////////////////////

    // Put here the delegate instances of your model.
    // Example :
    private var myDelegate : IModuleDelegate = null;


    ////////////////////////////////////////////////
    //                                            //
    //             Variables Of the model         //
    //                                            //
    ////////////////////////////////////////////////
    

    // Put here all the variables of the model
    // Example :
    private var _myDataObj : String = null;

    
    ////////////////////////////////////
    //                                //
    //             Functions          //
    //                                //
    ////////////////////////////////////

    public function ModuleModelLocator()
    {
        super();
    }

    ////////////////////////////////////
    //                                //
    //             Setters            //
    //                                //
    ////////////////////////////////////


    // Put here all the setters for the model update.
    // Example :
    public function set myDataObj(_myData : String) : void {
        this._myDataObj = _myData;
    }


    ////////////////////////////////////
    //                                //
    //             Getters            //
    //                                //
    ////////////////////////////////////
    

    // Put here all the getters to access the model variables
    // Example :
    public function get myDataObj() : String {
        return this._myDataObj;
    }



    ////////////////////////////////////////////
    //                                        //
    //            Delegate Getters            //
    //                                        //
    ////////////////////////////////////////////


    // Put here the getters to access all the delegates of the created module
    // Example :
    public function getMyDelegate() : IModuleDelegate {
        if (this.myDelegate == null) {
            this.myDelegate = new ModuleDelegate();
        }
        return this.myDelegate;
    }

}
}
