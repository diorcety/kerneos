package org.ow2.kerneos.login;


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import org.ow2.kerneos.service.KerneosLogin;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;

@Component
@Instantiate
@Provides
public class LoginService implements KerneosLogin {

    /**
     * CallbackHandler.
     */
    private CallbackHandler handler = null;

    public Collection<String> login(String user, String password) {
        this.handler = new NoInputCallbackHandler(user, password);
        try {
            // Obtain a LoginContext
            LoginContext lc = new LoginContext("eos", this.handler);

            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            lc.login();

            Collection<String> roles = new LinkedList<String>();
            for (Principal principal : lc.getSubject().getPrincipals()) {
                roles.add(principal.getName());
            }
            return roles;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean logout() {
        return true;
    }
}
