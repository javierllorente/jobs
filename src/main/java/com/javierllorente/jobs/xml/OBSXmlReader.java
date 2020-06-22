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
package com.javierllorente.jobs.xml;

import com.javierllorente.jobs.entity.OBSAbout;
import com.javierllorente.jobs.entity.OBSDistribution;
import com.javierllorente.jobs.entity.OBSFile;
import com.javierllorente.jobs.entity.OBSLink;
import com.javierllorente.jobs.entity.OBSMetaConfig;
import com.javierllorente.jobs.entity.OBSPerson;
import com.javierllorente.jobs.entity.OBSPkgMetaConfig;
import com.javierllorente.jobs.entity.OBSPrjMetaConfig;
import com.javierllorente.jobs.entity.OBSRepository;
import com.javierllorente.jobs.entity.OBSRequest;
import com.javierllorente.jobs.entity.OBSResult;
import com.javierllorente.jobs.entity.OBSRevision;
import com.javierllorente.jobs.entity.OBSStatus;
import com.javierllorente.jobs.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author javier
 */
public class OBSXmlReader {

    private int requestCount;

    public OBSXmlReader() {
    }

    public OBSRequest parseCreateRequest(InputStream is) throws ParserConfigurationException,
            IOException, SAXException {
        NodeList nodeList = getNodeList(is);
        OBSRequest request = new OBSRequest();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseRequest(node, request);
        }
        return request;

    }

    public OBSStatus parseBranchPackage(String prj, String pkg, InputStream is) throws
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();
        status.setProject(prj);
        status.setPkg(pkg);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseDeleteProject(String project, InputStream is) throws
            SAXException, IOException, ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();
        status.setProject(project);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseDeletePackage(String project, String pkg, InputStream is)
            throws SAXException, IOException, ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();
        status.setProject(project);
        status.setPkg(pkg);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseDeleteFile(String project, String pkg, String file,
            InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();
        status.setProject(project);
        status.setPkg(pkg);
        status.setDetails(file);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseBuildStatus(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public OBSStatus parseChangeRequestState(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {
        NodeList nodeList = getNodeList(is);
        OBSStatus status = new OBSStatus();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseStatus(node, status);
        }

        return status;
    }

    public List<OBSRequest> parseRequests(InputStream is) throws SAXException,
            IOException, ParserConfigurationException {
        
        OBSRequest request = null;        
        List<OBSRequest> requests = new ArrayList<>();
        NodeList nodeList = getNodeList(is);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "collection":
                    String matches = getAttributeValue(node, "matches");
                    requestCount = Integer.parseInt(matches);
                    System.out.println("matches: " + matches);
                    break;
                case "request":
                    request = new OBSRequest();
                    requests.add(request);
                    break;
            }
            
            parseRequest(node, request);
            
        }
        return requests;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public List<String> parseList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {        
        List<String> list = null;
        NodeList nodeList = getNodeList(is);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeName()) {
                case "directory":
                    list = new ArrayList<>();
                    break;
                case "entry":
                    list.add(getAttributeValue(node, "name"));
                    break;
            }
        }
        return list;
    }
    
    public List<String> parseProjectList(String userHome, InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        List<String> list = new ArrayList<>();
        NodeList nodeList = getNodeList(is);
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
                    
            if (node.getNodeName().equals("entry")) {
                String entry = getAttributeValue(node, "name");
                if (!userHome.isEmpty()) {
                    if (entry.startsWith(userHome)) {
                        list.add(entry);
                    } else if (!entry.startsWith("home")) {
                        list.add(entry);
                    }
                } else {
                    list.add(entry);
                }                
            }
            
        }
        return list;
    }

    public OBSPrjMetaConfig parsePrjMetaConfig(InputStream is) throws
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSPrjMetaConfig prjMetaConfig = new OBSPrjMetaConfig();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("project")) {
                if (node.hasAttributes()) {
                    String name = getAttributeValue(node, "name");
                    prjMetaConfig.setName(name);
                }
            } else if (node.getNodeName().equals("repository")) {
                OBSRepository repository = parseRepository(node);
                prjMetaConfig.addRepository(repository);
            }

            parseMetaConfig(node, prjMetaConfig);

        }
        return prjMetaConfig;
    }

    public OBSPkgMetaConfig parsePkgMetaConfig(InputStream is) throws
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSPkgMetaConfig pkgMetaConfig = new OBSPkgMetaConfig();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            
            if (node.getNodeName().equals("package")) {
                if (node.hasAttributes()) {
                    String name = getAttributeValue(node, "name");
                    pkgMetaConfig.setName(name);
                    String project = getAttributeValue(node, "project");
                    pkgMetaConfig.setProject(project);
                }
            }
            
            parseMetaConfig(node, pkgMetaConfig);
            
            if (node.getNodeName().equals("url")) {
                pkgMetaConfig.setUrl(new URL(node.getTextContent()));
            }
        }
        return pkgMetaConfig;
    }
    
    public List<OBSDistribution> parseDistributions(InputStream is) throws 
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        List<OBSDistribution> distributions = new ArrayList<>();
        OBSDistribution distribution = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeName()) {
                case "distribution":
                    distribution = new OBSDistribution();
                    distributions.add(distribution);
                    if (node.hasAttributes()) {
                        distribution.setVendor(getAttributeValue(node, "vendor"));
                        distribution.setVersion(getAttributeValue(node, "version"));
                        distribution.setId(getAttributeValue(node, "id"));
                    }
                    break;
                case "name":
                    distribution.setName(node.getTextContent());
                    break;
                case "project":
                    distribution.setProject(node.getTextContent());
                    break;
                case "reponame":
                    distribution.setRepoName(node.getTextContent());
                    break;
                case "repository":
                    distribution.setRepository(node.getTextContent());
                    break;
                case "link":
                    distribution.setLink(new URL(node.getTextContent()));
                    break;
                case "icon":
                    if (node.hasAttributes()) {
                        distribution.addIcon(new URL(getAttributeValue(node, "url")));
                    }
                    break;
                case "architecture":
                    distribution.addArch(node.getTextContent());
                    break;
            }
        }

        return distributions;        
    }

    public List<OBSResult> parseResultList(InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        List<OBSResult> list = null;
        OBSResult result = null;
        NodeList nodeList = getNodeList(is);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeName()) {
                case "resultlist":
                    list = new ArrayList<>();
                    break;
                case "result":
                    result = new OBSResult();
                    if (node.hasAttributes()) {
                        NamedNodeMap attributes = node.getAttributes();
                        for (int j = 0; j < attributes.getLength(); j++) {
                            Attr attribute = (Attr) (attributes.item(j));
                            switch (attribute.getName()) {
                                case "project":
                                    result.setProject(attribute.getValue());
                                    System.out.println(attribute.getName() + ": " + attribute.getValue());
                                    break;
                                case "repository":
                                    result.setRepository(attribute.getValue());
                                    System.out.println(attribute.getName() + ": " + attribute.getValue());
                                    break;
                                case "arch":
                                    result.setArch(attribute.getValue());
                                    System.out.println(attribute.getName() + ": " + attribute.getValue());
                                    break;
                                case "code":
                                    result.setCode(attribute.getValue());
                                    System.out.println(attribute.getName() + ": " + attribute.getValue());
                                    break;
                                case "state":
                                    result.setState(attribute.getValue());
                                    System.out.println(attribute.getName() + ": " + attribute.getValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        list.add(result);
                    }
                    break;
            }

            if (result != null) {
                parseStatus(node, result.getStatus());
            }

        }
        return list;
    }

    public List<OBSFile> parseFileList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        List<OBSFile> list = null;
        NodeList nodeList = getNodeList(is);
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "directory":
                    list = new ArrayList<>();
                    break;
                case "entry":
                    if (node.hasAttributes()) {
                        OBSFile file = new OBSFile();
                        NamedNodeMap attributes = node.getAttributes();
                        for (int j = 0; j < attributes.getLength(); j++) {
                            Attr attribute = (Attr) (attributes.item(j));
                            switch (attribute.getName()) {
                                case "name":
                                    file.setName(attribute.getValue());
                                    break;
                                case "size":
                                    file.setSize(attribute.getValue());
                                    break;
                                case "mtime":
                                    file.setLastModified(Utils.unixDateToDate(
                                            attribute.getValue()));
                                    break;
                            }
                        }
                        list.add(file);
                    }
                    break;
            }
        }
        return list;
    }
    
    public OBSLink parseLink(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSLink link = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("link")) {
                link = new OBSLink();
                if (node.hasAttributes()) {
                    String prj = getAttributeValue(node, "project");
                    link.setProject(prj);
                    String pkg = getAttributeValue(node, "package");
                    link.setPkg(pkg);
                }
            }
        }
        return link;
    }
    
    public OBSRevision parseRevision(InputStream is) throws
            ParserConfigurationException, SAXException, IOException {

        NodeList nodeList = getNodeList(is);
        OBSRevision revision = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "revision":
                    revision = new OBSRevision();
                    if (node.hasAttributes()) {
                        String rev = getAttributeValue(node, "rev");
                        revision.setRev(Integer.parseInt(rev));
                    }
                    break;
                case "version":
                    revision.setVersion(node.getTextContent());
                    break;
                case "time":
                    revision.setTime(Utils.unixDateToDate(node.getTextContent()));
                    break;
                case "user":
                    revision.setUser(node.getTextContent());
                    break;
                case "comment":
                    revision.setComment(node.getTextContent());
                    break;
            }
        }

        return revision;
    }
    
    public OBSPerson parsePerson(InputStream is) throws
            ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSPerson person = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "person":
                    person = new OBSPerson();
                    break;
                case "login":
                    person.setLogin(node.getTextContent());
                    break;
                case "email":
                    person.setEmail(node.getTextContent());
                    break;
                case "realname":
                    person.setRealName(node.getTextContent());
                    break;
                case "state":
                    person.setState(node.getTextContent());
                    break;
                case "project":
                    if (node.hasAttributes()) {
                        person.addWatchItem(getAttributeValue(node, "name"));
                    }
                    break;
            }
        }

        return person;
    }
    
    public OBSAbout parseAbout(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        NodeList nodeList = getNodeList(is);
        OBSAbout about = null;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            switch (node.getNodeName()) {
                case "about":
                    about = new OBSAbout();
                    break;
                case "title":
                    about.setTitle(node.getTextContent());
                    break;
                case "description":
                    about.setDescription(node.getTextContent());
                    break;
                case "revision":
                    about.setRevision(node.getTextContent());
                    break;
                case "last_deployment":
                    about.setLastDeployment(node.getTextContent());
                    break;
            }
        }

        return about;
    }

    private NodeList getNodeList(InputStream is) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        return document.getElementsByTagName("*");
    }

    private void parseStatus(Node node, OBSStatus status) {
        switch (node.getNodeName()) {
            case "status":
                if (node.hasAttributes()) {
                    NamedNodeMap attributes = node.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Attr attribute = (Attr) (attributes.item(j));
                        
                        switch (attribute.getName()) {
                            case "package":
                                status.setPkg(attribute.getValue());
                                System.out.println("package: " + attribute.getValue());
                                break;
                            case "code":
                                status.setCode(attribute.getValue());
                                System.out.println("code: " + attribute.getValue());
                                break;
                        }
                    }
                }
                break;
            case "summary":
                status.setSummary(node.getTextContent());
                System.out.println("summary: " + node.getTextContent());
                break;
            case "details":
                status.setDetails(node.getTextContent());
                break;
        }
    }

    private void parseRequest(Node node, OBSRequest request) {
        if (node.hasAttributes()) {

            switch (node.getNodeName()) {
                case "request":
                    System.out.println("Request");
                    String id = getAttributeValue(node, "id");
                    request.setId(id);
                    System.out.println("id: " + request.getId());
                    break;
                case "action":
                    String actionType = getAttributeValue(node, "type");
                    request.setActionType(actionType);
                    break;
                case "source":
                    String sourceProject = getAttributeValue(node, "project");
                    String sourcePackage = getAttributeValue(node, "package");
                    request.setSourceProject(sourceProject);
                    request.setSourcePackage(sourcePackage);
                    System.out.println("source project: " + request.getSourceProject());
                    break;
                case "target":
                    String targetProject = getAttributeValue(node, "project");
                    String targetPackage = getAttributeValue(node, "package");
                    request.setTargetProject(targetProject);
                    request.setTargetPackage(targetPackage);
                    System.out.println("target project: " + request.getSourceProject());
                    break;
                case "state":
                    String state = getAttributeValue(node, "name");
                    String requester = getAttributeValue(node, "who");
                    request.setState(state);
                    request.setRequester(requester);
                    request.setDate(Utils.iso8601DateToDate(getAttributeValue(node, "when")));
                    break;
            }

        } else {

            switch (node.getNodeName()) {
                case "sourceupdate":
                    String sourceUpdate = node.getTextContent();
                    request.setSourceUpdate(sourceUpdate);
                    System.out.println("Source update: " + request.getSourceUpdate());
                    break;
                case "description":
                    if (node.getParentNode().getNodeName().equals("request")) {
                        String description = node.getTextContent();
                        request.setDescription(description);
                        System.out.println("Description: " + request.getDescription());
                    }
                    break;
            }

        }

    }

    private String getAttributeValue(Node node, String item) {
        Node childNode = node.getAttributes().getNamedItem(item);
        if (childNode == null) {
            throw new IllegalArgumentException("Attribute " + item + " not found!");
        }
        return childNode.getNodeValue();
    }

    private Map<String, Boolean> parseRepositoryFlags(Node node) {
        Map<String, Boolean> flagHash = new HashMap<>();

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.hasAttributes()) {
                NamedNodeMap attributes = childNode.getAttributes();
                for (int j = 0; j < attributes.getLength(); j++) {
                    Attr attribute = (Attr) (attributes.item(j));

                    switch (childNode.getNodeName()) {
                        case "enable":
                            if (attributes.getNamedItem("repository") == null) {
                                flagHash.put("all", true);
                            } else {
                                flagHash.put(attribute.getValue(), true);
                            }
                            break;
                        case "disable":
                            if (attributes.getNamedItem("repository") == null) {
                                flagHash.put("all", false);
                            } else {
                                flagHash.put(attribute.getValue(), false);
                            }
                            break;
                    }
                }
            }
        }
        return flagHash;
    }

    private void parseMetaConfig(Node node, OBSMetaConfig metaConfig) throws
            ParserConfigurationException, SAXException, IOException {

        switch (node.getNodeName()) {
            case "title":
                metaConfig.setTitle(node.getTextContent());
                break;
            case "description":
                metaConfig.setDescription(node.getTextContent());
                break;
            case "person":
                if (node.hasAttributes()) {
                    String userId = getAttributeValue(node, "userid");
                    String role = getAttributeValue(node, "role");
                    metaConfig.putPerson(userId, role);
                }
                break;
            case "group":
                if (node.hasAttributes()) {
                    String groupId = getAttributeValue(node, "groupid");
                    String role = getAttributeValue(node, "role");
                    metaConfig.putGroup(groupId, role);
                }
                break;
            case "build":
                metaConfig.setBuildFlag(parseRepositoryFlags(node));
                break;
            case "publish":
                metaConfig.setPublishFlag(parseRepositoryFlags(node));
                break;
            case "useforbuild":
                metaConfig.setUseForBuildFlag(parseRepositoryFlags(node));
                break;
            case "debuginfo":
                metaConfig.setDebugInfoFlag(parseRepositoryFlags(node));
                break;
        }

    }

    private OBSRepository parseRepository(Node node) {
        OBSRepository repository = new OBSRepository();
        if (node.hasAttributes()) {
            String name = getAttributeValue(node, "name");
            repository.setName(name);
        }

        NodeList childNodeList = node.getChildNodes();
        for (int k = 0; k < childNodeList.getLength(); k++) {
            Node childNode = childNodeList.item(k);
              
            switch (childNode.getNodeName()) {
                case "path":
                    if (childNode.hasAttributes()) {
                        String project = getAttributeValue(childNode, "project");
                        repository.setProject(project);
                        String repo = getAttributeValue(childNode, "repository");
                        repository.setRepository(repo);
                    }
                    break;
                case "arch":
                    repository.addArch(childNode.getTextContent());
                    break;
            }
        }

        return repository;
    }
    
}
