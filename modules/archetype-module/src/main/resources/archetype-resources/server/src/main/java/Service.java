//package ${groupId};

import org.granite.messaging.service.annotations.RemoteDestination;

/**
 * Hello world!
 *
 */
@RemoteDestination(id="myJavaService")
public class Service 
{
    
    public Service() {
        
    }
    
    public void sayHello(String name)
    {
        System.out.println( "Hello " + name );
    }
}
