/*
 * Copyright (C) 2020 Javier Llorente <javier@opensuse.org>
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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author javier
 */
public class OBSHttp {

    private static final Logger logger = Logger.getLogger(OBSHttp.class.getName());
    private final String acceptHeader;
    private final String contentTypeHeader;
        
    public OBSHttp() {
        acceptHeader = "application/xml";
        contentTypeHeader = "application/x-www-form-urlencoded";
    }

    public InputStream get(URL url) throws IOException {
        HttpsURLConnection connection = httpRequest(url, "GET");
        logger.info(getConnectionInfo(connection));
        InputStream is = getInputStream(connection);
        return is;
    }    

    public InputStream delete(URL url) throws IOException {
        HttpsURLConnection connection = httpRequest(url, "DELETE");
        logger.info(getConnectionInfo(connection));
        InputStream is = getInputStream(connection);
        return is;
    }

    public InputStream post(URL url, byte[] data) throws IOException {
        HttpsURLConnection connection = httpRequest(url, "POST");
        connection.setRequestProperty("Content-Type", contentTypeHeader);
        writeData(connection, data);
        logger.info(getConnectionInfo(connection));
        InputStream is = getInputStream(connection);
        return is;
    }

    public InputStream put(URL url, byte[] data) throws IOException {
        HttpsURLConnection connection = httpRequest(url, "PUT");
        writeData(connection, data);
        logger.info(getConnectionInfo(connection));
        InputStream is = getInputStream(connection);
        return is;
    }
    
    private HttpsURLConnection httpRequest(URL url, String method) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        if (method.equals("POST") || method.equals("PUT")) {
            connection.setDoOutput(true);
        }
        connection.setRequestProperty("User-Agent", UserAgent.FULL);
        connection.setRequestProperty("Accept", acceptHeader);
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);
        return connection;
    }
    
    private void writeData(HttpsURLConnection connection, byte[] data) throws IOException {
        try (final DataOutputStream output
                = new DataOutputStream(connection.getOutputStream())) {
            output.write(data);
        }
    }
    
    private String getConnectionInfo(HttpsURLConnection connection) throws IOException {
        return "URL: " + connection.getURL().toString() + 
                ", method: " + connection.getRequestMethod() + 
                ", response: " + connection.getResponseCode();
    }
    
    private InputStream getInputStream(HttpsURLConnection connection) throws IOException {
        return (connection.getResponseCode() == HttpsURLConnection.HTTP_OK)
                ? connection.getInputStream()
                : connection.getErrorStream();
    }
    
}
