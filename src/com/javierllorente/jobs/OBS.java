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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author javier
 */
public class OBS {
    private final OBSCore obsCore;
    private final OBSXmlReader xmlReader;
    private URL apiUrl;

    public OBS() {
        obsCore = OBSCore.getInstance();
        xmlReader = OBSXmlReader.getInstance();
    }
    
    public URL getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(URL apiUrl) throws MalformedURLException {
        obsCore.setApiUrl(apiUrl);
        this.apiUrl = apiUrl;
    }

    public void setUsername(String username) {
        obsCore.setUsername(username);
    }

    public String getUsername() {
        return obsCore.getUsername();
    }

    public void setPassword(String password) {
        obsCore.setPassword(password);
    }

    public String getPassword() {
        return obsCore.getPassword();
    }
    
    public int getResponseCode() {
        return obsCore.getResponseCode();
    }

    public String getResponseMessage() {
        return obsCore.getResponseMessage();
    }

    public void authenticate() throws IOException {
        obsCore.authenticate();
    }

    public boolean isAuthenticated() {
        return obsCore.isAuthenticated();
    }

    private InputStream getRequest(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("User-Agent", UserAgent.FULL);
        connection.setRequestProperty("Accept", "xml/application");
        System.out.println("Response code: " + connection.getResponseCode());
        System.out.println("Request method: " + connection.getRequestMethod());
        InputStream is = (InputStream) connection.getInputStream();
        return is;
    }

    private InputStream postRequest(URL url, String data) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.addRequestProperty("User-Agent", UserAgent.FULL);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try (DataOutputStream output = new DataOutputStream(connection.getOutputStream())) {
            output.writeBytes(data);
        }
        System.out.println("Response code: " + connection.getResponseCode());
        System.out.println("Request method: " + connection.getRequestMethod());
        InputStream is = (InputStream) connection.getInputStream();
        return is;
    }

    public OBSBuild getBuild(String project, String repository, 
            String architecture, String buildName) throws 
            SAXException, IOException, ParserConfigurationException {
        System.out.println("Getting build status...");
        OBSBuild build = new OBSBuild();
        build.setProject(project);
        build.setRepository(repository);
        build.setArchitecture(architecture);
        build.setName(buildName);
//	URL format https://api.opensuse.org/build/KDE:Extra/openSUSE_13.2/x86_64/ktorrent/_status
        URL url = new URL(
                apiUrl + "/build/" +
                project + "/" +
                repository+ "/" +
                architecture + "/" +
                buildName + "/" +
                "_status");
        InputStream data = getRequest(url);
        OBSResult result = xmlReader.parseBuild(data);
        build.setStatus(result.getStatus());
        build.setDetails(result.getDetails());
        return build;
    }

    public ArrayList<OBSRequest> getRequests() throws IOException,
            MalformedURLException, SAXException, ParserConfigurationException {
        System.out.println("Getting requests...");
        URL url = new URL(apiUrl + 
                "/request?view=collection&states=new&roles=maintainer&user=" + getUsername());
        InputStream data = getRequest(url);
        return xmlReader.parseRequests(data);
    }

    public int getRequestNumber() {
        return xmlReader.getRequestNumber();
    }

    public String acceptRequest(String id, String comments) throws IOException,
            SAXException, ParserConfigurationException {
        URL url = new URL(apiUrl + "/request/" + id + "?cmd=changestate&newstate=accepted");
        InputStream data = postRequest(url, comments);
        OBSResult result = xmlReader.parseBuild(data);
        return result.getStatus();
    }

    public String declineRequest(String id, String comments) throws IOException,
            SAXException, ParserConfigurationException {
        URL url = new URL(apiUrl + "/request/" + id + "?cmd=changestate&newstate=declined");
        InputStream data = postRequest(url, comments);
        OBSResult result = xmlReader.parseBuild(data);
        return result.getStatus();
    }

    public String getRequestDiff(String source) throws IOException {
        URL url = new URL(apiUrl + "/source/" + source
                + "?unified=1&tarlimit=0&cmd=diff&filelimit=0&expand=1");
        InputStream data = postRequest(url, "");
        return inputStreamToString(data);
    }

    public ArrayList<String> getProjectList() throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + "/source");
        InputStream data = getRequest(url);
        return xmlReader.parseList(data);
    }

    public ArrayList<String> getPackageList(String projectName) throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + "/source/" + projectName);
        InputStream data = getRequest(url);
        return xmlReader.parseList(data);
    }

    public ArrayList<String> getProjectMetadata(String projectName) throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + "/source/" + projectName + "/_meta");
        InputStream data = getRequest(url);
        return xmlReader.parseList(data);
    }

    private String inputStreamToString(InputStream is) {
        String str;
        try (Scanner scanner = new Scanner(is)) {
            scanner.useDelimiter("\\A");
            str = scanner.hasNext() ? scanner.next() : "";
        }
        return str;
    }
}
