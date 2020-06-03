/*
 * Copyright (C) 2018-2020 Javier Llorente <javier@opensuse.org>
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
package com.javierllorente.jobs.xml;

import com.javierllorente.jobs.entity.OBSPkgMetaConfig;
import com.javierllorente.jobs.entity.OBSPrjMetaConfig;
import com.javierllorente.jobs.entity.OBSRepository;
import com.javierllorente.jobs.entity.OBSRequest;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author javier
 */
public class OBSXmlWriter {

    DocumentBuilderFactory factory;
    DocumentBuilder documentBuilder;

    public OBSXmlWriter() throws ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        documentBuilder = factory.newDocumentBuilder();
    }

    private Element createProjectElement(Document document, String project) {
        Element rootElement = document.createElement("project");
        document.appendChild(rootElement);
        rootElement.setAttribute("name", project);
        return rootElement;
    }

    private Element createPackageElement(Document document,
            String pkg, String project) {
        Element rootElement = document.createElement("package");
        document.appendChild(rootElement);
        rootElement.setAttribute("name", pkg);
        rootElement.setAttribute("project", project);
        return rootElement;
    }

    private void createTextNode(Document document, Element rootElement, String tag,
            String text) {
        Element element = document.createElement(tag);
        element.appendChild(document.createTextNode(text));
        rootElement.appendChild(element);
    }

    private void createUserRoles(Document document, Element rootElement, 
            Map<String, List<String>> userRoles, String type) {
        if (!userRoles.isEmpty()) {
            String tag = type.equals("userid") ? "person" : "group";
            List<String> users = new ArrayList<>(userRoles.keySet());
            String userAdded = null;
            for (String user : users) {
                List<String> roles = userRoles.get(user);
                if (!user.equals(userAdded)) {
                    for (String role : roles) {
                        Element personElement = document.createElement(tag);
                        personElement.setAttribute(type, user);
                        personElement.setAttribute("role", role);                        
                        rootElement.appendChild(personElement);
                    }
                    userAdded = user;
                }
            }
        }
    }

    public String createProjectMeta(OBSPrjMetaConfig prjMetaConfig) throws TransformerException {
        Document document = documentBuilder.newDocument();

        Element projectElement = createProjectElement(document, prjMetaConfig.getName());
        createTextNode(document, projectElement, "title", prjMetaConfig.getTitle());
        createTextNode(document, projectElement, "description", prjMetaConfig.getDescription());
        
        createUserRoles(document, projectElement, prjMetaConfig.getPersons(), "userid");
        createUserRoles(document, projectElement, prjMetaConfig.getGroups(), "groupid");
        
        createRepositoryFlags(document, prjMetaConfig.getBuildFlag(), "build");
        createRepositoryFlags(document, prjMetaConfig.getDebugInfoFlag(), "debuginfo");
        createRepositoryFlags(document, prjMetaConfig.getPublishFlag(), "publish");
        createRepositoryFlags(document, prjMetaConfig.getUseForBuildFlag(), "useforbuild");
        
        prjMetaConfig.getRepositories().forEach((repository) -> {
            createRepositoryElement(document, projectElement, repository);
        });

        return documentToString(document);
    }

    public String createPackageMeta(OBSPkgMetaConfig pkgMetaConfig) 
            throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element packageElement = createPackageElement(document, 
                pkgMetaConfig.getName(), pkgMetaConfig.getProject());
        
        createTextNode(document, packageElement, "title", pkgMetaConfig.getTitle());
        createTextNode(document, packageElement, "description", 
                pkgMetaConfig.getDescription());        
        
        createUserRoles(document, packageElement, pkgMetaConfig.getPersons(), "userid");
        createUserRoles(document, packageElement, pkgMetaConfig.getGroups(), "groupid");
        
        createRepositoryFlags(document, pkgMetaConfig.getBuildFlag(), "build");
        createRepositoryFlags(document, pkgMetaConfig.getDebugInfoFlag(), "debuginfo");
        createRepositoryFlags(document, pkgMetaConfig.getPublishFlag(), "publish");
        createRepositoryFlags(document, pkgMetaConfig.getUseForBuildFlag(), "useforbuild");
        
        createTextNode(document, packageElement, "url", pkgMetaConfig.getUrl().toString());

        return documentToString(document);
    }

    public String createRequest(OBSRequest request) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement("request");
        document.appendChild(rootElement);

        Element actionElement = createActionElement(document, rootElement,
                request.getActionType());
        createRequestPrjPkgElement(document, actionElement, "source",
                request.getSourceProject(), request.getSourcePackage());
        createRequestPrjPkgElement(document, actionElement, "target",
                request.getTargetProject(), request.getTargetPackage());

        if (request.getSourceUpdate() != null) {
            Element element = document.createElement("options");
            document.appendChild(element);
            createTextNode(document, actionElement, "sourceupdate", request.getSourceUpdate());
        }

        createDescriptionElement(document, rootElement, request.getDescription());

        return documentToString(document);
    }
    
    private void createRepositoryFlags(Document document, Map<String, Boolean> flag, 
            String type) {
        
        if (!flag.isEmpty()) {
            Element rootElement = document.createElement(type);
            
            for (String repository : flag.keySet()) {
                boolean enabled = flag.get(repository);
                String enabledStr = enabled ? "enable" : "disable";
                
                Element enableElement = document.createElement(enabledStr);
                rootElement.appendChild(enableElement);
                
                if (!repository.equals("all")) {
                    rootElement.setAttribute("repository", repository);
                }
            }
            document.appendChild(rootElement);            
        }
    }
    
    private void createRepositoryElement(Document document, Element rootElement, 
            OBSRepository repository) {
        Element repositoryElement = document.createElement("repository");
        rootElement.appendChild(repositoryElement);
        repositoryElement.setAttribute("name", repository.getName());

        Element pathElement = document.createElement("path");
        repositoryElement.appendChild(pathElement);
        pathElement.setAttribute("project", repository.getProject());
        pathElement.setAttribute("repository", repository.getRepository());

        for (String arch : repository.getArchs()) {
            Element archElement = document.createElement("arch");
            archElement.appendChild(document.createTextNode(arch));
            repositoryElement.appendChild(archElement);
        }
    }

    private Element createActionElement(Document document, Element rootElement,
            String type) {
        Element actionElement = document.createElement("action");
        actionElement.setAttribute("type", type);
        rootElement.appendChild(actionElement);
        return actionElement;
    }

    private void createRequestPrjPkgElement(Document document, Element rootElement,
            String type, String prj, String pkg) {
        Element prjPkgElement = document.createElement(type);
        prjPkgElement.setAttribute("project", prj);
        prjPkgElement.setAttribute("package", pkg);
        rootElement.appendChild(prjPkgElement);
    }

    private void createDescriptionElement(Document document, Element rootElement, String description) {
        createTextNode(document, rootElement, "description", description);
    }

    private String documentToString(Document document) throws TransformerException {
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

}
