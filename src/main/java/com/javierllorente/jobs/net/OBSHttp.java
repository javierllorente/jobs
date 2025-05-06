/*
 * Copyright (C) 2020-2025 Javier Llorente <javier@opensuse.org>
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
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.apache5.connector.Apache5ConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import static org.glassfish.jersey.client.authentication.HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD;
import static org.glassfish.jersey.client.authentication.HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME;
import org.glassfish.jersey.logging.LoggingFeature;

/**
 *
 * @author javier
 */
public class OBSHttp {

    private static final Logger logger = Logger.getLogger(OBSHttp.class.getName());
    private final Client client;
    private WebTarget target;
    private String username;
    private String password;
    private boolean authenticated;

    private enum RequestType {
        Incoming,
        Outgoing,
        Declined
    }

    public OBSHttp() {
        authenticated = false;
        ClientConfig config = new ClientConfig()
                .connectorProvider(new Apache5ConnectorProvider())
                .property(ClientProperties.CONNECT_TIMEOUT, 20000)
                .property(ClientProperties.FOLLOW_REDIRECTS, true)
                .register(new LoggingFeature(logger,
                        Level.INFO,
                        LoggingFeature.Verbosity.HEADERS_ONLY,
                        8192));
        client = ClientBuilder.newBuilder().withConfig(config).build();
    }

     public OBSHttp(URI apiURI) {
        this();
        target = client.target(apiURI);
    }

    public URI getApiUri() {
        return target.getUri();
    }

    public void setApiUri(URI apiURI) {
        target = client.target(apiURI);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    private void handleErrors(final Response response) {
        switch (response.getStatusInfo().getFamily()) {
            case CLIENT_ERROR:
                throw new ClientErrorException(response);
            case SERVER_ERROR:
                throw new ServerErrorException(response);
        }
    }

    public void authenticate() {
        HttpAuthenticationFeature authenticationFeature = HttpAuthenticationFeature.basicBuilder().build();
        target.register(authenticationFeature);

        try (Response response = target.request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .property(HTTP_AUTHENTICATION_BASIC_USERNAME, username)
                .property(HTTP_AUTHENTICATION_BASIC_PASSWORD, password)
                .get()) {
            authenticated = (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL);

            handleErrors(response);
        }
    }

    public OBSStatus branchPackage(String prj, String pkg) {
        Response response = target
                .path(String.format("/source/%s/%s", prj, pkg))
                .queryParam("cmd", "branch")
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .post(Entity.text(""));
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSStatus.class);
    }

    public OBSRevision linkPackage(String sourceProject, String sourcePackage,
            String targetProject) {
        OBSRevision revision = null;
        OBSPackage pkg = getPackageMetaConfig(sourceProject, sourcePackage);
        OBSProject project = new OBSProject();
        project.setName(targetProject);
        pkg.setProject(project);

        OBSStatus status = createPackage(pkg);
        OBSLink link;
        if (status.getCode().equals("ok")) {
            link = new OBSLink();
            link.setPrj(sourceProject);
            link.setPkg(sourcePackage);

            Response response = target
                    .path(String.format("/source/%s/%s/_link",
                            targetProject, sourcePackage))
                    .request()
                    .header("User-Agent", UserAgent.FULL)
                    .accept(MediaType.APPLICATION_XML_TYPE)
                    .put(Entity.xml(link));
            handleErrors(response);

            response.bufferEntity();
            revision = response.readEntity(OBSRevision.class);
        }
        return revision;
    }

