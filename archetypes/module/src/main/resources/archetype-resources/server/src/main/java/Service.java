#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package};

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.kerneos.core.service.api.KerneosService;
import org.ow2.kerneos.core.service.api.KerneosSimpleService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * HelloService
 */
//ipojo annotations Component, Instantiate and Provides
@Component
@Instantiate
@Provides
/*
 * Annotation that defines a KerneosService with the
 * "HelloService" destination.
 * It is the same destination defined in kerneos-module.xml
 */
@KerneosService(id = "hello_service")
public class Service implements KerneosSimpleService{

     /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(Service.class);

    /**
     * Start
     */
    @Validate
    private void start() {
        logger.info("Start HelloService");
    }

    /**
     * Stop
     */
    @Invalidate
    private void stop() {
        logger.info("Stop HelloService");
    }

    /**
     * @param name the name of the user
     * @return a welcome message
     */
    public String sayHello(final String name) {
        return "Hello " + name;
    }
}
