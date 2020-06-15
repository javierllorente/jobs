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
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author javier
 */
public class OBSHttp {

    public OBSHttp() {
    }

    public InputStream get(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        setupConnection(connection);
        System.out.println("Response code: " + connection.getResponseCode());
        System.out.println("Request method: " + connection.getRequestMethod());
        InputStream is = (InputStream) connection.getInputStream();
        return is;
    }

    public InputStream delete(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setDoOutput(true);
        setupConnection(connection);
        System.out.println("Response code: " + connection.getResponseCode());
        System.out.println("Request method: " + connection.getRequestMethod());
        InputStream is = (InputStream) connection.getInputStream();
        return is;
    }

    public InputStream post(URL url, String data) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        setupConnection(connection);
        connection.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
        try (final DataOutputStream output = new DataOutputStream(connection.getOutputStream())) {
            output.writeBytes(data);
            output.close();
        }
        System.out.println("Response code: " + connection.getResponseCode());
        System.out.println("Request method: " + connection.getRequestMethod());
        InputStream is = null;
        is = connection.getResponseCode() == 200 ? (InputStream) connection.getInputStream() : (InputStream) connection.getErrorStream();
        return is;
    }

    public InputStream put(URL url, byte[] data) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        setupConnection(connection);
        try (final DataOutputStream output = new DataOutputStream(connection.getOutputStream())) {
            output.write(data);
            output.close();
        }
        System.out.println("Response code: " + connection.getResponseCode());
        System.out.println("Request method: " + connection.getRequestMethod());
        InputStream is = (InputStream) connection.getInputStream();
        return is;
    }
    
    private void setupConnection(HttpsURLConnection connection) {
        connection.setRequestProperty("User-Agent", UserAgent.FULL);
        connection.setRequestProperty("Accept", "application/xml");
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);
    }
    
}
