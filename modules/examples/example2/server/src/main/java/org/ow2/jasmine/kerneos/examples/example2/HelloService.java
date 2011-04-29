package org.ow2.jasmine.kerneos.examples.example2;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.jasmine.kerneos.service.KerneosService;
import org.ow2.jasmine.kerneos.service.KerneosSimpleService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@Component
@Instantiate
@Provides
@KerneosService(destination = "HelloService")
public class HelloService implements KerneosSimpleService {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(HelloService.class);


    @Validate
    private void start() {
        logger.info("Start HelloService");
    }

    @Invalidate
    private void stop() {
        logger.info("Stop HelloService");
    }

    public String sayHello(String name) {
        return "Hello " + name;
    }
}
