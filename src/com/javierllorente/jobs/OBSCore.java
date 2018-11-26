/*
 * Copyright (C) 2015-2018 Javier Llorente <javier@opensuse.org>
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
package com.javierllorente.jobs;

import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author javier
 */
class OBSCore {
    private URL apiUrl;
    private String username;
    private String password;
    private int responseCode;
    private String responseMessage;
    private boolean authenticated;    

    private OBSCore() {
        authenticated = false;
    }
    
    public static OBSCore getInstance() {
        return OBSCoreHolder.INSTANCE;
    }
    
    private static class OBSCoreHolder {

        private static final OBSCore INSTANCE = new OBSCore();
    }

    URL getApiUrl() {
        return apiUrl;
    }

    void setApiUrl(URL apiUrl) throws MalformedURLException {
        this.apiUrl = apiUrl;
    }

    void setUsername(String username) {
        this.username = username;
    }

    String getUsername() {
        return username;
    }

    void setPassword(String password) {
        this.password = password;
    }

    String getPassword() {
        return password;
    }
    
    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    boolean isAuthenticated() {
        return authenticated;
    }

    class OBSAuthenticator extends Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            System.out.println("Authenticator called for " + getRequestingScheme() + " type");
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }

    void authenticate() throws ProtocolException, IOException {
        System.out.println("Authenticating...");
        System.out.println("apiUrl: " + apiUrl);
        System.out.println("User-Agent: " + UserAgent.FULL);
        
        HttpsURLConnection connection;
        System.setProperty("http.maxRedirects", "4");
        Authenticator.setDefault(new OBSAuthenticator());
        connection = (HttpsURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        System.out.println("RequestMethod: " + connection.getRequestMethod());
        connection.addRequestProperty("User-Agent", UserAgent.FULL);
        connection.connect();
        
        responseCode = connection.getResponseCode();
        responseMessage = connection.getResponseMessage();        
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            authenticated = true;
        }
    }
}
