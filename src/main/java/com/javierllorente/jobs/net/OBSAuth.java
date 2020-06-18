/*
 * Copyright (C) 2015-2020 Javier Llorente <javier@opensuse.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.javierllorente.jobs.net;

import com.javierllorente.jobs.config.UserAgent;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.AuthenticationException;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author javier
 */
public class OBSAuth {
    private static final Logger logger = Logger.getLogger(OBSAuth.class.getName());
    private URL apiUrl;
    private String username;
    private String password;
    private int responseCode;
    private String responseMessage;
    private boolean authenticated;    

    private OBSAuth() {
        authenticated = false;                
        System.setProperty("http.maxRedirects", "3");
        Authenticator.setDefault(new OBSAuthenticator());
    }
    
    public static OBSAuth getInstance() {
        return OBSAuthHolder.INSTANCE;
    }
    
    private static class OBSAuthHolder {

        private static final OBSAuth INSTANCE = new OBSAuth();
    }

    public URL getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(URL apiUrl) throws MalformedURLException {
        this.apiUrl = apiUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    
    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    class OBSAuthenticator extends Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            logger.log(Level.INFO, "Authenticator called for {0} type", getRequestingScheme());
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }

    public void authenticate() throws ProtocolException, IOException, AuthenticationException {        
        if (username.isEmpty() || password.isEmpty()) {
            throw new AuthenticationException("Empty username/password");
        }
        
        HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", UserAgent.FULL);
        connection.connect();
        
        responseCode = connection.getResponseCode();
        responseMessage = connection.getResponseMessage();        
        authenticated = (responseCode == HttpsURLConnection.HTTP_OK);
        
        logger.log(Level.INFO, "URL: {0}, method: " + connection.getRequestMethod()
                + ", User-Agent: " + UserAgent.FULL
                + ", response: " + connection.getResponseCode()
                + ", authenticated: " + authenticated, apiUrl);
    }
}
