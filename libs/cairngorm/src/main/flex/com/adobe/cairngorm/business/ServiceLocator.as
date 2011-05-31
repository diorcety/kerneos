/*

 Copyright (c) 2006. Adobe Systems Incorporated.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.
 * Neither the name of Adobe Systems Incorporated nor the names of its
 contributors may be used to endorse or promote products derived from this
 software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 @ignore

 modified 2009-04-09 14:53 : Jean-Pierre
 modified 2011-05-05 14:49 : riverapj
 */

package com.adobe.cairngorm.business {
import com.adobe.cairngorm.CairngormError;
import com.adobe.cairngorm.CairngormMessageCodes;

import com.adobe.cairngorm.Consumer;

import com.adobe.cairngorm.Producer;

import mx.rpc.AbstractInvoker;
import mx.rpc.AbstractService;
import mx.rpc.http.HTTPService;
import mx.rpc.remoting.RemoteObject;
import mx.rpc.soap.WebService;

/**
 * The ServiceLocator allows service to be located and security
 * credentials to be managed.
 *
 * Although credentials are set against a service they apply to the channel
 * i.e. the set of services belonging to the channel share the same
 * credentials.
 *
 * You must always make sure you call logout at the end of the user's
 * session.
 */
public class ServiceLocator implements IServiceLocator {
    private static var _instance:ServiceLocator;

    private var _httpServices:HTTPServices;
    private var _remoteObjects:RemoteObjects;
    private var _producers:Producers;
    private var _consumers:Consumers;
    private var _webServices:WebServices;
    private var _timeout:int = 0;

    /**
     * Return the ServiceLocator instance.
     * @return the instance.
     */
    public static function get instance():ServiceLocator {
        if (! _instance) {
            _instance = new ServiceLocator();
        }

        return _instance;
    }

    /**
     * Return the ServiceLocator instance.
     * @return the instance.
     */
    public static function getInstance():ServiceLocator {
        return instance;
    }

    // Constructor should be private but current AS3.0 does not allow it
    public function ServiceLocator() {
        if (_instance) {
            throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "ServiceLocator");
        }

        _instance = this;
    }

    /**
     * <p><strong>Deprecated as of Cairngorm 2.1</strong></p>
     *
     * Returns the service defined for the id, to allow services to be looked up
     * using the ServiceLocator by a canonical name.
     *
     * <p>If no service exists for the service name, an Error will be thrown.</p>
     * @param The id of the service to be returned. This is the id defined in the
     * concrete service locator implementation.
     */
    [Deprecated("You should now use one of the strongly typed methods for returning a service.")]
    public function getService(serviceId:String):AbstractService {
        return AbstractService(remoteObjects.getService(serviceId));
    }

    /**
     * <p><strong>Deprecated as of Cairngorm 2.1</strong></p>
     *
     * Returns an AbstractInvoker defined for the id, to allow services to be looked up
     * using the ServiceLocator by a canonical name.
     *
     * <p>If no service exists for the service name, an Error will be thrown.</p>
     * @param The id of the service to be returned. This is the id defined in the
     * concrete service locator implementation.
     */
    [Deprecated("You should now use one of the strongly typed methods for returning a service.")]
    public function getInvokerService(serviceId:String):AbstractInvoker {
        return AbstractInvoker(remoteObjects.getService(serviceId));
    }

    /**
     * Return the HTTPService for the given name.
     * @param name the name of the HTTPService
     * @return the HTTPService.
     */
    public function getHTTPService(name:String):HTTPService {
        return HTTPService(httpServices.getService(name));
    }

    /**
     * Return the RemoteObject for the given name.
     * @param name the name of the RemoteObject.
     * @return the RemoteObject.
     */
    public function getRemoteObject(name:String):RemoteObject {
        return RemoteObject(remoteObjects.getService(name));
    }

    /**
     * Return the Consumer for the given name.
     * @param name the name of the Consumer.
     * @return the Consumer.
     */
    public function getConsumer(name:String):Consumer {
        return Consumer(consumers.getService(name));
    }

    /**
     * Return the Producer for the given name.
     * @param name the name of the Producer.
     * @return the Producer.
     */
    public function getProducer(name:String):Producer {
        return Producer(producers.getService(name));
    }

    /**
     * Return the WebService for the given name.
     * @param name the name of the WebService.
     * @return the WebService.
     */
    public function getWebService(name:String):WebService {
        return WebService(webServices.getService(name));
    }

    /**
     * Set the credentials for all registered services.
     * @param username the username to set.
     * @param password the password to set.
     */
    public function setCredentials(username:String, password:String):void {
        httpServices.setCredentials(username, password);
        remoteObjects.setCredentials(username, password);
        webServices.setCredentials(username, password);
    }

    /**
     * Set the remote credentials for all registered services.
     * @param username the username to set.
     * @param password the password to set.
     */
    public function setRemoteCredentials(username:String, password:String):void {
        httpServices.setRemoteCredentials(username, password);
        remoteObjects.setRemoteCredentials(username, password);
        webServices.setRemoteCredentials(username, password);
    }

    /**
     * Logs the user out of all registered services.
     */
    public function logout():void {
        // First release the resources held by the service. We release the
        // resources first as the logout logs the user out at a channel level.
        httpServices.release();
        remoteObjects.release();
        webServices.release();

        // Now log the services out.
        httpServices.logout();
        remoteObjects.logout();
        webServices.logout();
    }

    public function set timeout(timeoutTime:int):void {
        _timeout = timeoutTime;
    }

    public function get timeout():int {
        return _timeout;
    }

    protected function get httpServices():HTTPServices {
        if (_httpServices == null) {
            _httpServices = new HTTPServices();
            _httpServices.timeout = timeout;
            _httpServices.register(this);
        }

        return _httpServices;
    }

    protected function get remoteObjects():RemoteObjects {
        if (_remoteObjects == null) {
            _remoteObjects = new RemoteObjects();
            _remoteObjects.timeout = timeout;
            _remoteObjects.register(this);
        }

        return _remoteObjects;
    }

    protected function get producers():Producers {
        if (_producers == null) {
            _producers = new Producers();
            _producers.timeout = timeout;
            _producers.register(this);
        }

        return _producers;
    }

    protected function get consumers():Consumers {
        if (_consumers == null) {
            _consumers = new Consumers();
            _consumers.timeout = timeout;
            _consumers.register(this);
        }

        return _consumers;
    }

    protected function get webServices():WebServices {
        if (_webServices == null) {
            _webServices = new WebServices();
            _webServices.timeout = timeout;
            _webServices.register(this);
        }

        return _webServices;
    }

    /* Dynamic service setting */

    /**
     * Set a new service name.
     * @param serviceId the service name.
     */
    public function setServiceForId(serviceId:String, destination:String, asynchronous:Boolean = false):void {
        if (asynchronous) {
            var producer:Producer = new Producer();

            if (_producers == null) {
                _producers = new Producers();
                _producers.timeout = timeout;
            }
            producer.destination = destination;
            _producers.registerProducer(serviceId, producer);
            producer.requestTimeout = timeout;

            var consumer:Consumer = new Consumer();

            if (_consumers == null) {
                _consumers = new Consumers();
                _consumers.timeout = timeout;
            }
            consumer.destination = destination;
            _consumers.registerConsumer(serviceId, consumer);
            consumer.requestTimeout = timeout;
        }
        else {
            var remote:RemoteObject = new RemoteObject();

            if (_remoteObjects == null) {
                _remoteObjects = new RemoteObjects();
                _remoteObjects.timeout = timeout;
            }
            remote.destination = destination;
            _remoteObjects.registerRemoteObject(serviceId, remote);
            remote.requestTimeout = timeout;
        }
    }

    /**
     * Delete a service name.
     * @param serviceId the service name.
     */
    public function removeServiceForId(serviceId:String, asynchronous:Boolean = false):void {
        if (asynchronous) {
            if (_producers != null) {
                _producers.unRegisterProducer(serviceId);
            }
            if (_consumers != null) {
                _consumers.unRegisterConsumer(serviceId);
            }
        }
        else {
            if (_remoteObjects != null) {
                _remoteObjects.unRegisterRemoteObject(serviceId);
            }
        }
    }

}
}