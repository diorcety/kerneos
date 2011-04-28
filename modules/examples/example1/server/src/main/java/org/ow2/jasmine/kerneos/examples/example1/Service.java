package org.ow2.jasmine.kerneos.examples.example1;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.jasmine.kerneos.service.KerneosService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

@Component
@Instantiate
@Provides
public class Service implements KerneosService {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(Service.class);


    @Validate
    private void start() {
        logger.info("Start Service");
    }

    @Invalidate
    private void stop() {
        logger.info("Stop Service");
    }

    public void sayHello(String name) {
        System.out.println("Hello " + name);
    }

    public String getId() {
        return "Service";
    }
}
