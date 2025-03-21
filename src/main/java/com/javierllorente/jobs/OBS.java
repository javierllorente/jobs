/*
 * Copyright (C) 2015-2025 Javier Llorente <javier@opensuse.org>
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
package com.javierllorente.jobs;

import com.javierllorente.jobs.entity.OBSAbout;
import com.javierllorente.jobs.entity.OBSCollection;
import com.javierllorente.jobs.entity.OBSDirectory;
import com.javierllorente.jobs.entity.OBSDistributions;
import com.javierllorente.jobs.entity.OBSLink;
import com.javierllorente.jobs.entity.OBSPackage;
import com.javierllorente.jobs.entity.OBSPerson;
import com.javierllorente.jobs.entity.OBSProject;
import com.javierllorente.jobs.entity.OBSRequest;
import com.javierllorente.jobs.entity.OBSResultList;
import com.javierllorente.jobs.entity.OBSRevision;
import com.javierllorente.jobs.entity.OBSRevisionList;
import com.javierllorente.jobs.entity.OBSStatus;
import com.javierllorente.jobs.net.OBSHttp;
import java.io.File;
import java.net.URI;
import java.util.logging.Logger;

/**
 *
 * @author javier
 */
public class OBS {
    private static final Logger logger = Logger.getLogger(OBS.class.getName());
    private final OBSHttp obsHttp;

    public OBS() {
        obsHttp = new OBSHttp();
    }
    
    public OBS(URI apiUri) {
        this();
        obsHttp.setApiUri(apiUri);
    }

    public URI getApiUri() {
        return obsHttp.getApiUri();
    }

    public void setApiUri(URI apiUri)  {
        obsHttp.setApiUri(apiUri);
    }

    public void setUsername(String username) {
        obsHttp.setUsername(username);
    }

    public String getUsername() {
        return obsHttp.getUsername();
    }

    public void setPassword(String password) {
        obsHttp.setPassword(password);
    }
    
    public void authenticate() {
        obsHttp.authenticate();
    }

    public boolean isAuthenticated() {
        return obsHttp.isAuthenticated();
    }
    
    public void logout() {
        obsHttp.setAuthenticated(false);
    }
    
    public OBSStatus branchPackage(String prj, String pkg) {
        return obsHttp.branchPackage(prj, pkg);
    }
    
    public OBSRevision linkPackage(String sourceProject, String sourcePackage, 
            String targetProject) {
        return obsHttp.linkPackage(sourceProject, sourcePackage, targetProject);
    }
    
    public OBSRevision copyPackage(String sourceProject, String sourcePackage,
            String targetProject, String targetPackage, String comments) {
        return obsHttp.copyPackage(sourceProject, sourcePackage, 
                targetProject, targetPackage, comments);
    }
    
    public OBSStatus createProject(OBSProject prj) {
        return obsHttp.createProject(prj);
    }
    
    public OBSStatus createPackage(OBSPackage pkg) {
        return obsHttp.createPackage(pkg);
    }
    
    public OBSRequest createRequest(OBSRequest request) {
        return obsHttp.createRequest(request);
    }
    
    public OBSRevision uploadFile(String prj, String pkg, File file) {
        return obsHttp.uploadFile(prj, pkg, file);
    }

    public File downloadFile(String prj, String pkg, String fileName) {
        return obsHttp.downloadFile(prj, pkg, fileName);
    }
    
    public String getBuildLog(String prj, String repository, String arch, String pkg) {
        return obsHttp.getBuildLog(prj, repository, arch, pkg);
    }

    public OBSStatus deleteProject(String project) {
        String resource = String.format("/source/%s", project);
        return obsHttp.delete(resource);
    }

    public OBSStatus deletePackage(String project, String pkg) {
        String resource = String.format("/source/%s/%s", project, pkg);
        return obsHttp.delete(resource);
    }

    public OBSStatus deleteFile(String project, String pkg, String file) {
        String resource = String.format("/source/%s/%s/%s", project, pkg, file);
        return obsHttp.delete(resource);
    }

    public OBSStatus getBuildStatus(String project, String repository,
            String architecture, String build) {
        return obsHttp.getBuildStatus(project, repository, architecture, build);
    }
    
    public OBSResultList getPackageResults(String prj, String pkg) {
        return obsHttp.getPackageResults(prj, pkg);
    }
    
    public OBSResultList getProjectResults(String project) {
        return obsHttp.getProjectResults(project);
    }
    
    public OBSRevisionList getRevisions(String prj, String pkg) {
        return obsHttp.getRevisions(prj, pkg);
    }
    
    public OBSRevisionList getLatestRevision(String prj, String pkg) {
        return obsHttp.getLatestRevision(prj, pkg);
    }
    
    public OBSCollection getIncomingRequests() {
        return obsHttp.getIncomingRequests();
    }

    public OBSCollection getOutgoingRequests() {
        return obsHttp.getOutgoingRequests();
    }

    public OBSCollection getDeclinedRequests() {
        return obsHttp.getDeclinedRequests();
    }
    
    public OBSCollection getProjectRequests(String project) {
        OBSCollection collection = obsHttp.getProjectRequests(project);
        return collection;
    }
    
    public OBSCollection getPackageRequests(String prj, String pkg) {
        OBSCollection collection = obsHttp.getPackageRequests(prj, pkg);
        return collection;
    }

    public OBSStatus changeRequestState(String id, String comments, boolean accepted) {
        String newState = accepted ? "accepted" : "declined";
        OBSStatus status = obsHttp.changeRequest(id, comments, newState);
        return status;
    }

    public String getRequestDiff(String id) {
        String str = obsHttp.getRequestDiff(id);
        return str;
    }    
    
    public OBSDirectory getProjects() {
        OBSDirectory directory = obsHttp.getProjects();
        return directory;
    }
    
    public OBSDirectory getPackages(String project) {
        OBSDirectory directory = obsHttp.getPackages(project);
        return directory;
    }
    
    public OBSDirectory getFiles(String prj, String pkg) {
        OBSDirectory directory = obsHttp.getFiles(prj, pkg);
        return directory;
    }
   
    public OBSProject getProjectMetaConfig(String prj) {
        OBSProject project = obsHttp.getProjectMetaConfig(prj);
        return project;
    }
    
    public OBSPackage getPackageMetaConfig(String prj, String pkg) {
        OBSPackage obsPackage = obsHttp.getPackageMetaConfig(prj, pkg);
        return obsPackage;
    }
    
    public OBSCollection packageSearch(String pkg) {
        OBSCollection collection = obsHttp.packageSearch(pkg);
        return collection;
    }
    
    public OBSDistributions getDistributions() {
        return obsHttp.getDistributions();
    }
    
    public OBSLink getLink(String prj, String pkg) {
        return obsHttp.getLink(prj, pkg);
    }

    public OBSPerson getPerson() {
        return obsHttp.getPerson();
    }
    
    public OBSStatus updatePerson(OBSPerson person) {
        OBSStatus status = obsHttp.updatePerson(person);
        return status;
    }
    
    public OBSAbout getAbout() {
        return obsHttp.getAbout();
    }
    
}
