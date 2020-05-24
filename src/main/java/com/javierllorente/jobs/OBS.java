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
package com.javierllorente.jobs;

import com.javierllorente.jobs.net.OBSAuth;
import com.javierllorente.jobs.net.OBSHttp;
import com.javierllorente.jobs.util.Utils;
import com.javierllorente.jobs.xml.OBSXmlReader;
import com.javierllorente.jobs.xml.OBSXmlWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.naming.AuthenticationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author javier
 */
public class OBS {

    private final OBSAuth obsAuth;
    private final OBSHttp obsHttp;
    private final OBSXmlReader xmlReader;
    private URL apiUrl;
    
    private enum RequestType {
        Incoming,
        Outgoing,
        Declined
    }

    public OBS() {
        obsAuth = OBSAuth.getInstance();
        obsHttp = OBSHttp.getInstance();
        xmlReader = OBSXmlReader.getInstance();
    }

    public URL getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(URL apiUrl) throws MalformedURLException {
        obsAuth.setApiUrl(apiUrl);
        this.apiUrl = apiUrl;
    }

    public void setUsername(String username) {
        obsAuth.setUsername(username);
    }

    public String getUsername() {
        return obsAuth.getUsername();
    }

    public void setPassword(String password) {
        obsAuth.setPassword(password);
    }

    public String getPassword() {
        return obsAuth.getPassword();
    }

    public int getResponseCode() {
        return obsAuth.getResponseCode();
    }

    public String getResponseMessage() {
        return obsAuth.getResponseMessage();
    }

    public void authenticate() throws IOException, AuthenticationException {
        obsAuth.authenticate();
    }

    public boolean isAuthenticated() {
        return obsAuth.isAuthenticated();
    }

    
    public OBSStatus branchPackage(String prj, String pkg) throws
            MalformedURLException, IOException, ParserConfigurationException,
            SAXException {
        String resource = String.format("/source/%s/%s?cmd=branch", prj, pkg);
        InputStream is = obsHttp.post(new URL(apiUrl + resource), "");
        OBSStatus status = xmlReader.parseBranchPackage(prj, pkg, is);
        is.close();
        return status;
    }

    public OBSStatus createProject(String project, String title, String description) throws
            TransformerException, MalformedURLException, IOException, SAXException, ParserConfigurationException {
        OBSXmlWriter xmlWriter = new OBSXmlWriter();
        String data = xmlWriter.createProjectMeta(project, title, description, getUsername());
        String resource = String.format("/source/%s/_meta", project);
        InputStream is = obsHttp.put(new URL(apiUrl + resource), data);
        OBSStatus status = xmlReader.parseBuildStatus(is);
        is.close();
        return status;
    }

    public OBSStatus createPackage(String project, String pkg, String title, String description) throws
            TransformerException, MalformedURLException, IOException, SAXException, ParserConfigurationException {
        OBSXmlWriter xmlWriter = new OBSXmlWriter();
        String data = xmlWriter.createPackageMeta(project, pkg, title, description, getUsername());
        String resource = String.format("/source/%s/%s/_meta", project, pkg);
        InputStream is = obsHttp.put(new URL(apiUrl + resource), data);
        OBSStatus status = xmlReader.parseBuildStatus(is);
        is.close();
        return status;
    }
    
    public OBSRequest createRequest(OBSRequest newRequest) throws MalformedURLException, 
            IOException, ParserConfigurationException, SAXException, TransformerException {
        OBSXmlWriter xmlWriter = new OBSXmlWriter();
        String data = xmlWriter.createRequest(newRequest);
        String resource = "/request?cmd=create";
        InputStream is = obsHttp.post(new URL(apiUrl + resource), data);
        OBSRequest request = xmlReader.parseCreateRequest(is);
        is.close();
        return request;
    }

