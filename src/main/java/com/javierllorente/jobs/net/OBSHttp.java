/*
 * Copyright (C) 2020 Javier Llorente <javier@opensuse.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
