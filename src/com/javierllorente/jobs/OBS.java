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

    private InputStream deleteRequest(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setDoOutput(true);
        connection.addRequestProperty("User-Agent", UserAgent.FULL);
        System.out.println("Response code: " + connection.getResponseCode());
        System.out.println("Request method: " + connection.getRequestMethod());
        InputStream is = (InputStream) connection.getInputStream();
        return is;
    }

    public OBSStatus deleteProject(String project) throws
            MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        String resource = String.format("/source/%s", project);
        InputStream is = deleteRequest(new URL(apiUrl + resource));
        OBSStatus status = xmlReader.parseDeleteProject(project, is);
        is.close();
        return status;
    }

    public OBSStatus deletePackage(String project, String pkg) throws
            MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        String resource = String.format("/source/%s/%s", project, pkg);
        InputStream is = deleteRequest(new URL(apiUrl + resource));
        OBSStatus status = xmlReader.parseDeletePackage(project, pkg, is);
        is.close();
        return status;
    }

    public OBSStatus deleteFile(String project, String pkg, String file) throws
            MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        String resource = String.format("/source/%s/%s/%s", project, pkg, file);
        InputStream is = deleteRequest(new URL(apiUrl + resource));
        OBSStatus status = xmlReader.parseDeleteFile(project, pkg, file, is);
        is.close();
        return status;
    }

    public OBSStatus getBuildStatus(String project, String repository,
            String architecture, String build) throws
            SAXException, IOException, ParserConfigurationException {
        System.out.println("Getting build status...");
//	URL format https://api.opensuse.org/build/KDE:Extra/openSUSE_13.2/x86_64/ktorrent/_status
        String resource = String.format("/build/%s/%s/%s/%s/_status", 
                project, repository, architecture, build);
        URL url = new URL(apiUrl + resource);
        InputStream is = getRequest(url);
        OBSStatus status = xmlReader.parseBuildStatus(is);
        is.close();
        return status;
    }

    public ArrayList<OBSRequest> getRequests() throws IOException,
            MalformedURLException, SAXException, ParserConfigurationException {
        System.out.println("Getting requests...");
        String resource = String.format(
                "/request?view=collection&states=new&roles=maintainer&user=%s",
                getUsername());
        URL url = new URL(apiUrl + resource);
        InputStream is = getRequest(url);
        ArrayList<OBSRequest> requests = xmlReader.parseRequests(is);
        is.close();
        return requests;
    }

    public int getRequestNumber() {
        return xmlReader.getRequestNumber();
    }

    public OBSStatus changeRequestState(String id, String comments, boolean accepted) throws IOException,
            SAXException, ParserConfigurationException {
        String newState = accepted ? "accepted" : "declined";
        String resource = String.format("/request/%s?cmd=changestate&newstate=%s", id, newState);
        URL url = new URL(apiUrl + resource);
        InputStream is = postRequest(url, comments);
        OBSStatus status = xmlReader.parseChangeRequestState(is);
        is.close();
        return status;
    }

    public String getRequestDiff(String source) throws IOException {
        URL url = new URL(apiUrl + "/source/" + source
                + "?unified=1&tarlimit=0&cmd=diff&filelimit=0&expand=1");
        InputStream is = postRequest(url, "");
        String str = inputStreamToString(is);
        is.close();
        return str;
    }

    public ArrayList<String> getProjectList() throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + "/source");
        InputStream is = getRequest(url);
        ArrayList<String> list = xmlReader.parseList(is);
        is.close();
        return list;
    }

    public ArrayList<String> getPackageList(String projectName) throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + "/source/" + projectName);
        InputStream is = getRequest(url);
        ArrayList<String> list = xmlReader.parseList(is);
        is.close();
        return list;
    }

    public ArrayList<String> getProjectMetadata(String projectName) throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + "/source/" + projectName + "/_meta");
        InputStream is = getRequest(url);
        ArrayList<String> list = xmlReader.parseList(is);
        is.close();
        return list;
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
