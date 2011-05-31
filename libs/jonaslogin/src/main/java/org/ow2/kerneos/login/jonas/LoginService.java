package org.ow2.kerneos.login.jonas;


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import org.ow2.jonas.security.auth.callback.NoInputCallbackHandler;
import org.ow2.kerneos.service.KerneosLogin;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Instantiate
@Provides
public class LoginService implements KerneosLogin {

    /**
     * The logger.
     */
    private static Log logger = LogFactory.getLog(LoginService.class);

    /**
     * CallbackHandler.
     */
    private CallbackHandler handler = null;

    public boolean login(String user, String password) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Login on JOnAS 5");
        this.handler = new NoInputCallbackHandler(user, password);
        try {
            // Obtain a LoginContext
            LoginContext lc = new LoginContext("eos", this.handler);

            lc.login();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean logout() {
        return true;
    }
}