    public OBSStatus deleteProject(String project) throws
            MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        String resource = String.format("/source/%s", project);
        InputStream is = obsHttp.delete(new URL(apiUrl + resource));
        OBSStatus status = xmlReader.parseDeleteProject(project, is);
        is.close();
        return status;
    }

    public OBSStatus deletePackage(String project, String pkg) throws
            MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        String resource = String.format("/source/%s/%s", project, pkg);
        InputStream is = obsHttp.delete(new URL(apiUrl + resource));
        OBSStatus status = xmlReader.parseDeletePackage(project, pkg, is);
        is.close();
        return status;
    }

    public OBSStatus deleteFile(String project, String pkg, String file) throws
            MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        String resource = String.format("/source/%s/%s/%s", project, pkg, file);
        InputStream is = obsHttp.delete(new URL(apiUrl + resource));
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
        InputStream is = obsHttp.get(url);
        OBSStatus status = xmlReader.parseBuildStatus(is);
        is.close();
        return status;
    }
    
    public List<OBSResult> getAllBuildStatus(String project, String pkg) throws 
            MalformedURLException, IOException, SAXException, ParserConfigurationException {
//        URL format: https://api.opensuse.org/build/<project>/_result?package=<package>
        String resource = String.format("/build/%s/_result?package=%s", project, pkg);
        URL url = new URL(apiUrl + resource);
        List<OBSResult> list;
        try (InputStream is = obsHttp.get(url)) {
            list = xmlReader.parseResultList(is);
        }
        return list;
    }

    private String createReqResourceStr(String states, String roles) {
        return String.format("/request/?view=collection&states=%s&roles=%s&user=%s",
                states, roles, getUsername());
    }
    
    private List<OBSRequest> getRequests(RequestType type) throws IOException,
            MalformedURLException, SAXException, ParserConfigurationException {
        String resource = null;

        switch (type) {
            case Incoming:
                resource = createReqResourceStr("new", "maintainer");
                break;
            case Outgoing:
                resource = createReqResourceStr("new", "creator");
                break;
            case Declined:
                resource = createReqResourceStr("declined", "creator");
                break;
            default:
                System.out.println("RequestType not handled!");
                break;
        }
        
        if (resource==null) {
            throw new MalformedURLException("Resource is empty!");
        }
        
        System.out.println("Getting requests...");
        URL url = new URL(apiUrl + resource);
        InputStream is = obsHttp.get(url);
        List<OBSRequest> requests = xmlReader.parseRequests(is);
        is.close();
        return requests;
    }
    
    public List<OBSRequest> getIncomingRequests() throws IOException,
            MalformedURLException, SAXException, ParserConfigurationException {
        return getRequests(RequestType.Incoming);
    }

    public List<OBSRequest> getOutgoingRequests() throws IOException,
            MalformedURLException, SAXException, ParserConfigurationException {
        return getRequests(RequestType.Outgoing);
    }

    public List<OBSRequest> getDeclinedRequests() throws IOException,
            MalformedURLException, SAXException, ParserConfigurationException {
        return getRequests(RequestType.Declined);
    }

    public int getRequestCount() {
        return xmlReader.getRequestCount();
    }

    public OBSStatus changeRequestState(String id, String comments, boolean accepted) throws IOException,
            SAXException, ParserConfigurationException {
        String newState = accepted ? "accepted" : "declined";
        String resource = String.format("/request/%s?cmd=changestate&newstate=%s", id, newState);
        URL url = new URL(apiUrl + resource);
        InputStream is = obsHttp.post(url, comments);
        OBSStatus status = xmlReader.parseChangeRequestState(is);
        is.close();
        return status;
    }

    public String getRequestDiff(String source) throws IOException {
        URL url = new URL(apiUrl + "/source/" + source
                + "?unified=1&tarlimit=0&cmd=diff&filelimit=0&expand=1");
        InputStream is = obsHttp.post(url, "");
        String str = Utils.inputStreamToString(is);
        is.close();
        return str;
    }

    public List<String> getProjectList() throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + "/source");
        InputStream is = obsHttp.get(url);
        List<String> list = xmlReader.parseList(is);
        is.close();
        return list;
    }

    public List<String> getPackageList(String projectName) throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + "/source/" + projectName);
        InputStream is = obsHttp.get(url);
        List<String> list = xmlReader.parseList(is);
        is.close();
        return list;
    }
    
    public List<OBSFile> getFileList(String project, String pkg) throws IOException, 
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + "/source/" + project + "/" + pkg);
        InputStream is = obsHttp.get(url);
        List<OBSFile> list = xmlReader.parseFileList(is);
        is.close();
        return list;
    }
   
    public OBSPrjMetaConfig getProjectMetaConfig(String prj) throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + String.format("source/%s/_meta", prj));
        InputStream is = obsHttp.get(url);
        OBSPrjMetaConfig prjMetaConfig = xmlReader.parsePrjMetaConfig(is);
        is.close();
        return prjMetaConfig;
    }
    
    public OBSPkgMetaConfig getPackageMetaConfig(String prj, String pkg) 
            throws IOException, ParserConfigurationException, SAXException {
        URL url = new URL(apiUrl + String.format("source/%s/%s/_meta", prj, pkg));
        InputStream is = obsHttp.get(url);
        OBSPkgMetaConfig pkgMetaConfig = xmlReader.parsePkgMetaConfig(is);
        is.close();
        return pkgMetaConfig;
    }
}
