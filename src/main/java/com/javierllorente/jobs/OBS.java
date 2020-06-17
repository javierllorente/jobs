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

import com.javierllorente.jobs.entity.OBSAbout;
import com.javierllorente.jobs.entity.OBSDistribution;
import com.javierllorente.jobs.entity.OBSFile;
import com.javierllorente.jobs.entity.OBSLink;
import com.javierllorente.jobs.entity.OBSPerson;
import com.javierllorente.jobs.entity.OBSPkgMetaConfig;
import com.javierllorente.jobs.entity.OBSPrjMetaConfig;
import com.javierllorente.jobs.entity.OBSRequest;
import com.javierllorente.jobs.entity.OBSResult;
import com.javierllorente.jobs.entity.OBSRevision;
import com.javierllorente.jobs.entity.OBSStatus;
import com.javierllorente.jobs.net.OBSAuth;
import com.javierllorente.jobs.net.OBSHttp;
import com.javierllorente.jobs.util.Utils;
import com.javierllorente.jobs.xml.OBSXmlReader;
import com.javierllorente.jobs.xml.OBSXmlWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
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
    
    private enum RequestType {
        Incoming,
        Outgoing,
        Declined
    }

    public OBS() {
        obsAuth = OBSAuth.getInstance();
        obsHttp = new OBSHttp();
        xmlReader = new OBSXmlReader();
    }
    
    public OBS(URL apiUrl) throws MalformedURLException {
        this();
        obsAuth.setApiUrl(apiUrl);
    }

    public URL getApiUrl() {
        return obsAuth.getApiUrl();
    }

    public void setApiUrl(URL apiUrl) throws MalformedURLException {
        obsAuth.setApiUrl(apiUrl);
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
        OBSStatus status;
        try (InputStream is = obsHttp.post(new URL(obsAuth.getApiUrl() + resource),
                "".getBytes())) {
            status = xmlReader.parseBranchPackage(prj, pkg, is);
        }
        return status;
    }
    
    public OBSRevision linkPackage(String srcProject, String srcPackage, 
            String dstProject) throws IOException, ParserConfigurationException, 
            SAXException, TransformerException {
        OBSRevision revision = null;
        OBSPkgMetaConfig pkgMetaConfig = getPackageMetaConfig(srcProject, srcPackage);
        pkgMetaConfig.setProject(dstProject);        
        String dstPackage = srcPackage;
        
        OBSStatus status = createPackage(pkgMetaConfig);
        if (status.getCode().equals("ok")) {
            OBSXmlWriter xmlWriter = new OBSXmlWriter();
            String data = xmlWriter.createLink(srcProject, dstPackage);
            String resource = String.format("/source/%s/%s/_link", 
                    dstProject, dstPackage);
            try (InputStream is = obsHttp.put(new URL(obsAuth.getApiUrl() + resource), 
                    data.getBytes())) {
                revision = xmlReader.parseRevision(is);
            }
        }
        
        return revision;
    }
    
    public OBSRevision copyPackage(String srcProject, String srcPackage,
            String dstProject, String dstPackage, String comments) throws
            IOException, ParserConfigurationException, SAXException,
            TransformerException {
        String resource
                = String.format("/source/%s/%s?cmd=copy&oproject=%s&opackage=%s&comment=%s",
                        dstProject, dstPackage, srcProject, srcPackage,
                        URLEncoder.encode(comments, "UTF-8"));
        OBSRevision revision;
        try (InputStream is = obsHttp.post(new URL(obsAuth.getApiUrl() + resource),
                "".getBytes())) {
            revision = xmlReader.parseRevision(is);
            revision.setProject(dstProject);
            revision.setPkg(dstPackage);
        }

        return revision;
    }

    public OBSStatus createProject(OBSPrjMetaConfig prjMetaConfig) throws
            TransformerException, MalformedURLException, IOException, SAXException, 
            ParserConfigurationException {
        OBSXmlWriter xmlWriter = new OBSXmlWriter();
        String data = xmlWriter.createProjectMeta(prjMetaConfig);
        String resource = String.format("/source/%s/_meta", prjMetaConfig.getName());
        OBSStatus status;
        try (InputStream is = obsHttp.put(new URL(obsAuth.getApiUrl() + resource), 
                data.getBytes())) {
            status = xmlReader.parseBuildStatus(is);
        }
        return status;
    }

    public OBSStatus createPackage(OBSPkgMetaConfig pkgMetaConfig) throws
            TransformerException, MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        OBSXmlWriter xmlWriter = new OBSXmlWriter();
        String data = xmlWriter.createPackageMeta(pkgMetaConfig);
        String resource = String.format("/source/%s/%s/_meta", 
                pkgMetaConfig.getProject(), pkgMetaConfig.getName());
        OBSStatus status;
        try (InputStream is = obsHttp.put(new URL(obsAuth.getApiUrl() + resource),
                data.getBytes())) {
            status = xmlReader.parseBuildStatus(is);
        }
        return status;
    }
    
    public OBSRequest createRequest(OBSRequest newRequest) throws 
            MalformedURLException, IOException, ParserConfigurationException, 
            SAXException, TransformerException {
        OBSXmlWriter xmlWriter = new OBSXmlWriter();
        String requestXml = xmlWriter.createRequest(newRequest);
        String resource = "/request?cmd=create";
        OBSRequest request;
        try (InputStream is = obsHttp.post(new URL(obsAuth.getApiUrl() + resource), 
                requestXml.getBytes())) {
            request = xmlReader.parseCreateRequest(is);
        }
        return request;
    }
    
    public OBSRevision uploadFile(String prj, String pkg, File file)
            throws MalformedURLException, IOException, ParserConfigurationException,
            SAXException {
        String resource = String.format("/source/%s/%s/%s", prj, pkg, file.getName());
        InputStream is = obsHttp.put(new URL(obsAuth.getApiUrl() + resource), 
                Files.readAllBytes(file.toPath()));
        OBSRevision revision = xmlReader.parseRevision(is);
        return revision;
    }
    
    public byte[] downloadFile(String prj, String pkg, String fileName) throws 
            IOException, ParserConfigurationException, SAXException {
        URL url = new URL(obsAuth.getApiUrl() + String.format("source/%s/%s/%s", 
                prj, pkg, fileName));
        byte[] data;
        try (InputStream is = obsHttp.get(url)) {
            data = is.readAllBytes();
        }
        return data;
    }
    
    public String getBuildLog(String prj, String repository, String arch, String pkg) 
            throws IOException, ParserConfigurationException, SAXException {
        String resource = String.format("/build/%s/%s/%s/%s/_log",
                prj, repository, arch, pkg);
        URL url = new URL(obsAuth.getApiUrl() + resource);
        String buildLog;
        try (InputStream is = obsHttp.get(url)) {
            buildLog = Utils.inputStreamToString(is);
        }
        return buildLog;
    }

    public OBSStatus deleteProject(String project) throws
            MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        String resource = String.format("/source/%s", project);
        OBSStatus status;
        try (InputStream is = obsHttp.delete(new URL(obsAuth.getApiUrl()
                + resource))) {
            status = xmlReader.parseDeleteProject(project, is);
        }
        return status;
    }

    public OBSStatus deletePackage(String project, String pkg) throws
            MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        String resource = String.format("/source/%s/%s", project, pkg);
        OBSStatus status;
        try (InputStream is = obsHttp.delete(new URL(obsAuth.getApiUrl()
                + resource))) {
            status = xmlReader.parseDeletePackage(project, pkg, is);
        }
        return status;
    }

    public OBSStatus deleteFile(String project, String pkg, String file) throws
            MalformedURLException, IOException, SAXException,
            ParserConfigurationException {
        String resource = String.format("/source/%s/%s/%s", project, pkg, file);
        OBSStatus status;
        try (InputStream is = obsHttp.delete(new URL(obsAuth.getApiUrl()
                + resource))) {
            status = xmlReader.parseDeleteFile(project, pkg, file, is);
        }
        return status;
    }

    public OBSStatus getBuildStatus(String project, String repository,
            String architecture, String build) throws
            SAXException, IOException, ParserConfigurationException {
        String resource = String.format("/build/%s/%s/%s/%s/_status",
                project, repository, architecture, build);
        URL url = new URL(obsAuth.getApiUrl() + resource);
        OBSStatus status;
        try (InputStream is = obsHttp.get(url)) {
            status = xmlReader.parseBuildStatus(is);
        }
        return status;
    }
    
    public List<OBSResult> getAllBuildStatus(String project, String pkg) throws 
            MalformedURLException, IOException, SAXException, 
            ParserConfigurationException {
        String resource = String.format("/build/%s/_result?package=%s", project, pkg);
        URL url = new URL(obsAuth.getApiUrl() + resource);
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
        
        URL url = new URL(obsAuth.getApiUrl() + resource);
        List<OBSRequest> requests;
        try (InputStream is = obsHttp.get(url)) {
            requests = xmlReader.parseRequests(is);
        }
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

    public OBSStatus changeRequestState(String id, String comments, boolean accepted) 
            throws IOException, SAXException, ParserConfigurationException {
        String newState = accepted ? "accepted" : "declined";
        String resource = String.format("/request/%s?cmd=changestate&newstate=%s", 
                id, newState);
        URL url = new URL(obsAuth.getApiUrl() + resource);
        OBSStatus status;
        try (InputStream is = obsHttp.post(url, comments.getBytes())) {
            status = xmlReader.parseChangeRequestState(is);
        }
        return status;
    }

    public String getRequestDiff(String source) throws IOException {
        URL url = new URL(obsAuth.getApiUrl() + "/source/" + source
                + "?unified=1&tarlimit=0&cmd=diff&filelimit=0&expand=1");
        String str;
        try (InputStream is = obsHttp.post(url, "".getBytes())) {
            str = Utils.inputStreamToString(is);
        }
        return str;
    }

    public List<String> getProjectList(boolean includeHomePrjs) throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(obsAuth.getApiUrl() + "/source");
        List<String> list;
        try (InputStream is = obsHttp.get(url)) {
            String userHome = includeHomePrjs ? "" : "home:" + obsAuth.getUsername();
            list = xmlReader.parseProjectList(userHome, is);
        }
        return list;
    }

    public List<String> getPackageList(String projectName) throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(obsAuth.getApiUrl() + "/source/" + projectName);
        List<String> list;
        try (InputStream is = obsHttp.get(url)) {
            list = xmlReader.parseList(is);
        }
        return list;
    }
    
    public List<OBSFile> getFileList(String project, String pkg) throws IOException, 
            ParserConfigurationException, SAXException {
        URL url = new URL(obsAuth.getApiUrl() + "/source/" + project + "/" + pkg);
        List<OBSFile> list;
        try (InputStream is = obsHttp.get(url)) {
            list = xmlReader.parseFileList(is);
        }
        return list;
    }
   
    public OBSPrjMetaConfig getProjectMetaConfig(String prj) throws IOException,
            ParserConfigurationException, SAXException {
        URL url = new URL(obsAuth.getApiUrl() + String.format("source/%s/_meta", prj));
        OBSPrjMetaConfig prjMetaConfig;
        try (InputStream is = obsHttp.get(url)) {
            prjMetaConfig = xmlReader.parsePrjMetaConfig(is);
        }
        return prjMetaConfig;
    }
    
    public OBSPkgMetaConfig getPackageMetaConfig(String prj, String pkg) 
            throws IOException, ParserConfigurationException, SAXException {
        URL url = new URL(obsAuth.getApiUrl() + String.format("source/%s/%s/_meta", 
                prj, pkg));
        OBSPkgMetaConfig pkgMetaConfig;
        try (InputStream is = obsHttp.get(url)) {
            pkgMetaConfig = xmlReader.parsePkgMetaConfig(is);
        }
        return pkgMetaConfig;
    }
    
    public List<OBSDistribution> getDistributions() 
            throws IOException, ParserConfigurationException, SAXException {
        List<OBSDistribution> distributions;
        try (InputStream is = obsHttp.get(new URL(obsAuth.getApiUrl() 
                + "/distributions"))) {
            distributions = xmlReader.parseDistributions(is);
        }
        return distributions;
    }
    
    public OBSLink getLink(String prj, String pkg) throws MalformedURLException, 
            IOException, ParserConfigurationException, SAXException {
        String resource = String.format("source/%s/%s/_link", prj, pkg);
        URL url = new URL(obsAuth.getApiUrl() + resource);
        OBSLink link;
        try (InputStream is = obsHttp.get(url)) {
            link = xmlReader.parseLink(is);
        }
        return link;
    }
    
    public OBSPerson getPerson() throws MalformedURLException, IOException, 
            ParserConfigurationException, SAXException {
        String resource = String.format("/person/%s", obsAuth.getUsername());
        URL url = new URL(obsAuth.getApiUrl() + resource);
        OBSPerson person;
        try (InputStream is = obsHttp.get(url)) {
            person = xmlReader.parsePerson(is);
        }
        return person;
    }
    
    public OBSStatus updatePerson(OBSPerson person) throws 
            ParserConfigurationException, TransformerException, 
            MalformedURLException, IOException, SAXException {
        OBSXmlWriter xmlWriter = new OBSXmlWriter();
        String data = xmlWriter.createPerson(person);
        String resource = String.format("/person/%s", obsAuth.getUsername());
        OBSStatus status;
        try (InputStream is = obsHttp.put(new URL(obsAuth.getApiUrl() + resource), 
                data.getBytes())) {
            status = xmlReader.parseBuildStatus(is);
        }
        return status;
    }
    
    public OBSAbout getAbout() throws MalformedURLException, IOException, 
            ParserConfigurationException, SAXException {
        URL url = new URL(obsAuth.getApiUrl() + "/about");
        OBSAbout about;
        try (InputStream is = obsHttp.get(url)) {
            about = xmlReader.parseAbout(is);
        }
        return about;
    }
    
}
