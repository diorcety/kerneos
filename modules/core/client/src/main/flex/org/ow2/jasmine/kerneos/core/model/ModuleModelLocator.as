package org.ow2.jasmine.kerneos.core.model{
	import com.adobe.cairngorm.model.ModelLocator;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	
	import org.ow2.jasmine.kerneos.core.business.IModuleDelegate;
	import org.ow2.jasmine.kerneos.core.business.ModuleDelegate;
	
	public class ModuleModelLocator implements ModelLocator{
		/**
	    * Unique instance of this locator.
	    */
	    private static var model:ModuleModelLocator;

	    /**
	    * Unique instance of delegate.
	    */
	    private var moduleDelegate:IModuleDelegate = null;
	    
	    // private
	    private var _listModules:ArrayCollection;
	    
	    // constructor
	    public function ModuleModelLocator(){
	        super();
	        if (model != null){
	            throw new Error("Only one ModuleModelLocator has to be set");
	        }
	    }
	    
	    public static function getInstance() : ModuleModelLocator{
	        if (ModuleModelLocator.model == null) {
	            ModuleModelLocator.model = new ModuleModelLocator();
	        }
	        return ModuleModelLocator.model;
    	}	
	    
	    // setter and getter of modules	    
	    public function get listModules():ArrayCollection {
	        	return _listModules;
	    }	    
	    
	    public function set listModules(modules:ArrayCollection):void {
	    		_listModules = modules;
   		}	        
	    
	    // getter delegates
	    public function getModuleDelegate():IModuleDelegate {
	        if (this.moduleDelegate == null) {
	            this.moduleDelegate = new ModuleDelegate();
	        }
	        return this.moduleDelegate;
	    }
	}
}