    public OBSRevision copyPackage(String sourceProject, String sourcePackage,
            String targetProject, String targetPackage, String comments) {
        Response response = target
                .path(String.format("/source/%s/%s", targetProject, targetPackage))
                .queryParam("cmd", "copy")
                .queryParam("oproject", sourceProject)
                .queryParam("opackage", sourcePackage)
                .queryParam("comment", comments)
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .post(Entity.text(""));
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSRevision.class);
    }

    public OBSStatus createProject(OBSProject prj) {
        Response response = target
                .path(String.format("/source/%s/_meta",
                        prj.getName()))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .put(Entity.xml(prj));
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSStatus.class);
    }

    public OBSStatus createPackage(OBSPackage pkg) {
        Response response = target
                .path(String.format("/source/%s/%s/_meta",
                        pkg.getProject(), pkg.getName()))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .put(Entity.xml(pkg));
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSStatus.class);
    }

    public OBSRequest createRequest(OBSRequest request) {
        Response response = target
                .path("/request")
                .queryParam("cmd", "create")
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .post(Entity.xml(request));
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSRequest.class);
    }

    public OBSRevision uploadFile(String prj, String pkg, File file) {
        Response response = target
                .path(String.format("/source/%s/%s/%s", prj, pkg, file.getName()))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .put(Entity.entity(file, MediaType.APPLICATION_XML_TYPE));
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSRevision.class);
    }

    public InputStream downloadFile(String prj, String pkg, String fileName) {
        Response response = target
                .path(String.format("/source/%s/%s/%s", prj, pkg, fileName))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(InputStream.class);
    }

    public OBSDirectory getProjects() {
        Response response = target
                .path("/source")
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSDirectory.class);
    }

    public OBSDirectory getPackages(String project) {
        Response response = target
                .path("/source/" + project)
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSDirectory.class);
    }

    public OBSLink getLink(String prj, String pkg) {
        Response response = target
                .path(String.format("/source/%s/%s/_link", prj, pkg))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSLink.class);
    }

    public OBSPerson getPerson() {
        Response response = target
                .path("/person/" + username)
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSPerson.class);
    }

    public OBSStatus updatePerson(OBSPerson person) {
        Response response = target
                .path("/person/" + username)
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .put(Entity.xml(person));
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSStatus.class);
    }

    public OBSProject getProjectMetaConfig(String prj) {
        Response response = target
                .path(String.format("/source/%s/_meta", prj))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSProject.class);
    }

    public OBSPackage getPackageMetaConfig(String prj, String pkg) {
        Response response = target
                .path(String.format("/source/%s/%s/_meta", prj, pkg))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSPackage.class);
    }

    public OBSStatus getBuildStatus(String project, String repository,
            String architecture, String build) {
        Response response = target
                .path(String.format("/build/%s/%s/%s/%s/_status",
                        project, repository, architecture, build))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSStatus.class);
    }

    public OBSResultList getPackageResults(String prj, String pkg) {
        Response response = target
                .path(String.format("/build/%s/_result", prj))
                .queryParam("package", pkg)
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSResultList.class);
    }

    public OBSResultList getProjectResults(String prj) {
        Response response = target
                .path(String.format("/build/%s/_result", prj))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSResultList.class);
    }

    public OBSRevisionList getRevisions(String prj, String pkg) {
        Response response = target
                .path(String.format("/source/%s/%s/_history", prj, pkg))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSRevisionList.class);
    }

    public OBSRevisionList getLatestRevision(String prj, String pkg) {
        Response response = target
                .path(String.format("/source/%s/%s/_history", prj, pkg))
                .queryParam("limit", "1")
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSRevisionList.class);
    }

    private OBSCollection getRequests(RequestType type) {

        String states;
        String roles;

        switch (type) {
            case Incoming -> {
                states = "new";
                roles = "maintainer";
            }
            case Outgoing -> {
                states = "new,review";
                roles = "creator";
            }
            case Declined -> {
                states = "declined";
                roles = "creator";
            }
            default -> throw new IllegalArgumentException("Unknown RequestType!");
        }

        Response response = target
                .path("/request")
                .queryParam("view", "collection")
                .queryParam("states", states)
                .queryParam("roles", roles)
                .queryParam("user", getUsername())
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSCollection.class);
    }

    public OBSCollection getIncomingRequests() {
        return getRequests(RequestType.Incoming);
    }

    public OBSCollection getOutgoingRequests() {
        return getRequests(RequestType.Outgoing);
    }

    public OBSCollection getDeclinedRequests() {
        return getRequests(RequestType.Declined);
    }

    public OBSCollection getProjectRequests(String project) {
        Response response = target
                .path("/request")
                .queryParam("view", "collection")
                .queryParam("types", "submit,delete,add_role,change_devel,maintenance_incident,maintenance_release,release")
                .queryParam("states", "new,review")
                .queryParam("project", project)
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSCollection.class);
    }

    public OBSCollection getPackageRequests(String prj, String pkg) {
        Response response = target
                .path("/request")
                .queryParam("view", "collection")
                .queryParam("types", "submit,delete,add_role,change_devel,maintenance_incident,maintenance_release,release")
                .queryParam("states", "new,review")
                .queryParam("project", prj)
                .queryParam("package", pkg)
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSCollection.class);
    }

    public OBSStatus changeRequest(String id, String comments, String newState) {
        Response response = target
                .path("/request/" + id)
                .queryParam("cmd", "changestate")
                .queryParam("newstate", newState)
                .request()
                .header("User-Agent", UserAgent.FULL)
                .post(Entity.text(comments));
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSStatus.class);
    }

    public String getRequestDiff(String id) {
        Response response = target
                .path("/request/" + id)
                .queryParam("cmd", "diff")
                .request()
                .header("User-Agent", UserAgent.FULL)
                .post(Entity.text(""));
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(String.class);
    }

    public String getBuildLog(String prj, String repository, String arch, String pkg) {
        Response response = target
                .path(String.format("/build/%s/%s/%s/%s/_log",
                        prj, repository, arch, pkg))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(String.class);
    }

    public OBSDirectory getFiles(String prj, String pkg) {
        Response response = target
                .path(String.format("/source/%s/%s/", prj, pkg))
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSDirectory.class);
    }

    public OBSCollection packageSearch(String pkg) {
        Response response = target
                .path("/search/package")
                .queryParam("match", "starts_with(@name,'" + pkg + "')")
                .queryParam("limit", "20")
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSCollection.class);
    }

    public OBSDistributions getDistributions() {
        Response response = target
                .path("/distributions")
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSDistributions.class);
    }

    public OBSAbout getAbout() {
        Response response = target
                .path("/about")
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSAbout.class);
    }

    public OBSStatus delete(String resource) {
        Response response = target
                .path(resource)
                .request()
                .header("User-Agent", UserAgent.FULL)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .delete();
        handleErrors(response);

        response.bufferEntity();
        return response.readEntity(OBSStatus.class);
    }

}