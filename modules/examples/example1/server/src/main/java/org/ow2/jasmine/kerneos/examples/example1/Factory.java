package org.ow2.jasmine.kerneos.examples.example1;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.jasmine.kerneos.service.KerneosFactory;
import org.ow2.jasmine.kerneos.service.KerneosFactoryProperties;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;


@Component
@Instantiate
@Provides
@KerneosFactoryProperties(scope = KerneosFactory.SCOPE.REQUEST)
public class Factory implements KerneosFactory<String> {
    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(Factory.class);

    @Validate
    private void start() {
        logger.info("Start Factory");
    }

    @Invalidate
    private void stop() {
        logger.info("Stop Factory");
    }

    public String getId() {
        return "Factory";
    }

    public String newInstance() {
        return "Hi!";
    }

    public Class<String> getInstanceClass() {
        return String.class;
    }
}
